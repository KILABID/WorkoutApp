package com.kilabid.workoutapp.ui.RegisterPage

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.kilabid.workoutapp.databinding.ActivityRegisterPageBinding

class RegisterPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var topAppBar: androidx.appcompat.widget.Toolbar
    private lateinit var registButton: FloatingActionButton
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etEmail: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topAppBar = binding.topAppBar
        registButton = binding.registButton
        etUsername = binding.etUsernameRegist
        etPassword = binding.etPasswordRegist
        etEmail = binding.etEmailRegist

        auth = FirebaseAuth.getInstance()

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        registButton.setOnClickListener {
            email = etEmail.text.toString()
            password = etPassword.text.toString()
            username = etUsername.text.toString()
            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                Log.d("RegisterPageActivity", "$email $password $username")
            } else if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            } else if (password.contains(" ")) {
                Toast.makeText(this, "Password cannot contain spaces", Toast.LENGTH_SHORT).show()
            } else if (username.contains(" ")) {
                Toast.makeText(this, "Username cannot contain spaces", Toast.LENGTH_SHORT).show()
            } else if (!email.contains("@")) {
                Toast.makeText(this, "Email must contain @", Toast.LENGTH_SHORT).show()
            } else {
                createAnAccount(email, password, username)
            }
        }
    }

    private fun createAnAccount(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    user?.let {
                        updateProfile(it, username)
                    }
                    user?.sendEmailVerification()?.addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            Log.d(ContentValues.TAG, "Email sent")
                            finish()
                            Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.w(ContentValues.TAG, "sendEmailVerification:failure", result.exception)
                            Toast.makeText(this, "Email could not be sent", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateProfile(user: FirebaseUser, username: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "User profile updated.")
                } else {
                    Log.w(ContentValues.TAG, "User profile update failed.", task.exception)
                }
            }
    }
}
