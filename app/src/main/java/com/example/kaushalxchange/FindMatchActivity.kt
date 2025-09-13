package com.example.kaushalxchange

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kaushalxchange.databinding.ActivityFindMatchBinding
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

class FindMatchActivity : AppCompatActivity(), MatchAdapter.OnRequestClickListener {

    private lateinit var binding: ActivityFindMatchBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    private var skillsICanTeach: Set<String> = emptySet()
    private var returnedFromSearching = false
    private var skillToLearnAfterSearch = ""

    private var lastSelectedTeachSkills: List<String> = emptyList()
    private var myDisplayName: String = "Unknown"

    private val wishlistPrefsName = "LikedSkills"
    private val TAG = "FIND_MATCH_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        skillsICanTeach = sharedPreferences.getStringSet("SkillsICanTeach", emptySet()) ?: emptySet()
        if (skillsICanTeach.isEmpty()) {
            Toast.makeText(this, "No skills found in Skill I Can Teach", Toast.LENGTH_SHORT).show()
        }

        myDisplayName = auth.currentUser?.displayName ?: "Unknown"
        val myUid = auth.currentUser?.uid
        if (myUid != null) {
            db.collection("users").document(myUid).get()
                .addOnSuccessListener { snap ->
                    val profile = snap.toObject<UserProfile>()
                    if (!profile?.displayName.isNullOrBlank()) {
                        myDisplayName = profile?.displayName ?: myDisplayName
                        Log.d(TAG, "Loaded my displayName=$myDisplayName")
                    }
                }
        }

        loadLearnWishlistChips()
        loadTeachSkills()

        binding.matchesRecycler.layoutManager = LinearLayoutManager(this)

        // Always keep skills synced
        syncWishlistToFirestore()

        binding.findButton.setOnClickListener {
            val checkedId = binding.learnSkillsChipGroup.checkedChipId
            if (checkedId == -1) {
                Toast.makeText(this, "Please select a skill you want to learn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val chip = binding.learnSkillsChipGroup.findViewById<Chip>(checkedId)
            val skillToLearn = chip?.text?.toString() ?: ""
            if (skillToLearn.isEmpty()) {
                Toast.makeText(this, "Please select a skill you want to learn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedTeachSkills = mutableListOf<String>()
            for (i in 0 until binding.teachSkillsChipGroup.childCount) {
                val teachChip = binding.teachSkillsChipGroup.getChildAt(i) as Chip
                if (teachChip.isChecked) selectedTeachSkills.add(teachChip.text.toString())
            }
            if (selectedTeachSkills.isEmpty()) {
                Toast.makeText(this, "Please select at least one skill to teach", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lastSelectedTeachSkills = selectedTeachSkills
            skillToLearnAfterSearch = skillToLearn
            returnedFromSearching = true

            syncWishlistToFirestore()

            Log.d(TAG, "Starting search for skill=$skillToLearn teachSkills=$lastSelectedTeachSkills")

            val intent = Intent(this, SearchingActivity::class.java)
            intent.putExtra("skillToLearn", skillToLearn)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadLearnWishlistChips()
        if (returnedFromSearching) {
            returnedFromSearching = false
            performFirebaseMatching(skillToLearnAfterSearch)
        }
    }

    private fun loadTeachSkills() {
        binding.teachSkillsChipGroup.removeAllViews()
        for (skillName in skillsICanTeach) {
            val chip = Chip(this).apply {
                text = skillName
                isCheckable = true
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    binding.teachSkillsChipGroup.removeView(this)
                }
            }
            binding.teachSkillsChipGroup.addView(chip)
        }
        Log.d(TAG, "Teach skills loaded: $skillsICanTeach")
    }

    private fun loadLearnWishlistChips() {
        binding.learnSkillsChipGroup.removeAllViews()
        val prefs = getSharedPreferences(wishlistPrefsName, MODE_PRIVATE)
        val nameList = arrayOf("Python", "Java", "C", "HTML", "CSS", "JavaScript", "MS Word", "MS Excel", "Powerpoint", "Canva")

        for (name in nameList) {
            if (prefs.getBoolean(name, false)) {
                val chip = Chip(this).apply {
                    text = name
                    isCheckable = true
                }
                binding.learnSkillsChipGroup.addView(chip)
            }
        }
        Log.d(TAG, "Wishlist chips loaded.")
    }

    private fun syncWishlistToFirestore() {
        val uid = auth.currentUser?.uid ?: return
        val prefs = getSharedPreferences(wishlistPrefsName, MODE_PRIVATE)

        val nameList = arrayOf("Python", "Java", "C", "HTML", "CSS", "JavaScript", "MS Word", "MS Excel", "Powerpoint", "Canva")
        val wishlist = mutableListOf<String>()
        for (name in nameList) {
            if (prefs.getBoolean(name, false)) wishlist.add(name)
        }

        val teachPrefs = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        val teachSkills = teachPrefs.getStringSet("SkillsICanTeach", emptySet())?.toList() ?: emptyList()

        val data = mapOf(
            "skills_want_to_learn" to wishlist,
            "skills_can_teach" to teachSkills
        )
        db.collection("users").document(uid).set(data, SetOptions.merge())
        Log.d(TAG, "Synced to Firestore: wishlist=$wishlist teachSkills=$teachSkills")
    }

    private fun performFirebaseMatching(learnSkill: String) {
        val selectedTeachSkills = mutableListOf<String>()
        for (i in 0 until binding.teachSkillsChipGroup.childCount) {
            val chip = binding.teachSkillsChipGroup.getChildAt(i) as Chip
            if (chip.isChecked) selectedTeachSkills.add(chip.text.toString())
        }
        if (selectedTeachSkills.isEmpty()) {
            Toast.makeText(this, "Please select at least one skill to teach", Toast.LENGTH_SHORT).show()
            binding.matchesRecycler.adapter = null
            return
        }
        lastSelectedTeachSkills = selectedTeachSkills

        val myUid = auth.currentUser?.uid
        if (myUid == null) {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").get()
            .addOnSuccessListener { qs ->
                val candidates = qs.documents.mapNotNull { it.toObject<UserProfile>()?.apply { uid = it.id } }
                    .filter { it.uid != myUid }
                    .filter { profile ->
                        val teachesMe = profile.skills_can_teach.contains(learnSkill)
                        val wantsFromMe = profile.skills_want_to_learn.any { it in selectedTeachSkills }
                        teachesMe && wantsFromMe
                    }

                Log.d(TAG, "Matching finished: found=${candidates.size} for learn=$learnSkill teach=$selectedTeachSkills")

                if (candidates.isEmpty()) {
                    Toast.makeText(this, "No skill match found!", Toast.LENGTH_SHORT).show()
                    binding.matchesRecycler.adapter = null
                } else {
                    binding.matchesRecycler.adapter = MatchAdapter(candidates, this)
                    Toast.makeText(this, "Found ${candidates.size} skill matches", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to search matches", it)
                Toast.makeText(this, "Failed to search matches: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestClick(targetProfile: UserProfile) {
        val myUid = auth.currentUser?.uid
        if (myUid == null) {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show()
            return
        }
        val targetUid = targetProfile.uid
        if (targetUid.isNullOrBlank()) {
            Toast.makeText(this, "Invalid target profile", Toast.LENGTH_SHORT).show()
            return
        }
        if (targetUid == myUid) {
            Log.w(TAG, "Safeguard: Tried to send request to myself! Blocked.")
            Toast.makeText(this, "Cannot send request to yourself", Toast.LENGTH_SHORT).show()
            return
        }

        val request = MatchRequest(
            fromUid = myUid,
            fromName = myDisplayName,
            skills_want_to_learn = listOf(skillToLearnAfterSearch),
            skills_can_teach = lastSelectedTeachSkills,
            status = "pending",
            timestamp = System.currentTimeMillis()
        )

        Log.d(TAG, "Sending request: from=$myUid to=$targetUid skillToLearn=$skillToLearnAfterSearch teach=$lastSelectedTeachSkills")

        db.collection("users")
            .document(targetUid)
            .collection("requests")
            .add(request)
            .addOnSuccessListener {
                Log.d(TAG, "Request sent successfully to $targetUid")
                Toast.makeText(this, "Request sent to ${targetProfile.displayName ?: "user"}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to send request", it)
                Toast.makeText(this, "Failed to send request: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
