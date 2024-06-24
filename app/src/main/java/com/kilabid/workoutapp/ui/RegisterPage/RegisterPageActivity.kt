package com.kilabid.workoutapp.ui.RegisterPage

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.kilabid.workoutapp.databinding.ActivityRegisterPageBinding

class RegisterPageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterPageBinding
    private lateinit var topAppBar: androidx.appcompat.widget.Toolbar
    private lateinit var registButton : FloatingActionButton
    private lateinit var etUsername : EditText
    private lateinit var etPassword : EditText
    private lateinit var etEmail : EditText
    private lateinit var googleButton : CardView
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

        email = etEmail.text.toString()
        password = etPassword.text.toString()
        username = etUsername.text.toString()

        topAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
            finish()
        }


        registButton.setOnClickListener {
            email = etEmail.text.toString()
            password = etPassword.text.toString()
            username = etUsername.text.toString()
            // Handle registration button press
            if(email.isEmpty() || password.isEmpty() || username.isEmpty()){
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                Log.d("RegisterPageActivity", email + " " + password + " " + username )
            }else if (password.length < 8){
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            }else if(password.contains(" ")){
                Toast.makeText(this, "Password cannot contain spaces", Toast.LENGTH_SHORT).show()
            }else if(username.contains(" ")){
                Toast.makeText(this, "Username cannot contain spaces", Toast.LENGTH_SHORT).show()
            }else if(!email.contains("@")){
                Toast.makeText(this, "Email must contain @", Toast.LENGTH_SHORT).show()
            }else{
                createAnAccount(email, password)
            }
        }

    }
    private fun createAnAccount(email: String, password: String){
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    // Send email verification
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { result ->
                            if (result.isSuccessful) {
                                Log.d(ContentValues.TAG, "Email sent")
                                finish()
                                // Show a message to the user indicating that an email has been sent
                                Toast.makeText(this,  "", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.w(ContentValues.TAG, "sendEmailVerification:failure", result.exception)
                                // Show a message to the user indicating that the email could not be sent
                                Toast.makeText(this,  "Email could not be sent", Toast.LENGTH_SHORT).show()
                            }
                        }

                    // Navigate to the next screen or perform other actions
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    // Update UI for error case
                }
            }
    }
}