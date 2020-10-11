package com.anshuls.carbontirekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class List : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val db = Firebase.firestore

        val car = findViewById<ImageView>(R.id.car2)
        val user = findViewById<ImageView>(R.id.imageView2)

        car.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        user.setOnClickListener { startActivity(Intent(this, Profile::class.java)) }
        val drivesList = findViewById<TextView>(R.id.drives)
        var drivesStr = ""
        db.collection("drives")
            .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser()?.uid.toString())
            .get()
            .addOnSuccessListener {documents ->
                for (document in documents) {
                    val kgEmitted = document.data["carbonEmitted"].toString().toFloat() / 1000
                    val kgSaved = document.data["carbonSaved"].toString().toFloat() / 1000
                    drivesStr += "\n\n${document.data["date"]}\nEmitted: ${kgEmitted} kilograms of CO2\nSaved: ${kgSaved} kilograms of CO2\n"
                }
                drivesList.text = drivesStr
            }
            .addOnFailureListener {e ->
                Log.d("error", e.toString())
            }
    }
}