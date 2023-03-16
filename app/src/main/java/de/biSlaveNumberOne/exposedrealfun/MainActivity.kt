package de.biSlaveNumberOne.exposedrealfun



import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Activate Material3
        if (DynamicColors.isDynamicColorAvailable()) {
            DynamicColors.applyToActivitiesIfAvailable(application)
        }

        /*
        val versionName = findViewById<TextView>(R.id.version)
        versionName.text = BuildConfig.VERSION_NAME
         */

        //Tracking
        var trackingID: String

        //Retrieve from SharedPreference
        val preference=getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)
        trackingID = preference.getString("trackingID","").toString()

        if(trackingID==""){
            val newTrackingID = getRandomString(16)

            //Store in SharedPreference
            val editor=preference.edit()
            editor.putString("trackingID",newTrackingID)
            editor.apply()

            trackingID = newTrackingID;
        }


        val thread = Thread {
            try {
                val response = Request().run("http://exxxpose-extend.bplaced.net/api/tracking/?id=$trackingID")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()

        val intent = Intent(this, Main::class.java)

        startActivity(intent)
        finish()

    }

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}