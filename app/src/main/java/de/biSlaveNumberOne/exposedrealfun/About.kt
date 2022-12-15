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
    }

}