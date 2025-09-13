package com.example.kaushalxchange

import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val messages: List<ChatMessage>, private val currentUser: String) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
        val fileLayout: LinearLayout = view.findViewById(R.id.fileLayout)
        val fileNameText: TextView = view.findViewById(R.id.fileNameText)
        val imagePreview: ImageView = view.findViewById(R.id.imagePreview)
        val videoPreview: ImageView = view.findViewById(R.id.videoPreview)
        val rootLayout: LinearLayout = view.findViewById(R.id.rootLayout)
        val timestampText: TextView = view.findViewById(R.id.timestampText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val msg = messages[position]

        // Reset all views
        holder.messageText.visibility = View.GONE
        holder.fileLayout.visibility = View.GONE
        holder.imagePreview.visibility = View.GONE
        holder.videoPreview.visibility = View.GONE

        // Align left or right with WhatsApp-style bubbles
        val params = holder.rootLayout.layoutParams as ViewGroup.MarginLayoutParams

        if (msg.sender == currentUser) {
            // Sent → right side
            (holder.rootLayout.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 60
            (holder.rootLayout.layoutParams as ViewGroup.MarginLayoutParams).marginEnd = 8
            holder.rootLayout.setBackgroundResource(R.drawable.bg_chat_sent)
            holder.rootLayout.gravity = Gravity.END
            holder.timestampText.gravity = Gravity.END
        } else {
            // Received → left side
            (holder.rootLayout.layoutParams as ViewGroup.MarginLayoutParams).marginStart = 8
            (holder.rootLayout.layoutParams as ViewGroup.MarginLayoutParams).marginEnd = 60
            holder.rootLayout.setBackgroundResource(R.drawable.bg_chat_received)
            holder.rootLayout.gravity = Gravity.START
            holder.timestampText.gravity = Gravity.START
        }

        holder.rootLayout.layoutParams = params

        // Handle message type
        when (msg.type) {
            "text" -> {
                holder.messageText.visibility = View.VISIBLE
                holder.messageText.text = msg.message
            }

            "file" -> {
                holder.fileLayout.visibility = View.VISIBLE
                holder.fileNameText.text = msg.fileName ?: "File"
                holder.fileLayout.setOnClickListener {
                    msg.fileUrl?.let { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        holder.itemView.context.startActivity(intent)
                    }
                }
            }

            "image" -> {
                holder.imagePreview.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(msg.fileUrl)
                    .into(holder.imagePreview)

                holder.imagePreview.setOnClickListener {
                    msg.fileUrl?.let { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        holder.itemView.context.startActivity(intent)
                    }
                }
            }

            "video" -> {
                holder.videoPreview.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(msg.fileUrl) // video thumbnail
                    .into(holder.videoPreview)

                holder.videoPreview.setOnClickListener {
                    msg.fileUrl?.let { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        intent.setDataAndType(Uri.parse(url), "video/*")
                        holder.itemView.context.startActivity(intent)
                    }
                }
            }
        }

        // Format & show timestamp
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeString = sdf.format(Date(msg.timestamp))
        holder.timestampText.text = timeString
    }

    override fun getItemCount(): Int = messages.size
}
