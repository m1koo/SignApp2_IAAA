package com.fysq.signapp2.MVP.HIstoryModule

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.fysq.signapp2.MVP.MainModule.MainActivity
import com.fysq.signapp2.R
import com.fysq.signapp2.Utils.Utils
import com.fysq.signapp2.Utils.dp
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        var url = getString(R.string.pkuHost) + "/front.aspx?page=scanhis&account=" + Utils.getAccount()
        webView!!.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                webView!!.visibility = View.GONE
                progress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView!!.visibility = View.VISIBLE
                progress.visibility = View.GONE
                toolbar.title = view?.title
            }
        }

        webView!!.loadUrl(url)
        initToolbar()

    }

    private fun initToolbar() {
        Utils.fullScreen(this)

        toolbar.title = ""
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        //设置沉浸式toolbar
        var param: LinearLayout.LayoutParams = toolbar.layoutParams as LinearLayout.LayoutParams
        param.height = dp(56f) + Utils.getStatusBarHeight()
        toolbar.setPadding(0, Utils.getStatusBarHeight(), 0, 0)
        toolbar.layoutParams = param

        //menu click响应
        toolbar.setOnMenuItemClickListener { item ->
            if (item!!.itemId == R.id.toolbar_scan) {
                startScanner()
            }
            true
        }
        //添加返回按钮并添加响应
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener({ finish() })
    }

    fun startScanner() {
        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra("openScanner", true)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
