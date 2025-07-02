package com.sougata.chatly

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sougata.chatly.auth.AuthenticationActivity


class SplashActivity : AppCompatActivity() {

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        installSplashScreen()

        if (this.auth.currentUser == null) {
            startActivity(Intent(this, AuthenticationActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }

        finishAffinity()
    }
}