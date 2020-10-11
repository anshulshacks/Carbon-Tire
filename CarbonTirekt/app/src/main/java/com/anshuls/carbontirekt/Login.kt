package com.anshuls.carbontirekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val signup = findViewById<Button>(R.id.signupPath)
        signup.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

        val loginBtn = findViewById<Button>(R.id.login)

        loginBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.signupEmail)
            val password = findViewById<EditText>(R.id.loginPassword)
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            if (emailText == "" || passwordText == "") {
                Toast.makeText(
                    this,
                    "Please enter a valid email and/or password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val TAG = "login"
            auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
//                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
//                        updateUI(null)
                        // ...
                    }

                }
        }
    }
}