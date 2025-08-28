package com.example.kaushalxchange

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class JavaModulesActivity : AppCompatActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_java_modules)

        prefs = getSharedPreferences("KaushalXChangePrefs", Context.MODE_PRIVATE)

        viewPager = findViewById(R.id.viewPager)
        val adapter = ModulePagerAdapterJava(this)
        viewPager.adapter = adapter

        // Prevent overscroll beyond Module 6 and keep pages in memory
        viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
        viewPager.offscreenPageLimit = adapter.itemCount

        // Resume from last incomplete module
        val startModule = prefs.getInt("last_completed_module", 0)
        viewPager.setCurrentItem(startModule, false)

        // Save last visited module
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                prefs.edit().putInt("last_completed_module", position).apply()
            }
        })
    }
}
