package com.example.kaushalxchange

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.kaushalxchange.databinding.ActivityCustomListBinding

class CustomList : AppCompatActivity() {

    private lateinit var binding: ActivityCustomListBinding
    private lateinit var listAdapter: ListAdapter
    private val dataArrayList = ArrayList<ListData?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameList = arrayOf(
            "Python", "Java", "C", "HTML", "CSS",
            "JavaScript", "MS Word", "MS Excel", "Powerpoint", "Canva"
        )
        val imageList = intArrayOf(
            R.drawable.python, R.drawable.java, R.drawable.c, R.drawable.html, R.drawable.css,
            R.drawable.javascript, R.drawable.word, R.drawable.excel, R.drawable.powerpoint, R.drawable.canva
        )
        for (i in nameList.indices) dataArrayList.add(ListData(nameList[i], imageList[i]))

        listAdapter = ListAdapter(this, dataArrayList)
        binding.listview.adapter = listAdapter
        binding.listview.isClickable = true
        binding.listview.emptyView = binding.emptyView

        binding.listview.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, i, _ ->
                listAdapter.getItem(i)?.let { openDetail(it) }
            }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun openDetail(item: ListData) {
        val intent = Intent(this, DetailedActivity::class.java)
        intent.putExtra("name", item.name)
        intent.putExtra("image", item.image)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView ?: return true

        searchView.queryHint = "Search skills..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                listAdapter.filter.filter(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val q = query?.trim().orEmpty()
                if (q.isEmpty()) return true

                val match = (0 until listAdapter.count)
                    .mapNotNull { listAdapter.getItem(it) }
                    .firstOrNull { it.name.equals(q, ignoreCase = true) }

                if (match != null) {
                    openDetail(match)
                } else {
                    Toast.makeText(this@CustomList, "Skill not found", Toast.LENGTH_SHORT).show()
                }

                searchView.clearFocus()
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
