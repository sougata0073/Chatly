package com.sougata.chatly

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.sougata.chatly.auth.AuthenticationActivity
import com.sougata.chatly.data.MySupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val supabase = MySupabaseClient.getInstance()
    private val auth = this.supabase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        installSplashScreen()

        this.lifecycleScope.launch {
            // This ensures authentication is properly initialized
            auth.awaitInitialization()

            val currentUser = auth.currentUserOrNull()

            if (currentUser == null) {
                startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }

            finishAffinity()
        }
    }
}