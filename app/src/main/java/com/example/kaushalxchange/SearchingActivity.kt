package com.example.kaushalxchange

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class SearchingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searching)

        val animationView = findViewById<LottieAnimationView>(R.id.searching_animation)
        animationView.setAnimation(R.raw.ai_search)
        animationView.playAnimation()

        // Delay 3 seconds → just finish activity
        Handler(Looper.getMainLooper()).postDelayed({
            finish() // Do not restart FindMatchActivity
        }, 8000)
    }
}
