package com.sougata.chatly

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sougata.chatly.auth.AuthenticationActivity
import com.sougata.chatly.data.MySupabaseClient
import io.github.jan.supabase.auth.auth

class SplashActivity : AppCompatActivity() {

    private val auth = MySupabaseClient.getInstance().auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        installSplashScreen()

        if (this.auth.currentUserOrNull() == null) {
            startActivity(Intent(this, AuthenticationActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }

        finishAffinity()
    }
}