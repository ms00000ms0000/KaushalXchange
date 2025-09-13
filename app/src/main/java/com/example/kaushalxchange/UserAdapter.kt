package com.example.kaushalxchange

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserAdapter(
    private val userList: MutableList<User>,
    private val context: Context,
    private val onRemoveClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("KaushalXChangePrefs", Context.MODE_PRIVATE)

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser?.displayName ?: "me"

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val chatIcon: ImageView = itemView.findViewById(R.id.chatIcon)
        val removeIcon: ImageView = itemView.findViewById(R.id.removeIcon) // new cross icon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_connection, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userName.text = user.name
        holder.profileImage.setImageResource(user.profileImage)

        // Open chat
        holder.chatIcon.setOnClickListener {
            val accepted = prefs.getStringSet("ChatAccepted", emptySet()) ?: emptySet()
            if (accepted.contains(user.name)) {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("otherUser", user.name)
                context.startActivity(intent)
            } else {
                val reqSet = prefs.getStringSet("ChatRequests", mutableSetOf())?.toMutableSet()
                    ?: mutableSetOf()
                reqSet.add(user.name)
                prefs.edit().putStringSet("ChatRequests", reqSet).apply()

                firestore.collection("ChatRequests")
                    .document(user.name)
                    .collection("received")
                    .document(currentUser)
                    .set(mapOf("from" to currentUser))
            }
        }

        // Remove connection
        holder.removeIcon.setOnClickListener {
            onRemoveClick(user)
        }
    }

    override fun getItemCount(): Int = userList.size
}
