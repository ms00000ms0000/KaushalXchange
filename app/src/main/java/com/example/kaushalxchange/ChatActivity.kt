package com.example.kaushalxchange

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var attachButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private var fileUri: Uri? = null
    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var chatId: String
    private lateinit var currentUser: String
    private lateinit var otherUser: String
    private lateinit var currentUserUid: String

    private val openDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val uri = data?.data
                if (uri != null) handlePickedFile(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageInput = findViewById(R.id.edtMessage)
        sendButton = findViewById(R.id.btnSend)
        attachButton = findViewById(R.id.btnFile)
        recyclerView = findViewById(R.id.recyclerChat)

        prefs = getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        currentUser = FirebaseAuth.getInstance().currentUser?.displayName ?: "me"
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        otherUser = intent.getStringExtra("otherUser") ?: "user"

        chatId = listOf(currentUser, otherUser).sorted().joinToString("_")

        adapter = ChatAdapter(messages, currentUser)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadLocalMessages()
        listenToFirestore()

        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isNotEmpty()) {
                sendTextMessage(text)
            }
        }

        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendButton.performClick()
                true
            } else false
        }

        attachButton.setOnClickListener { pickFile() }
    }

    private fun sendTextMessage(text: String) {
        val message = ChatMessage(
            sender = currentUser,
            message = text,
            type = "text",
            timestamp = System.currentTimeMillis()
        )

        saveLocalMessage(message)

        val msgMap = hashMapOf(
            "sender" to message.sender,
            "senderUid" to currentUserUid,
            "message" to message.message,
            "type" to message.type,
            "fileName" to "",
            "fileUrl" to "",
            "timestamp" to message.timestamp
        )

        firestore.collection("Chats")
            .document(chatId)
            .collection("messages")
            .add(msgMap)

        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
        messageInput.text.clear()
    }

    private fun loadLocalMessages() {
        val savedMessages = prefs.getStringSet("chat_$chatId", emptySet()) ?: emptySet()
        messages.clear()
        for (msg in savedMessages) {
            val parts = msg.split("||")
            if (parts.size >= 6) {
                messages.add(
                    ChatMessage(
                        sender = parts[0],
                        message = if (parts[1].isNotEmpty()) parts[1] else null,
                        type = parts[2],
                        fileName = if (parts[3].isNotEmpty()) parts[3] else null,
                        fileUrl = if (parts[4].isNotEmpty()) parts[4] else null,
                        timestamp = parts[5].toLongOrNull() ?: System.currentTimeMillis()
                    )
                )
            }
        }
        adapter.notifyDataSetChanged()
        if (messages.isNotEmpty()) recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun saveLocalMessage(message: ChatMessage) {
        val savedMessages =
            prefs.getStringSet("chat_$chatId", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val entry = listOf(
            message.sender,
            message.message ?: "",
            message.type,
            message.fileName ?: "",
            message.fileUrl ?: "",
            message.timestamp.toString()
        ).joinToString("||")
        savedMessages.add(entry)
        prefs.edit().putStringSet("chat_$chatId", savedMessages).apply()
    }

    private fun listenToFirestore() {
        firestore.collection("Chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    messages.clear()
                    for (doc in snapshots.documents) {
                        val msg = doc.toObject(ChatMessage::class.java)
                        if (msg != null) messages.add(msg)
                    }
                    adapter.notifyDataSetChanged()
                    if (messages.isNotEmpty()) recyclerView.scrollToPosition(messages.size - 1)
                }
            }
    }

    private fun pickFile() {
        val mimeTypes = arrayOf(
            "image/*", "video/*", "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        )
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        openDocumentLauncher.launch(intent)
    }

    private fun handlePickedFile(uri: Uri) {
        val mime = contentResolver.getType(uri) ?: ""
        val filename = queryFileName(contentResolver, uri) ?: uri.lastPathSegment ?: "file"
        val type = when {
            mime.startsWith("image") -> "image"
            mime.startsWith("video") -> "video"
            else -> "file"
        }
        uploadFileToStorage(uri, filename, type)
    }

    private fun uploadFileToStorage(uri: Uri, filename: String, type: String) {
        val fileNameTimestamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val ref = storage.reference.child("chat_uploads/$chatId/${fileNameTimestamp}_$filename")
        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                val msg = ChatMessage(
                    sender = currentUser,
                    message = if (type == "file" || type == "image" || type == "video") "Sent a file" else null,
                    type = type,
                    fileName = filename,
                    fileUrl = downloadUrl.toString(),
                    timestamp = System.currentTimeMillis()
                )
                // include senderUid
                val msgMap = hashMapOf(
                    "sender" to msg.sender,
                    "senderUid" to currentUserUid,
                    "message" to msg.message,
                    "type" to msg.type,
                    "fileName" to msg.fileName,
                    "fileUrl" to msg.fileUrl,
                    "timestamp" to msg.timestamp
                )
                firestore.collection("Chats").document(chatId)
                    .collection("messages")
                    .add(msgMap)
                saveLocalMessage(msg)
                messages.add(msg)
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun queryFileName(resolver: ContentResolver, uri: Uri): String? {
        var name: String? = null
        val cursor = resolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) name = it.getString(idx)
            }
        }
        return name
    }
}
