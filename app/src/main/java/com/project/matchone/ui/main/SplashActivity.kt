package com.project.matchone.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.matchone.R
import com.project.matchone.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tvLogo     = findViewById<TextView>(R.id.tvSplashLogo)
        val tvTagline  = findViewById<TextView>(R.id.tvSplashTagline)
        val tvBrand    = findViewById<TextView>(R.id.tvSplashBrand)

        // Animasi fade + scale untuk logo
        val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 1000 }
        val scaleUp = ScaleAnimation(
            0.5f, 1f, 0.5f, 1f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        ).apply { duration = 1000 }

        val animSet = AnimationSet(true).apply {
            addAnimation(fadeIn)
            addAnimation(scaleUp)
            fillAfter = true
        }

        tvLogo.startAnimation(animSet)

        // Animasi fade untuk tagline (delay sedikit)
        val fadeInSlow = AlphaAnimation(0f, 1f).apply {
            duration = 1200
            startOffset = 600
            fillAfter = true
        }
        tvTagline.startAnimation(fadeInSlow)
        tvBrand.startAnimation(fadeInSlow)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000)
    }
}