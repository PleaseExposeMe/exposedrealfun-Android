package de.biSlaveNumberOne.exposedrealfun

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.DynamicColors


class Search : AppCompatActivity() {

    val tags : MutableList<String> = ArrayList()
    var id = 0
    var currentSearchValue = ""
    var tagMode = false

    @SuppressLint("ResourceAsColor", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val editText = findViewById<EditText>(R.id.input)
        editText.requestFocus()

        UpdateRecyclinView("")

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            if (DynamicColors.isDynamicColorAvailable()) {
                val window = window
                window.statusBarColor = ContextCompat.getColor(this, R.color.Third_color)
            }
        }

        //close btn
        val btn = findViewById<ImageView>(R.id.closebtn)
        btn.setOnClickListener {
            finish()
        }

        val tagbtn = findViewById<ImageView>(R.id.tags)
        tagbtn.setOnClickListener {
            tagMode = !tagMode
            if(tagMode){
                currentSearchValue = editText.text.toString()
                editText.setText("")
                editText.setHint("Add a tag")
            }else{
                editText.setHint("Search")
                editText.setText(currentSearchValue)
                editText.setSelection(editText.text.length)
            }
        }

        //   AddTag("100Days")

        editText.setOnKeyListener(View.OnKeyListener{v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){
                if(tagMode){
                    AddTag(editText.text.toString())
                }else{
                    loadViewer("https://www.exposedrealfun.com/?q="+ editText.text +"&order=creation%3Adesc")
                }
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

    fun AddTag(tag: String){
        tags.add(tag)
        val tagManager = findViewById<LinearLayout>(R.id.taglist)
        tagManager.removeAllViews()
        val a = LinearLayout(this)
        a.orientation = LinearLayout.HORIZONTAL

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        for (i in tags.indices) {
            val textView = TextView(this)
            textView.text = tags[i]
            textView.setPadding(40,20,40,20)
            textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.rounded_tgas)

            //margin
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 30, 0, 30) // setMargins(left, top, right, bottom)

            textView.layoutParams = params
            //id
            textView.id = id
            id++



            textView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                   // tags.removeAt(textView.id)
                    tagManager.removeView(view)
                }
            })

            tagManager.addView(textView)
        }
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
            //finish()
        }else{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
}