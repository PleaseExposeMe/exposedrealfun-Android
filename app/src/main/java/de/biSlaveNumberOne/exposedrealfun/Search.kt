package de.biSlaveNumberOne.exposedrealfun

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder


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
                window.statusBarColor = ContextCompat.getColor(this, R.color.Background_color)
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
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE)
                editText.setHint("Add a tag")
            }else{
                editText.setHint("Search")
                editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH)
                editText.setText(currentSearchValue)
                editText.setSelection(editText.text.length)
            }
        }

        editText.setOnKeyListener(View.OnKeyListener{v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP){
                if(!tagMode){

                    var URL = "https://www.exposedrealfun.com/"

                    //Build URL
                    if(editText.text.isNotBlank() || editText.text.isNotEmpty()){
                        URL = URL + "?q="+ editText.text +"&order=creation%3Adesc"
                    }else{
                        URL = "$URL?order=creation%3Adesc"
                    }

                    if(tags.size > 0){
                        for (tag in tags){
                            URL = "$URL&tags%5Btags%5D%5B%5D=$tag"
                        }
                    }

                    loadViewer(URL)
                }
            }
            false
        })

        editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                AddTag(editText.text.toString())
                editText.requestFocus()
                editText.setText("")
                editText.setSelection(editText.text.length)
            }
            false
        }

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

    @SuppressLint("ResourceType")
    fun AddTag(tag: String){
        var scrollView2 = findViewById<HorizontalScrollView>(R.id.scrollView2)
        scrollView2.visibility = View.VISIBLE

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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                textView.setPadding(20, 10, 20, 10)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(20, 10, 0, 10) // setMargins(left, top, right, bottom)
            }
            textView.background = ContextCompat.getDrawable(applicationContext, R.drawable.rounded_tgas)
            textView.setTextColor(Color.WHITE)

            //padding
            textView.setPadding(20, 10, 20, 10)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            //margin
            params.setMargins(20, 10, 0, 10) // setMargins(left, top, right, bottom)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                //padding
                textView.setPadding(40,30,40,30)
                //margin
                params.setMargins(20, 0, 0, 30)
            }

            textView.layoutParams = params
            //id
            textView.id = id
            id++



            textView.setOnClickListener { view -> // tags.removeAt(textView.id)
                MaterialAlertDialogBuilder(this, R.drawable.rounded_dialog)
                    .setTitle("Remove Tag?")
                    .setMessage("Are you sure you want to remove the tag ${textView.text}?")
                    .setNegativeButton("No", null)
                    .setPositiveButton(
                        "Yes"
                    ) { _, _ ->
                        tags.remove(textView.text)
                        tagManager.removeView(view)
                        if (tags.size == 0) {
                            id = 0
                            scrollView2.visibility = View.GONE
                        }
                    }.create().show()
            }

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