package com.ark.automobile.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.ark.automobile.R

class Splash : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 3000 // 3 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            startActivity(Intent(this, SecondActivity::class.java))
            finish()
            // close this activitY
        }, SPLASH_TIME_OUT)
    }
}
