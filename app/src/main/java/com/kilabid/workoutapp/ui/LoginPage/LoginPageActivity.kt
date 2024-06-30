package com.kilabid.workoutapp.ui.LoginPage

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.kilabid.workoutapp.data.UserModel
import com.kilabid.workoutapp.databinding.ActivityLoginPageBinding
import com.kilabid.workoutapp.ui.MainPage.MainActivity
import com.kilabid.workoutapp.ui.ViewModelFactory
import kotlinx.coroutines.launch

class LoginPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var topAppBar: androidx.appcompat.widget.Toolbar
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var loginButton: FloatingActionButton
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topAppBar = binding.topAppBar
        etEmail = binding.etEmail
        etPassword = binding.etPassword
        loginButton = binding.loginButton

        loginButton.setOnClickListener {
            email = etEmail.text.toString()
            password = etPassword.text.toString()

            // Initialize Firebase Auth
            auth = FirebaseAuth.getInstance()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val emailVerified = user?.isEmailVerified
                        if (emailVerified == true) {
                            user.getIdToken(true).addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val idToken = tokenTask.result?.token
                                    Log.d(TAG, "Token: $idToken")
                                    Toast.makeText(baseContext, "Authentication success.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("displayName", user.displayName.toString())
                                    intent.putExtra("photoUrl", user.photoUrl.toString())
                                    intent.putExtra("idToken", idToken)
                                    lifecycleScope.launch {
                                        viewModel.saveSession(UserModel(
                                            user.email.toString(),
                                            user.displayName.toString(),
                                            true
                                        ))
                                    }
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Log.w(TAG, "getIdToken:failure", tokenTask.exception)
                                    Toast.makeText(baseContext, "Failed to get token.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(baseContext, "Please verify your email.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        etEmail.text.clear()
                        etPassword.text.clear()
                    }
                }
        }


        topAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
            finish()
        }
    }
}
