package com.fysq.signapp2.MVP.MainModule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.widget.LinearLayout
import android.widget.Toast
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.UIData
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener
import com.fysq.signapp2.Base.PostBean.UpdateJson
import com.fysq.signapp2.MVP.HIstoryModule.HistoryActivity
import com.fysq.signapp2.MVP.LoginModule.LoginActivity
import com.fysq.signapp2.MVP.OrderPreviewModule.OrderPreviewActivity
import com.fysq.signapp2.MVP.VersionModule.VerActivity
import com.fysq.signapp2.MyApp
import com.fysq.signapp2.R
import com.fysq.signapp2.Utils.DeleteFileUtil
import com.fysq.signapp2.Utils.Utils
import com.fysq.signapp2.Utils.dp
import com.google.gson.Gson
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionListener
import io.github.xudaojie.qrcodelib.CaptureActivity
import kotlinx.android.synthetic.main.activity_main.*
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    val REQUEST_QR_CODE = 300

    private var mIsExit: Boolean = false


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                finish()
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show()
                mIsExit = true
                Handler().postDelayed(Runnable { mIsExit = false }, 2000)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) {
            return
        }
        //完成一次操作后，直接返回到扫描的界面
        val openScanner = intent.getBooleanExtra("openScanner", false)
        if (openScanner) {
            startScanner()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermission()
        initToolbar()

        val sp = getSharedPreferences("user", Context.MODE_PRIVATE)
        val name = sp.getString("name", "未命名")
        tv_name.text = name

        bt_start_scan.setOnClickListener({
            startScanner()
        })

        bt_logout.setOnClickListener({
            Utils.setAccount(getString(R.string.defaultAccount))
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })

        rl_history.setOnClickListener({
            startActivity(Intent(this, HistoryActivity::class.java))
        })

        rl_version.setOnClickListener({
            startActivity(Intent(this, VerActivity::class.java))
        })
//       防止文件过多，打开时删除文件夹
        DeleteFileUtil.deleteDirectory(Environment
                .getExternalStorageDirectory().path + "/SignApp2/")
        var requestUrl = "http://www.labase.cn/weixin/update"

        var builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(requestUrl)
                .request(object : RequestVersionListener {
                    override fun onRequestVersionSuccess(result: String?): UIData? {
                        //拿到服务器返回的数据，解析，拿到downloadUrl和一些其他的UI数据
                        //如果是最新版本直接return null
                        Log.i("xyz", "版本信息 " + result!!)

                        if (result === "") {
                            return null
                        }
                        val bean = Gson().fromJson(result, UpdateJson::class.java)
                        val serverVerCode = Integer.parseInt(bean.version)
                        val localVerCode = Utils.getVersionCode(MyApp.context)
                        return if (serverVerCode > localVerCode) {
                            UIData.create().setTitle("发现必须更新的新版本").setContent("是否立即更新?").
                                    setDownloadUrl(bean.url)
                        } else {
                            null
                        }
                    }

                    override fun onRequestVersionFailure(message: String) {
                        Log.i("xyz", "获取版本信息失败")
                    }
                })
        builder.forceUpdateListener = ForceUpdateListener { forceUpdate() }
        builder.excuteMission(this)


    }

    private fun forceUpdate() {
        Toast.makeText(this, "force update handle", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun startScanner() {
        val i = Intent(this, CaptureActivity::class.java)
        startActivityForResult(i, REQUEST_QR_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUEST_QR_CODE
                && data != null) {
            val result = data.getStringExtra("result")
            val intent = Intent(this, OrderPreviewActivity::class.java)
            intent.putExtra("orderNumber", result)
            startActivity(intent)
        }
    }

    fun initPermission() {
        val listener = object : PermissionListener {
            override fun onSucceed(requestCode: Int, grantedPermissions: List<String>) {
                // Successfully.
                if (requestCode == 200) {
                }
            }

            override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
                // Failure.
                if (requestCode == 200) {
                    toast("权限申请失败，无法使用摄像头")
                }
            }
        }
        AndPermission.with(this)
                .requestCode(200)
                .permission(Permission.CAMERA, Permission.STORAGE)
                .callback(listener)
                .start()
    }

    private fun initToolbar() {
        Utils.fullScreen(this)

        toolbar.title = "扫描二维码"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        var param: LinearLayout.LayoutParams = toolbar.layoutParams as LinearLayout.LayoutParams
        param.height = dp(56f) + Utils.getStatusBarHeight()
        toolbar.setPadding(0, Utils.getStatusBarHeight(), 0, 0)
        toolbar.layoutParams = param

        val drawerToggle = DuoDrawerToggle(this,
                drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.setDrawerListener(drawerToggle)
        drawerToggle.syncState()

        menu_toolbar.setPadding(0, Utils.getStatusBarHeight(), 0, 0)
        menu_toolbar.layoutParams = param

        toolbar.setOnMenuItemClickListener { item ->
            if (item!!.itemId == R.id.toolbar_scan) {
                startScanner()
            }
            true
        }
    }
}
