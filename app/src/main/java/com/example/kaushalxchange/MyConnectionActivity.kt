package com.example.kaushalxchange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyConnectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_connection)

        recyclerView = findViewById(R.id.recyclerViewConnections)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val userList = listOf(
            User("Arun Kumar", R.drawable.profile),
            User("Shiv Shankar", R.drawable.profile),
            User("Prashant", R.drawable.profile),
            User("Navneet Mishra", R.drawable.profile),
            User("Swati Singh", R.drawable.profile),
            User("Animesh Pandey", R.drawable.profile),
            User("Rashmi Singh", R.drawable.profile),
            User("Ansh Sigh", R.drawable.profile),
            User("Aditya Dubey", R.drawable.profile),
            User("Palak Mishra", R.drawable.profile)
        )

        adapter = UserAdapter(userList)
        recyclerView.adapter = adapter
    }
}
