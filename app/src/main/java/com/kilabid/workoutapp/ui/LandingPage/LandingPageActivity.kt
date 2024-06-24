package com.kilabid.workoutapp.ui.LandingPage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kilabid.workoutapp.databinding.ActivityLandingPageBinding
import com.kilabid.workoutapp.ui.LoginPage.LoginPageActivity
import com.kilabid.workoutapp.ui.RegisterPage.RegisterPageActivity

class LandingPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingPageBinding
    private lateinit var loginButton : Button
    private lateinit var signupButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginButton = binding.loginButton
        signupButton = binding.registerButton
        loginButton.setOnClickListener{
            intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)

        }

        signupButton.setOnClickListener {
            intent = Intent(this, RegisterPageActivity::class.java)
            startActivity(intent)
        }
    }
}