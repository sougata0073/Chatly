package com.sougata.chatly

import android.annotation.SuppressLint
import android.app.ActivityOptions
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

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val supabase = MySupabaseClient.getInstance()
    private val auth = this.supabase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()

        this.setContentView(R.layout.activity_splash)

        this.actionBar?.hide()

        this.lifecycleScope.launch {
            // This ensures authentication is properly initialized
            auth.awaitInitialization()

            val currentUser = auth.currentUserOrNull()
            val activityOptionsBundle = ActivityOptions.makeCustomAnimation(
                this@SplashActivity,
                R.anim.fade_in_enter,
                R.anim.fade_out_exit
            ).toBundle()

            val activityClass = if (currentUser == null) {
                AuthenticationActivity::class.java
            } else {
                MainActivity::class.java
            }

            startActivity(
                Intent(this@SplashActivity, activityClass),
                activityOptionsBundle
            )

            finishAffinity()
        }
    }
}