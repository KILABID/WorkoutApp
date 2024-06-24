package com.kilabid.workoutapp.ui.SplashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.kilabid.workoutapp.R
import com.kilabid.workoutapp.ui.LandingPage.LandingPageActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)
        //Buatkan saya kode splash screen
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, LandingPageActivity::class.java))
            finish()
        }, 2000)
    }
}