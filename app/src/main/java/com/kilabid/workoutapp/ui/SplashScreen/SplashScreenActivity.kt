package com.kilabid.workoutapp.ui.SplashScreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kilabid.workoutapp.R
import com.kilabid.workoutapp.data.UserPreferences
import com.kilabid.workoutapp.data.datastore
import com.kilabid.workoutapp.ui.LandingPage.LandingPageActivity
import com.kilabid.workoutapp.ui.MainPage.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)
        //Buatkan saya kode splash screen
        lifecycleScope.launch {
            delay(5000)
            val userPreferences = UserPreferences.getInstance(datastore)
            val user = userPreferences.getSession().first()
            val intent = if (user.isLogin) {
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashScreenActivity, LandingPageActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}