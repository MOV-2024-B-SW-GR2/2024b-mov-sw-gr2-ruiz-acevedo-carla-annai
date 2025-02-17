package com.example.a05_proyecto.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.a05_proyecto.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Used handler to delay the intent by 1 seconds
        Handler().postDelayed(
            {
                val intent = Intent(this@SplashActivity, LoginRegisterActivity::class.java)
                finish()
                startActivity(intent)
            }, 1000
        )
    }
}