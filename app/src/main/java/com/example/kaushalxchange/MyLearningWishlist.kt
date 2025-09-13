package com.example.kaushalxchange

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyLearningWishlist : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var listView: ListView
    private lateinit var listAdapter: ListAdapter
    private val dataArrayList = ArrayList<ListData?>()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_learning_wishlist)

        prefs = getSharedPreferences("LikedSkills", MODE_PRIVATE)

        //  Clear preferences if user is new (UID changed)
        val uid = auth.currentUser?.uid
        if (uid != null && prefs.getString("lastUid", "") != uid) {
            prefs.edit().clear().putString("lastUid", uid).apply()
        }

        listView = findViewById(R.id.listLikedSkills)

        loadLikedSkills()

        listAdapter = ListAdapter(this, dataArrayList)
        listView.adapter = listAdapter

        // Sync immediately so firestore always has latest data
        syncWishlistToFirestore()
    }

    override fun onResume() {
        super.onResume()
        loadLikedSkills()
        listAdapter.notifyDataSetChanged()
        syncWishlistToFirestore()
    }

    private fun loadLikedSkills() {
        dataArrayList.clear()

        // Names & Images available in the app
        val nameList = arrayOf(
            "Python", "Java", "C", "HTML", "CSS",
            "JavaScript", "MS Word", "MS Excel", "Powerpoint", "Canva"
        )
        val imageList = intArrayOf(
            R.drawable.python,
            R.drawable.java,
            R.drawable.c,
            R.drawable.html,
            R.drawable.css,
            R.drawable.javascript,
            R.drawable.word,
            R.drawable.excel,
            R.drawable.powerpoint,
            R.drawable.canva
        )

        // Only show selected skills
        for (i in nameList.indices) {
            if (prefs.getBoolean(nameList[i], false)) {
                dataArrayList.add(ListData(nameList[i], imageList[i]))
            }
        }
    }

    private fun syncWishlistToFirestore() {
        val uid = auth.currentUser?.uid ?: return
        val displayName = auth.currentUser?.displayName ?: "Unknown"

        val nameList = arrayOf(
            "Python", "Java", "C", "HTML", "CSS",
            "JavaScript", "MS Word", "MS Excel", "Powerpoint", "Canva"
        )
        val wishlist = mutableListOf<String>()
        for (name in nameList) {
            if (prefs.getBoolean(name, false)) {
                wishlist.add(name)
            }
        }

        val teachPrefs = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        val teachSkills = teachPrefs.getStringSet("SkillsICanTeach", emptySet())?.toList() ?: emptyList()

        val data = mapOf(
            "uid" to uid,
            "displayName" to displayName,
            "skills_want_to_learn" to wishlist,
            "skills_can_teach" to teachSkills
        )

        // Merge instead of overwrite → avoids missing fields
        firestore.collection("users").document(uid).set(data, com.google.firebase.firestore.SetOptions.merge())
    }
}
