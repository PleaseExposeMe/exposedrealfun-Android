package de.biSlaveNumberOne.exposedrealfun

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val editText = findViewById<EditText>(R.id.input)
        editText.requestFocus()

        UpdateRecyclinView("")

        val searchBtn = findViewById<ImageView>(R.id.btn)
        searchBtn.setOnClickListener {
            loadViewer("https://www.exposedrealfun.com/?q="+ editText.text +"&order=creation%3Adesc")
        }

        editText.setOnKeyListener(View.OnKeyListener{v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){
                loadViewer("https://www.exposedrealfun.com/?q="+ editText.text +"&order=creation%3Adesc")
            }
            false
        })

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                UpdateRecyclinView(s.toString())
            }
        })

    }

    fun UpdateRecyclinView(s: String){
        val editText = findViewById<EditText>(R.id.input)
        val tagManager =  TagManager()
        val result = tagManager.SerchTags(s.toString())
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)

        if(result.size <= 1){
            recyclerview.visibility = View.GONE
        }else{
            recyclerview.visibility = View.VISIBLE
        }

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(applicationContext)

        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(result)

        adapter.setOnItemClickListener(object : CustomAdapter.onItemClickListner{
            override fun onItemClick(position: Int){
                editText.setText(result[position])
                editText.setSelection(editText.text.length)
            }
        })

        recyclerview.adapter = adapter
    }

    fun loadViewer(url: String){

        if(url.startsWith("https://www.exposedrealfun.com/")){
            val intent = Intent(this, Viewer::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }else{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
}