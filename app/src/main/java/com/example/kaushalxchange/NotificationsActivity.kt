package com.example.kaushalxchange

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kaushalxchange.databinding.ActivityNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class NotificationsActivity : AppCompatActivity(), RequestAdapter.OnDecisionClickListener {

    private lateinit var binding: ActivityNotificationsBinding
    private val requests = mutableListOf<Pair<String, MatchRequest>>()
    private lateinit var adapter: RequestAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var listener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        adapter = RequestAdapter(requests, this)
        binding.requestsRecycler.layoutManager = LinearLayoutManager(this)
        binding.requestsRecycler.adapter = adapter

        binding.btnClearNotifications.setOnClickListener {
            val myUid = auth.currentUser?.uid ?: return@setOnClickListener
            db.collection("users")
                .document(myUid)
                .collection("requests")
                .get()
                .addOnSuccessListener { snapshot ->
                    val batch = db.batch()
                    snapshot.documents.forEach { doc -> batch.delete(doc.reference) }
                    batch.commit().addOnSuccessListener {
                        requests.clear()
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "All notifications cleared", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        val myUid = auth.currentUser?.uid ?: return
        listener = db.collection("users")
            .document(myUid)
            .collection("requests")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snaps, _ ->
                if (snaps == null) {
                    requests.clear()
                    adapter.notifyDataSetChanged()
                    return@addSnapshotListener
                }
                val newList = mutableListOf<Pair<String, MatchRequest>>()
                snaps.documents.forEach { doc ->
                    val req = doc.toObject(MatchRequest::class.java) ?: return@forEach
                    newList.add(doc.id to req)
                }
                requests.clear()
                requests.addAll(newList)
                adapter.notifyDataSetChanged()
            }
    }

    override fun onStop() {
        super.onStop()
        listener?.remove()
        listener = null
    }

    override fun onDecision(requestId: String, request: MatchRequest, accepted: Boolean) {
        val myUid = auth.currentUser?.uid ?: return
        val myName = auth.currentUser?.displayName ?: "Me"
        val newStatus = if (accepted) "accepted" else "rejected"

        db.collection("users")
            .document(myUid)
            .collection("requests")
            .document(requestId)
            .update("status", newStatus)
            .addOnSuccessListener {

                val idx = requests.indexOfFirst { it.first == requestId }
                if (idx != -1) {
                    requests[idx] = requestId to request.copy(status = newStatus)
                    adapter.notifyItemChanged(idx)
                }

                if (accepted) {
                    val prefs = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

                    val serialized = buildString {
                        append(request.fromName)
                        append("||")
                        append(request.skills_want_to_learn.joinToString(","))
                        append("||")
                        append(request.skills_can_teach.joinToString(","))
                    }

                    // ---- Add ActiveCourse locally ----
                    val activeSet = prefs.getStringSet("ActiveCourses", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                    activeSet.add(serialized)
                    prefs.edit().putStringSet("ActiveCourses", activeSet).apply()

                    // ---- Firestore: Add ActiveCourse for both sender and receiver ----
                    val batch = db.batch()

                    val myActiveRef = db.collection("users")
                        .document(myUid)
                        .collection("ActiveCourses")
                        .document(request.fromUid)
                    batch.set(myActiveRef, mapOf(
                        "studentName" to request.fromName,
                        "skills_want_to_learn" to request.skills_want_to_learn,
                        "skills_can_teach" to request.skills_can_teach
                    ))

                    val senderActiveRef = db.collection("users")
                        .document(request.fromUid)
                        .collection("ActiveCourses")
                        .document(myUid)
                    batch.set(senderActiveRef, mapOf(
                        "studentName" to myName,
                        "skills_want_to_learn" to request.skills_can_teach,
                        "skills_can_teach" to request.skills_want_to_learn
                    ))

                    // Also sync My Connections like before
                    val senderConnRef = db.collection("users")
                        .document(request.fromUid)
                        .collection("connections")
                        .document(myUid)
                    batch.set(senderConnRef, mapOf("name" to myName, "uid" to myUid))

                    val myConnRef = db.collection("users")
                        .document(myUid)
                        .collection("connections")
                        .document(request.fromUid)
                    batch.set(myConnRef, mapOf("name" to request.fromName, "uid" to request.fromUid))

                    batch.commit().addOnSuccessListener {
                        Toast.makeText(this, "Request accepted. Active Course & Connections synced for both users.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Request rejected", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
