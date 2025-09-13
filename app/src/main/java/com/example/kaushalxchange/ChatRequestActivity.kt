package com.example.kaushalxchange

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatRequestActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var listView: ListView
    private lateinit var btnAccept: Button
    private var selectedUser: String? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser?.displayName ?: "me"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_request)

        prefs = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        listView = findViewById(R.id.listViewRequests)
        btnAccept = findViewById(R.id.btnAccept)

        val requests = prefs.getStringSet("ChatRequests", emptySet())?.toMutableList() ?: mutableListOf()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, requests)
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedUser = requests[position]
        }

        btnAccept.setOnClickListener {
            selectedUser?.let {
                val accepted = prefs.getStringSet("ChatAccepted", mutableSetOf())?.toMutableSet()
                    ?: mutableSetOf()
                accepted.add(it)
                prefs.edit().putStringSet("ChatAccepted", accepted).apply()

                requests.remove(it)
                prefs.edit().putStringSet("ChatRequests", requests.toSet()).apply()

                adapter.clear()
                adapter.addAll(requests)
                adapter.notifyDataSetChanged()

                // Save acceptance in Firestore (so both sides can chat)
                firestore.collection("ChatAccepted")
                    .document(it)
                    .collection("users")
                    .document(currentUser)
                    .set(mapOf("accepted" to true))

                firestore.collection("ChatAccepted")
                    .document(currentUser)
                    .collection("users")
                    .document(it)
                    .set(mapOf("accepted" to true))
            }
        }
    }
}
