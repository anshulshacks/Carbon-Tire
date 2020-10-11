package com.anshuls.carbontirekt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.collections.List

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val db = Firebase.firestore

        var carbonEmittedGrams = 0
        var carbonSavedGrams = 0
        var netCarbon = 0
        var prePoint:Float = 0f
        var score = 0

        val list = findViewById<ImageView>(R.id.list1)
        val car = findViewById<ImageView>(R.id.car)

        list.setOnClickListener {
            startActivity(Intent(this, List::class.java))
        }
        car.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        db.collection("drives")
            .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser()?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    carbonEmittedGrams += document.data["carbonEmitted"].toString().toInt()
                    carbonSavedGrams += document.data["carbonSaved"].toString().toInt()
                }
                netCarbon = carbonEmittedGrams - carbonSavedGrams
                Log.d("stats", "netCarbon: " + netCarbon.toString())
                prePoint = (netCarbon.toFloat() / 574999)
                Log.d("stats", "prePoint: " + prePoint.toString())
                score = (prePoint * 100).toInt()
                Log.d("stats", "score: " + score.toString())
                progress_bar.progress = score
                text_view_progress.text = score.toString() +" points"
            }
            .addOnFailureListener {e ->
                Log.d("random", e.toString())
            }
        donate.setOnClickListener {
            goToUrl("https://teamtrees.org")
        }

    }

    private fun goToUrl(url: String) {
        val uriUrl: Uri = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }
}