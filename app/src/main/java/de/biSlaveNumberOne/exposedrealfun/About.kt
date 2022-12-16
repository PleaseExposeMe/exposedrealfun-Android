package de.biSlaveNumberOne.exposedrealfun

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        //Tracking
        var trackingID: String

        //Retrieve from SharedPreference
        val preference=getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)
        trackingID = preference.getString("trackingID","").toString()

        //mailto: btn
        val btn = findViewById<TextView>(R.id.mail_btn)
        btn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:ExposeMeNowMarcel@outlook.de"))
            startActivity(browserIntent)
        }
        //close btn
        val btn2 = findViewById<ImageView>(R.id.btn2)
        btn2.setOnClickListener {
            finish()
        }

        // in your method, just use:
        val versionName = findViewById<TextView>(R.id.version)
        versionName.text = "App version: " + BuildConfig.VERSION_NAME

        // in your method, just use:
        val trackingName = findViewById<TextView>(R.id.trackingID)
        trackingName.text = "TrackingID: " + trackingID
    }

}