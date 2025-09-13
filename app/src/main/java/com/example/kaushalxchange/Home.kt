package com.example.kaushalxchange

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.kaushalxchange.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Home : AppCompatActivity() {
    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private lateinit var toggle: ActionBarDrawerToggle
    private val PICK_IMAGE_REQUEST = 1001

    private lateinit var headerProfileImage: ImageView
    private lateinit var headerUsername: TextView
    private lateinit var headerEmail: TextView

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val nameList = arrayOf(
        "Python", "Java", "C", "HTML", "CSS",
        "JavaScript", "MS Word", "MS Excel", "Powerpoint", "Canva"
    )
    private val imageList = intArrayOf(
        R.drawable.python, R.drawable.java, R.drawable.c, R.drawable.html, R.drawable.css,
        R.drawable.javascript, R.drawable.word, R.drawable.excel, R.drawable.powerpoint, R.drawable.canva
    )

    // --- Double back press handling ---
    private var backPressedOnce = false
    private val backPressHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarHome)
        setSupportActionBar(binding.toolbarHome)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Setup Drawer Toggle
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbarHome,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Get header view from NavigationView
        val headerView = binding.navigationView.getHeaderView(0)
        headerProfileImage = headerView.findViewById(R.id.profileImage)
        headerUsername = headerView.findViewById(R.id.usernameText)
        headerEmail = headerView.findViewById(R.id.emailText)

        // Load user data
        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            headerEmail.text = it.email ?: "No Email"

            val userRef = firestore.collection("users").document(it.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    headerUsername.text = document.getString("displayName") ?: "User"
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(headerProfileImage)
                    } else {
                        headerProfileImage.setImageResource(R.drawable.profile)
                    }
                }
            }
        }

        // Profile image click
        headerProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Navigation Drawer
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navCourses -> startActivity(Intent(this, OngoingCoursesActivity::class.java))
                R.id.navLiked -> startActivity(Intent(this, LikedSkillsActivity::class.java))
                R.id.navFavourites -> startActivity(Intent(this, FavouriteTutorActivity::class.java))
                R.id.navFeedback -> startActivity(Intent(this, FeedbackActivity::class.java))
                R.id.navTerms -> startActivity(Intent(this, TermsAndConditionsActivity::class.java))
                R.id.navContact -> startActivity(Intent(this, ContactUsActivity::class.java))
                R.id.navShare -> {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out Kaushal Xchange App!")
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share via"))
                }
                R.id.navAbout -> startActivity(Intent(this, AboutUsActivity::class.java))
                R.id.navLogout -> {
                    firebaseAuth.signOut()
                    val intent = Intent(this, LogIn::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // Buttons
        binding.exploreButton.setOnClickListener {
            startActivity(Intent(this, CustomList::class.java))
        }
        binding.matchButton.setOnClickListener {
            startActivity(Intent(this, FindMatchActivity::class.java))
        }
        binding.myconnectionButton.setOnClickListener {
            startActivity(Intent(this, MyConnectionActivity::class.java))
        }
        binding.wishlistButton.setOnClickListener {
            startActivity(Intent(this, MyLearningWishlist::class.java))
        }
        binding.myskillsButton.setOnClickListener {
            startActivity(Intent(this, MySkillsActivity::class.java))
        }
        binding.activeCoursesButton.setOnClickListener {
            startActivity(Intent(this, ActiveCourses::class.java))
        }
        binding.skillAcquiredButton.setOnClickListener {
            startActivity(Intent(this, AcquiredSkills::class.java))
        }
        binding.teachButton.setOnClickListener {
            startActivity(Intent(this, SkillICanTeachActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let { uploadProfilePicture(it) }
        }
    }

    private fun uploadProfilePicture(imageUri: Uri) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val profileRef = firebaseStorage.reference.child("profile_pictures/${currentUser.uid}.jpg")

        profileRef.putFile(imageUri)
            .addOnSuccessListener {
                profileRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        firestore.collection("users").document(currentUser.uid)
                            .update("profileImageUrl", downloadUrl)
                            .addOnSuccessListener {
                                Glide.with(this).load(downloadUrl).into(headerProfileImage)
                            }
                    }
            }
    }

    // Toolbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.queryHint = "Search skills..."
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val trimmedQuery = it.trim()
                    val index = nameList.indexOfFirst { skill -> skill.equals(trimmedQuery, ignoreCase = true) }
                    if (index != -1) {
                        val intent = Intent(this@Home, DetailedActivity::class.java)
                        intent.putExtra("name", nameList[index])
                        intent.putExtra("image", imageList[index])
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Home, "Skill not found", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                startActivity(Intent(this, NotificationsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //  Double back press to exit
    override fun onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed()
            return
        }

        backPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        backPressHandler.postDelayed({ backPressedOnce = false }, 2000)
    }
}
