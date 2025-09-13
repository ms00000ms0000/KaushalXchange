package com.example.kaushalxchange

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MyConnectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var btnRequests: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var userList: MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_connection)

        recyclerView = findViewById(R.id.recyclerViewConnections)
        btnRequests = findViewById(R.id.btnRequests)

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        userList = mutableListOf()
        adapter = UserAdapter(userList, this) { user ->
            removeConnection(user.uid)
            userList.remove(user)
            adapter.notifyDataSetChanged()
        }
        recyclerView.adapter = adapter

        btnRequests.setOnClickListener {
            startActivity(Intent(this, ChatRequestActivity::class.java))
        }

        loadConnections()
        listenForNewMessages()
    }

    // Load existing connections from Firestore
    private fun loadConnections() {
        val currentUserId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(currentUserId)
            .collection("connections")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    userList.clear()
                    for (doc in snapshot.documents) {
                        val name = doc.getString("name") ?: continue
                        val uid = doc.getString("uid") ?: continue
                        userList.add(User(name, R.drawable.profile, uid = uid))
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    // Remove a connection document from Firestore
    private fun removeConnection(userUid: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(currentUserId)
            .collection("connections")
            .document(userUid)
            .delete()
    }

    // Add a connection document to Firestore if it does not exist
    private fun addConnection(userName: String, userUid: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        val connectionData = hashMapOf(
            "name" to userName,
            "uid" to userUid
        )

        firestore.collection("users")
            .document(currentUserId)
            .collection("connections")
            .document(userUid)
            .set(connectionData)
    }

    // Listen for any new messages and add the sender to connections dynamically
    private fun listenForNewMessages() {
        val currentUserUid = auth.currentUser?.uid ?: return

        firestore.collection("Chats")
            .addSnapshotListener { chatsSnapshot, _ ->
                if (chatsSnapshot != null) {
                    for (chatDoc in chatsSnapshot.documents) {
                        val chatId = chatDoc.id
                        if (chatId.contains(currentUserUid)) {
                            firestore.collection("Chats")
                                .document(chatId)
                                .collection("messages")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(1)
                                .addSnapshotListener { msgsSnapshot, _ ->
                                    if (msgsSnapshot != null && msgsSnapshot.documents.isNotEmpty()) {
                                        val lastMsg = msgsSnapshot.documents[0]
                                        val sender = lastMsg.getString("sender") ?: return@addSnapshotListener
                                        val senderUid = lastMsg.getString("senderUid") ?: return@addSnapshotListener
                                        if (senderUid != currentUserUid) {
                                            // Always add sender to MyConnections (re-add if deleted)
                                            addConnection(sender, senderUid)
                                        }
                                    }
                                }
                        }
                    }
                }
            }
    }
}
