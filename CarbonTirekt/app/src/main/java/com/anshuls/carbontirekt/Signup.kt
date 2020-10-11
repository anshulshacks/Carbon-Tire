package com.anshuls.carbontirekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore
        val signupBtn = findViewById<Button>(R.id.signup)
        signupBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.signupEmail)
            val password = findViewById<EditText>(R.id.signupPassword)
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            if (emailText == "" ||  passwordText == "") {
                Toast.makeText(this, "Please enter a valid email and/or password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val TAG = "login"
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        val uuid = user?.uid
                        val mpgText = findViewById<EditText>(R.id.mpg)
                        val mpg = mpgText.text.toString().toInt()
                        val userInfo = hashMapOf(
                            "uid" to uuid.toString(),
                            "carbonEmitted" to 0,
                            "carbonSaved" to 0,
                            "carbonScore" to 0,
                            "mpg" to mpg
                        )
                        db.collection("profiles")
                            .add(userInfo)
                            .addOnSuccessListener {documentReference ->
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                            .addOnFailureListener {e ->
                                Log.d(TAG, e.toString())
                            }

//                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }

                    // ...
                }

        }

    }
}