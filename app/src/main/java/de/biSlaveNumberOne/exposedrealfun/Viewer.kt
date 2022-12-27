package de.biSlaveNumberOne.exposedrealfun

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.net.http.SslError
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.net.URISyntaxException


class Viewer : AppCompatActivity() {
    lateinit var webview: WebView
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    val REQUEST_SELECT_FILE = 100
    var firstLoad = true
    var title = ""
    val Link: MutableList<String> = ArrayList()
    var bookmarkBtnState = false
    var leaveTimeStemp =  System.currentTimeMillis()
    var postOpened = false
    var urlBeforeError = ""
    var disableHistory = false
    var searchState = false

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        //File Upload
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null) return
            uploadMessage!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    intent
                )
            )
            uploadMessage = null
        }
    }
    @SuppressLint("SetJavaScriptEnabled", "Range", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        webview = findViewById(R.id.uploadview)
        webview.settings.javaScriptEnabled = true
        webview.settings.allowFileAccess = true
        webview.settings.domStorageEnabled = true
        webview.settings.allowContentAccess = true
        webview.webChromeClient = WebChromeClient()

        val refresh = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        refresh.setOnRefreshListener {
            if(webview.url == "file:///android_asset/noconnection.html"){
                webview.loadUrl(urlBeforeError)
            }else{
                webview.reload()
            }
            refresh.isRefreshing = false
        }

        //Darkmode
        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(webview.settings, WebSettingsCompat.FORCE_DARK_ON);
                }
                webview.setBackgroundColor(Color.parseColor("#212121"))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                webview.setBackgroundColor(Color.parseColor("#f7f7f7"))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                webview.setBackgroundColor(Color.parseColor("#f7f7f7"))
            }
        }

        /* An instance of this class will be registered as a JavaScript interface */
         class MyJavaScriptInterface() {
            @JavascriptInterface
            fun setTitle(aContent: String) {
                title = try{
                    aContent.replace("\n","");
                }catch (c: Exception){
                    aContent
                }
            }
        }

        webview.addJavascriptInterface(MyJavaScriptInterface(), "INTERFACE");

        webview.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                handler.cancel()
            }

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                //Check Internet Connection
                val webViewhelper = WebViewHelper()
                if(!webViewhelper.isOnline(applicationContext)){
                    updateURLBeforeError()
                    webview.loadUrl("file:///android_asset/noconnection.html")
                    return false
                }

                if (url == "https://www.exposedrealfun.com/") {
                    finish()
                }else if(url.contains("/download")){
                    //download the image
                }else
                    if (url.startsWith("https://www.exposedrealfun.com/?q=")) {

                    }
                else {
                      if (!firstLoad) {
                           loadViewer(url)
                           webview.stopLoading()
                           return false
                      }
                }

                //Open special links
                if (url.startsWith("mailto:")) {
                    var intent: Intent? = null
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory("android.intent.category.BROWSABLE")
                        intent.component = null
                        intent.selector = null
                        view.context.startActivity(intent)
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }
                    view.goBack()
                    view.stopLoading()
                    view.context.startActivity(intent)
                } else if (url.startsWith("tel:")) {
                    var intent: Intent? = null
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory("android.intent.category.BROWSABLE")
                        intent.component = null
                        intent.selector = null
                        view.context.startActivity(intent)
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }
                    view.goBack()
                    view.stopLoading()
                    view.context.startActivity(intent)
                } else if (url.startsWith("whatsapp:")) {
                    var intent: Intent? = null
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory("android.intent.category.BROWSABLE")
                        intent.component = null
                        intent.selector = null
                        view.context.startActivity(intent)
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }
                    view.goBack()
                    view.goBack()
                    view.stopLoading()
                    view.context.startActivity(intent)
                } else if (url.startsWith("tg:")) {
                    var intent: Intent? = null
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory("android.intent.category.BROWSABLE")
                        intent.component = null
                        intent.selector = null
                        view.context.startActivity(intent)
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }
                    view.goBack()
                    view.goBack()
                    view.stopLoading()
                    view.context.startActivity(intent)
                } else if (url.startsWith("geo:")) {
                    var intent: Intent? = null
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory("android.intent.category.BROWSABLE")
                        intent.component = null
                        intent.selector = null
                        view.context.startActivity(intent)
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }
                    view.goBack()
                    view.goBack()
                    view.stopLoading()
                    view.context.startActivity(intent)
                } else if (url.startsWith("sms:")) {
                    var intent: Intent? = null
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory("android.intent.category.BROWSABLE")
                        intent.component = null
                        intent.selector = null
                        view.context.startActivity(intent)
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }
                    view.goBack()
                    view.goBack()
                    view.stopLoading()
                    view.context.startActivity(intent)
                } else if (Uri.parse(url).scheme == "market") {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        val host = view.context as Activity
                        host.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // Google Play app is not installed, you may want to open the app store link
                        val uri = Uri.parse(url)
                        view.loadUrl("http://play.google.com/store/apps/" + uri.host + "?" + uri.query)
                    }
                    view.goBack()
                    view.stopLoading()
                } else if (url.startsWith("intent:")) {
                    try {
                        val context = view.context
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent!!.addCategory("android.intent.category.BROWSABLE")
                        intent.component = null
                        intent.selector = null
                        if (intent != null) {
                            view.stopLoading()
                            val packageManager = context.packageManager
                            val info = packageManager.resolveActivity(
                                intent,
                                PackageManager.MATCH_DEFAULT_ONLY
                            )
                            if (info != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "App is not installed!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return true
                        }
                    } catch (e: URISyntaxException) {
                    }
                }else{
                    view.loadUrl(url)
                }
                return true
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                updateURLBeforeError()
                webview.loadUrl("file:///android_asset/noconnection.html")
                super.onReceivedError(view, errorCode, description, failingUrl)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.VISIBLE
                webview.visibility = View.INVISIBLE

                //JavaScript/CSS injection mobile header
                val cssHeader = "html{-webkit-tap-highlight-color: transparent;}" //your css as String
                val jsHeader = "var style = document.createElement('style'); style.innerHTML = '$cssHeader'; " +
                        "document.head.appendChild(style);"
                webview.loadUrl(
                    "javascript:(function() {"
                            + jsHeader +
                            "})()"
                )
                super.onPageStarted(view, url, favicon)
            }


            override fun onPageFinished(webView: WebView?, url: String?) {
                //Save cookies for login and popup
                CookieManager.getInstance().flush()

                //JavaScript/CSS injection mobile header
                val cssHeader = "html{-webkit-tap-highlight-color: transparent;}.erf-homepage-pagination{overflow: auto;}.d-flex { overflow: auto; } .erf-homepage-filter-row { display: none; } .filter-dropmenu{overflow: unset;}/*custom-search-bar*/ .erf-search { position: fixed; left: 0; top: 0; right: 0; background-color: #ffffff; z-index: 99999; padding: 20px; box-shadow: 1px 1px 11px 2px #000000a8; } /*custom-search-bar end*/" //your css as String

                //JavaScript/CSS injection mobile header
                val jsHeader = "var style = document.createElement('style'); style.innerHTML = '$cssHeader'; " +
                        "document.getElementsByTagName('nav')[0].style.display = 'none';" +
                        "document.head.appendChild(style);"
                webview.loadUrl(
                    "javascript:(function() {"
                            + jsHeader +
                            "})()"
                )

                //Js Interface
                if (url?.startsWith("https://www.exposedrealfun.com/post/") == true) {
                    webview.loadUrl("javascript:window.INTERFACE.setTitle(document.getElementsByTagName('h1')[0].innerText);");
                }

                if (url?.startsWith("https://www.exposedrealfun.com/post/") == true) {
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            //New history entry
                            if(!disableHistory){
                                val history = SQLlite(applicationContext, null)
                                history.addHistoryEntry(url,title)
                            }
                        },
                        600 // value in milliseconds
                    )
                }

                if (url?.startsWith("https://www.exposedrealfun.com/post/") == true && url.contains("/edit/")) {
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            //New history entry
                            if(!disableHistory){
                                val history = SQLlite(applicationContext, null)

                                title = "!!!Your Post Settings!!!"

                                history.addHistoryEntry(url,title)
                            }
                        },
                        600 // value in milliseconds
                    )
                }

                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                        progressBar.visibility = View.INVISIBLE
                        webview.visibility = View.VISIBLE;

                        //Enable open new view on link click
                        firstLoad = false
                    },
                    400 // value in milliseconds
                )
                super.onPageFinished(webView, url)
            }

        }

        webview.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->

            val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)


            val request = DownloadManager.Request(
                Uri.parse(url)
            )
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle("Image Download") //Notify client once download is completed!
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "exposedrealfun_files/$filename"
            )
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(
                applicationContext,
                "Downloading Image",  //To notify the Client that the file is being downloaded
                Toast.LENGTH_LONG
            ).show()
        })

        webview.setWebChromeClient(object : WebChromeClient() {

            //File Upload
            override fun onShowFileChooser(
                mWebView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }
                uploadMessage = filePathCallback
                val intent = fileChooserParams.createIntent()
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    return false
                }
                return true
            }
        })

        //get url from intent extra
        val url :String = intent.getStringExtra("url").toString()

        var historyState: String
        if (intent.hasExtra("history")) {
            historyState = intent.getStringExtra("history").toString()
        } else {
            historyState = "true"
        }

        if(historyState=="false"){
            disableHistory = true
        }

        //Check Internet Connection
        val webViewhelper = WebViewHelper()
        if(webViewhelper.isOnline(applicationContext)){
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.VISIBLE
            //Load site
            webview.loadUrl(url)
        }else{
            updateURLBeforeError()
            webview.loadUrl("file:///android_asset/noconnection.html")
        }

        //close btn
        val btn = findViewById<ImageView>(R.id.btn)
        btn.setOnClickListener {
                finish()
        }

        val searchButton = findViewById<ImageView>(R.id.search2)
        searchButton.setOnClickListener {
            //JavaScript/CSS injection mobile header
            var jsHeader = ""
            if(searchState){
                jsHeader = "document.getElementsByClassName('erf-search')[0].style.display = 'none';"
                searchState = false
            }else{
                jsHeader = "document.getElementsByClassName('erf-search')[0].style.display = 'flex';"
                searchState = true
            }
            webview.loadUrl(
                "javascript:(function() {"
                        + jsHeader +
                        "})()"
            )
        }

        //open in default browser
        val shareButton = findViewById<ImageView>(R.id.share)
        shareButton.setOnClickListener {
            sharePost()
        }

        //enable open in default browser
        if (url.startsWith("https://www.exposedrealfun.com/post/")) {
            shareButton.visibility = View.VISIBLE

            val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            floatingActionButton.setColorFilter(Color.parseColor("#8C8C8C"));
            floatingActionButton.visibility = View.VISIBLE
            val db = SQLlite(this, null)
            floatingActionButton.setOnClickListener {
                if(bookmarkBtnState) {
                    db.deleteBookmark(url)
                    bookmarkBtnState = false

                    bookmarkRemoved()
                }else{
                    if (url.startsWith("https://www.exposedrealfun.com/post/")){
                        db.addBookmark(url,title,"post")
                    }else{
                        db.addBookmark(url,title,"profile")
                    }

                    bookmarkBtnState = true

                    bookmarkAdded()
                }
            }

            // below is the variable for cursor
            // we have called method to get
            // all names from our database
            // and add to name text view
            val cursor = db.getBookmarks()

            // moving the cursor to first position and
            // appending value in the text view
            cursor!!.moveToFirst()

            cursor.moveToFirst();
            while (!cursor.isAfterLast) {
                Link.add(cursor.getString(cursor.getColumnIndex(SQLlite.LINK_COl)))
                cursor.moveToNext();
            }

            // at last we close our cursor
            cursor.close()
            //restore bookmark state

            for (l in Link){
                if(url==l){
                    bookmarkBtnState = true
                    bookmarkAdded()
                }
            }
        }
        if (url.startsWith("https://www.exposedrealfun.com/?q=")) {
            searchButton.visibility = View.VISIBLE
            val searchValue = findViewById<TextView>(R.id.searchValue)
            val sanitizer = UrlQuerySanitizer(url)
            searchValue.text = sanitizer.getValue("q")
        }
    }


    @SuppressLint("ResourceAsColor")
    fun Snackbar(message: String) {
        val contextView = findViewById<View>(R.id.uploadview)
        Snackbar.make(contextView, message, Snackbar.LENGTH_SHORT)
            .setAction("Dismiss") {
                // Responds to click on the action
            }
            .setActionTextColor(ContextCompat.getColor(this, R.color.main_green))
            .show()
    }

    override fun onBackPressed() {

        if(webview.canGoBack()){
            webview.goBack()
        }
        else{
            super@Viewer.onBackPressed()
        }
    }

    fun bookmarkAdded(){
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        floatingActionButton.setColorFilter(Color.parseColor("#285285"));
    }
    fun bookmarkRemoved(){
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        floatingActionButton.setColorFilter(Color.parseColor("#8C8C8C"));
    }

    fun sharePost(){
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing exposure")
        i.putExtra(Intent.EXTRA_TEXT, webview.url)
        startActivity(Intent.createChooser(i, "Share Post"))
    }


    fun updateURLBeforeError(){
        var webviewURL = webview.url.toString()
        if(webviewURL != "file:///android_asset/noconnection.html"){
            urlBeforeError = webviewURL
        }
    }

    fun loadViewer(url: String){

        if(url.startsWith("https://www.exposedrealfun.com/post/")){
            postOpened = true
            leaveTimeStemp = System.currentTimeMillis()
        }



        //Check Internet Connection
        val webViewhelper = WebViewHelper()
        if(webViewhelper.isOnline(applicationContext)){
            if(url.startsWith("https://www.exposedrealfun.com/")){
                val intent = Intent(this, Viewer::class.java)
                intent.putExtra("url", url)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }else{
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }
        }else{
            updateURLBeforeError()
            webview.loadUrl("file:///android_asset/noconnection.html")
        }
    }
}