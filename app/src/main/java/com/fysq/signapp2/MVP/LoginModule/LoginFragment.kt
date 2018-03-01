package com.fysq.signapp2.MVP.LoginModule

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.fysq.signapp2.R
import com.fysq.signapp2.Utils.KeybordS
import com.fysq.signapp2.Utils.Utils
import com.fysq.signapp2.Utils.dp
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(), LoginContract.View {
    override fun finishActivity() {
        this.activity!!.finish()
    }

    override fun startActivity(intent: Intent) {
        this.context!!.startActivity(intent)
    }

    var loginPresenter: LoginContract.Presenter? = null

    lateinit var progress: ProgressDialog
    override fun setPresenter(presenter: LoginContract.Presenter) {
        this.loginPresenter = presenter
    }

    override fun showProgress(msg: String) {
        progress = ProgressDialog.show(this.context, "提示", msg,
                true, false)
    }

    override fun closeProgress() {
        progress.cancel()
    }

    override fun closeKeyboard() {
        KeybordS.closeKeybord(ed_password, this.context)
    }

    override fun getUsername(): String {
        return ed_username.text.toString()
    }

    override fun getPassword(): String {
        return ed_password.text.toString()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initUserName()
        initEvent()
    }

    private fun initUserName() {
        val sp = context!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        var lastUsername = sp.getString("last_username", "null")
        //如果不为null，将ed——username设置初始值
        if (lastUsername != "null") {
            ed_username.setText(lastUsername)
        }
    }

    private fun initToolbar() {
        var param: LinearLayout.LayoutParams = toolbar.layoutParams as LinearLayout.LayoutParams
        param.height = dp(56f) + Utils.getStatusBarHeight()
        toolbar.setPadding(0, Utils.getStatusBarHeight(), 0, 0)
        toolbar.layoutParams = param
    }

    private fun initEvent() {
        bt_login.setOnClickListener {
            loginPresenter?.login()
        }
    }
}
