package com.fysq.signapp2.MVP.LoginModule

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.Toast
import com.fysq.signapp2.MVP.MainModule.MainActivity
import com.fysq.signapp2.R
import com.fysq.signapp2.Utils.ActivityUtils
import com.fysq.signapp2.Utils.Utils


class LoginActivity : AppCompatActivity() {

    var presenter: LoginContract.Presenter? = null
    private var mIsExit: Boolean = false


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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Utils.getAccount() != getString(R.string.defaultAccount)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        Utils.fullScreen(this)

        val loginFragment = LoginFragment()
        Utils.addStatusBar(this.window, Color.parseColor("#BA0001"))
        ActivityUtils.addFragmentToRoot(supportFragmentManager,
                loginFragment, R.id.id_fragment_container, null)

        presenter = LoginPresenter(loginFragment, this)
    }
}
