package com.anshuls.carbontirekt

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.internal.bind.util.ISO8601Utils.format
import java.time.Instant
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var startLocation: Location? = null
    private var endLocation: Location? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        startActivity(Intent(this, Login::class.java))
        val db = Firebase.firestore


        val tips = mutableListOf(
            "Tip of the Day: Lower your thermostat in winter or increase it in summer to save green house gas emissions",
            "Tip of the Day: Be sure to turn off your lights to save energy!",
            "Tip of the Day: Try going solar to harness the sun's energy"
        )

        val tip = findViewById<TextView>(R.id.tip)

        tip.text = tips[1]

        val user = findViewById<ImageView>(R.id.user)
        user.setOnClickListener {
             startActivity(Intent(this, Profile::class.java))
        }
        val list = findViewById<ImageView>(R.id.list)
        list.setOnClickListener {
            startActivity(Intent(this, List::class.java))
        }
        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

//        var startLocation: Location?
//        var endLocation: Location?

        val startBtn = findViewById<Button>(R.id.start)
        val endBtn = findViewById<Button>(R.id.end)

        startBtn.setOnClickListener {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    // Got last known location. In some rare situations this can be null.
                    Log.d("random", location.toString())
                    startLocation = location
                    Log.d("random", "startLocation: " + startLocation.toString())

                }
                .addOnFailureListener {e ->
                    Log.d("random", e.toString())
                }

        }

        endBtn.setOnClickListener{
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    // Got last known location. In some rare situations this can be null.
                    Log.d("random", location.toString())
                    endLocation = location

                    val startLat = startLocation?.latitude.toString()

                    Log.d("random", "startLocation: " + startLocation.toString())
                    Log.d("random", "endLocation: " + endLocation.toString())
                    var distance = startLocation?.distanceTo(endLocation)

                    val type = findViewById<Spinner>(R.id.spinner)
                    var carbonEmittedGrams = 0
                    var carbonSavedGrams = 0
                    val currentUser = FirebaseAuth.getInstance().getCurrentUser()?.uid ;
                    db.collection("profiles")
                        .whereEqualTo("uid", currentUser)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                if (type.selectedItem == "Driving") {
//                                    val distanceInKm = (distance.toString().toInt() / 1000)
//                                    val distanceInMi = distanceInKm * 62 / 100
//                                    val gallons = distanceInMi
                                    carbonEmittedGrams = ((((distance.toString().toInt() / 1000) * 0.62) / document.data["mpg"].toString().toInt()) * 8887).toInt()
                                    carbonSavedGrams = 0
                                }
                                else {
                                    carbonSavedGrams = ((((distance.toString().toInt() / 1000) * 0.62) / document.data["mpg"].toString().toInt()) * 8887).toInt()
                                    carbonEmittedGrams = 0
                                }

                                val drive = hashMapOf(
                                    "distance" to distance,
                                    "carbonEmitted" to carbonEmittedGrams,
                                    "carbonSaved" to carbonSavedGrams,
                                    "date" to DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString(),
                                    "uid" to currentUser
                                )

                                db.collection("drives")
                                    .add(drive)
                                    .addOnSuccessListener { documentReference ->
                                        Toast.makeText(this, "Drive Recorded", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d("random", e.toString())
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.d("random", e.toString())
                        }

                }
                .addOnFailureListener {e ->
                    Log.d("random", e.toString())
                }

        }
//        fusedLocationClient.lastLocation
//                .addOnSuccessListener { location : Location? ->
//                    // Got last known location. In some rare situations this can be null.
//                    Log.d("random", location.toString())
//                    val lat = findViewById<TextView>(R.id.lat)
//                    val lng = findViewById<TextView>(R.id.lng)
//                    lat.text = location?.latitude.toString()
//                    lng.text = location?.longitude.toString()
//                }
//                .addOnFailureListener {e ->
//                    Log.d("random", e.toString())
//                }
    }
}