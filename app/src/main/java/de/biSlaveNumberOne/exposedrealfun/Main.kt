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
import android.net.http.SslError
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.beust.klaxon.Klaxon
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.Serializable
import java.net.URISyntaxException


class Main : AppCompatActivity() {
    lateinit var webview: WebView
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    val REQUEST_SELECT_FILE = 100
    var onLogin = false
    var leaveTimeStemp =  System.currentTimeMillis()
    var postOpened = false
    var disableOnClickEvents = false
    var isUpdateAvailible = false
    var urlBeforeError = ""
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

    @Serializable
    data class Data(val version: String)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home)

        val refresh = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        refresh.setOnRefreshListener {
            if(webview.url == "file:///android_asset/noconnection.html"){
                webview.loadUrl(urlBeforeError)
            }else{
                val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.VISIBLE
                webview.visibility = View.INVISIBLE
                webview.reload()
                searchState = false
            }
            refresh.isRefreshing = false
        }

        webview = findViewById(R.id.webview)
        webview.settings.javaScriptEnabled = true
        webview.settings.allowFileAccess = true
        webview.settings.domStorageEnabled = true
        webview.settings.allowContentAccess = true
        webview.webChromeClient = WebChromeClient()
        webview.setBackgroundColor(Color.parseColor("#f7f7f7"))

        //Darkmode
        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(webview.settings, WebSettingsCompat.FORCE_DARK_ON)
                }
                webview.setBackgroundColor(Color.parseColor("#212121"))
            }
            Configuration.UI_MODE_NIGHT_NO -> {}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }

        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)


        //Floating btn
        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)
        fab.setOnClickListener {
            loadViewer("https://www.exposedrealfun.com/new")
        }
        val search = findViewById<ImageView>(R.id.search)
        search.setOnClickListener {
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
                    bottomNavigationView.selectedItemId = R.id.home
                } else
                    if (url.startsWith("https://www.exposedrealfun.com/?")) {

                    } else
                        if (url.startsWith("https://www.exposedrealfun.com/post/clear.php")) {
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    bottomNavigationView.selectedItemId = R.id.more
                                },
                                300 // value in milliseconds
                            )
                        }else
                        {
                            loadViewer(url)
                            webview.stopLoading()
                            return false
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

                if(url == "https://www.exposedrealfun.com/" || url?.startsWith("https://www.exposedrealfun.com/?") == true){
                    disableOnClickEvents = true
                    bottomNavigationView.selectedItemId = R.id.home
                    disableOnClickEvents = false
                }

                super.onPageStarted(view, url, favicon)
            }


            override fun onPageFinished(webView: WebView?, url: String?) {
                //Save cookies for login and popup
                CookieManager.getInstance().flush()


                //JavaScript/CSS injection mobile header
                val cssHeader = "html{-webkit-tap-highlight-color: transparent;}.erf-homepage-pagination{overflow: auto;}.d-flex { overflow: auto; } .filter-dropmenu{overflow: unset;} /*custom-search-bar*/ .erf-search { position: fixed; left: 0; top: 0; right: 0; background-color: #ffffff; z-index: 99999; padding: 20px; box-shadow: 1px 1px 11px 2px #000000a8; } /*custom-search-bar end*/" //your css as String
                val jsHeader = "var style = document.createElement('style'); style.innerHTML = '$cssHeader'; " +
                        "document.getElementsByTagName('nav')[0].style.display = 'none';" +
                        "document.head.appendChild(style);"
                webview.loadUrl(
                    "javascript:(function() {"
                            + jsHeader +
                            "})()"
                )


                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                        progressBar.visibility = View.INVISIBLE
                        webview.visibility = View.VISIBLE
                    },
                    250 // value in milliseconds
                )

                Handler(Looper.getMainLooper()).postDelayed(
                    {

                    },
                    1000 // value in milliseconds
                )
                searchState = false

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


        webview.webChromeClient = object : WebChromeClient() {

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
        }

        //Check Internet Connection
        val webViewhelper = WebViewHelper()
        if(webViewhelper.isOnline(applicationContext)){
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.VISIBLE
            //Load site
            webview.loadUrl("https://www.exposedrealfun.com/")

            //Show update popup

            //Call api to get current app version
            val thread = Thread {
                try {
                    val versionCode = BuildConfig.VERSION_CODE
                    val json = Request().run("http://exxxpose-extend.bplaced.net/api/getCurrentAppVersion/")

                    val result = Klaxon()
                        .parse<Data>(json)

                    if (result != null) {
                        if(result.version != versionCode.toString()){
                            isUpdateAvailible = true
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            thread.start()

            //Check if a update is available
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    /* while (true){
                         if(isUpdateAvailible){
                             showUpdatePopup()
                             return@postDelayed
                         }
                     }*/
                    if(isUpdateAvailible){
                        showUpdatePopup()
                    }
                },
                2000 // value in milliseconds
            )
        }else{
            updateURLBeforeError()
            webview.loadUrl("file:///android_asset/noconnection.html")
        }


        //Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.home -> {
                    if(!disableOnClickEvents){
                        //Check Internet Connection
                        if(webViewhelper.isOnline(applicationContext)){
                            //Load site
                            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                            progressBar.visibility = View.VISIBLE
                            webview.visibility = View.INVISIBLE

                            webview.loadUrl("https://www.exposedrealfun.com/")
                            searchState = false
                        }else{
                            updateURLBeforeError()
                            webview.loadUrl("file:///android_asset/noconnection.html")
                        }
                    }
                    true
                }
                R.id.more -> {
                    val bottomSheetDialog = BottomSheetDialog(this)
                    bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_menu_layout)


                    bottomSheetDialog.findViewById<LinearLayout>(R.id.bookmarks)
                        ?.setOnClickListener(View.OnClickListener {
                            val intent = Intent(this, Bookmarks::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            bottomSheetDialog.dismiss()
                        })

                    bottomSheetDialog.findViewById<LinearLayout>(R.id.history)
                        ?.setOnClickListener(View.OnClickListener {
                            val intent = Intent(this, History::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            bottomSheetDialog.dismiss()
                        })
                    bottomSheetDialog.findViewById<LinearLayout>(R.id.terms)
                        ?.setOnClickListener(View.OnClickListener {
                            loadViewer("https://www.exposedrealfun.com/terms")
                            bottomSheetDialog.dismiss()
                        })

                    bottomSheetDialog.findViewById<LinearLayout>(R.id.faq)
                        ?.setOnClickListener(View.OnClickListener {
                            loadViewer("https://www.exposedrealfun.com/faq")
                            bottomSheetDialog.dismiss()
                        })

                    bottomSheetDialog.findViewById<LinearLayout>(R.id.discord)
                        ?.setOnClickListener(View.OnClickListener {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/E4bNcsJGs3"))
                            startActivity(browserIntent)
                            bottomSheetDialog.dismiss()
                        })

                    bottomSheetDialog.findViewById<LinearLayout>(R.id.about)
                        ?.setOnClickListener(View.OnClickListener {
                            val intent = Intent(this, About::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            bottomSheetDialog.dismiss()
                        })


                    bottomSheetDialog.show()

                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            val url = webview.url

                            if(url == "https://www.exposedrealfun.com/" || url?.startsWith("https://www.exposedrealfun.com/?") == true){
                                disableOnClickEvents = true
                                bottomNavigationView.selectedItemId = R.id.home
                                disableOnClickEvents = false
                            }

                            if(url == "https://www.exposedrealfun.com/fag-of-the-day"){
                                disableOnClickEvents = true
                                bottomNavigationView.selectedItemId = R.id.FagOfTheDay
                                disableOnClickEvents = false
                            }
                        },
                        500 // value in milliseconds
                    )

                    true
                }
                R.id.FagOfTheDay -> {
                    if(!disableOnClickEvents){
                        //Check Internet Connection
                        if(webViewhelper.isOnline(applicationContext)){
                            //Load site
                            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                            progressBar.visibility = View.VISIBLE
                            webview.visibility = View.INVISIBLE

                            webview.loadUrl("https://www.exposedrealfun.com/fag-of-the-day")
                            searchState = false
                        }else{
                            updateURLBeforeError()
                            webview.loadUrl("file:///android_asset/noconnection.html")
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    fun Snackbar(message: String) {
        val contextView = findViewById<View>(R.id.webview)
        Snackbar.make(contextView, message, Snackbar.LENGTH_SHORT)
            .setAction("Dismiss") {
                // Responds to click on the action
            }
            .setActionTextColor(ContextCompat.getColor(this, R.color.main_green))
            .show()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onResume() {
        super.onResume()

        val url = webview.url


        //Update bottom nav

        if(url == "https://www.exposedrealfun.com/" || url?.startsWith("https://www.exposedrealfun.com/?") == true){
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            disableOnClickEvents = true
            bottomNavigationView.selectedItemId = R.id.home
            disableOnClickEvents = false
        }

        if(url == "https://www.exposedrealfun.com/fag-of-the-day"){
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            disableOnClickEvents = true
            bottomNavigationView.selectedItemId = R.id.FagOfTheDay
            disableOnClickEvents = false
        }


    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Really Exit?")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton("No", null)
            .setPositiveButton("Yes"
            ) { _, _ -> super@Main.onBackPressed() }.create().show()
    }

    fun showUpdatePopup(){
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout_update)


        bottomSheetDialog.findViewById<LinearLayout>(R.id.getTheUpdate)
            ?.setOnClickListener(View.OnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MobileAppDev451/exposedrealfun-Android"))
                startActivity(browserIntent)
                bottomSheetDialog.dismiss()
            })

        bottomSheetDialog.findViewById<LinearLayout>(R.id.Dismiss)
            ?.setOnClickListener(View.OnClickListener {
                bottomSheetDialog.dismiss()
            })


        bottomSheetDialog.show()
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

        if(url.startsWith("https://www.exposedrealfun.com/")){
            val intent = Intent(this, Viewer::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }else{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
}








