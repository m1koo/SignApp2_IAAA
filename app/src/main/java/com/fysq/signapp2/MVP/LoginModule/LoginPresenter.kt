package com.fysq.signapp2.MVP.LoginModule

import android.content.Context
import android.content.Intent
import com.fysq.signapp2.Base.PostBean.ResultJson
import com.fysq.signapp2.MVP.MainModule.MainActivity
import com.fysq.signapp2.Utils.Utils
import com.fysq.signapp2.Utils.log
import com.fysq.signapp2.Utils.toast
import com.google.gson.Gson
import org.dom4j.io.SAXReader
import java.io.ByteArrayInputStream

/**
 * Created by Miko on 2017/10/18.
 *
 */
class LoginPresenter : LoginContract.Presenter {
    override fun start() {

    }

    lateinit var loginServer: LoginService

    var activity: LoginContract.View

    var context: Context

    constructor(view: LoginContract.View, context: Context) {
        this.activity = view
        this.context = context
        view?.setPresenter(this)
    }

    override fun login() {
        var usernameStr = activity.getUsername()
        var passwordStr = activity.getPassword()

        if (usernameStr == "") {
            toast("用户名为空")
            return
        }
        if (passwordStr == "") {
            toast("密码为空")
            return
        }

        activity.closeKeyboard()
        activity.showProgress("登录中..")
        loginServer = LoginService()

        loginServer.startLogin(usernameStr, passwordStr, object : Utils.PostCall {
            override fun onSuccess(response: String) {
                activity.closeProgress()

                if (response == "") {
                    log("login response is null")
                    return
                }

                if (response.contains("登录成功")) {
                    var resultJson = Gson().fromJson(response, ResultJson::class.java)

                    val account = resultJson.account

                    if (account == null || account == "") {
                        toast("用户名或密码错误,account 为null")

                        log("account null")
                        return
                    }

                    toast("登录成功")

                    //保存本地账号以及account，账号用于保存用户名，避免重复输入，account用于判断当前登录的状态
                    val sp = context.getSharedPreferences("user", Context.MODE_PRIVATE)
                    sp.edit().putString("last_username", usernameStr)
                            .putString("account", account).apply()

                    val result = Gson().fromJson<ResultJson>(response, ResultJson::class.java)

                    val data = result.data

                    val reader = SAXReader()

                    val ins = ByteArrayInputStream(data.toByteArray())
                    val document = reader.read(ins)

                    val root = document.rootElement

                    val name = root.element("aaa").element("姓名").text

                    //保存姓名
                    sp.edit().putString("name", name).apply()

                    val intent = Intent(context, MainActivity::class.java)
                    activity.startActivity(intent)
                    activity.finishActivity()
                } else {
                    toast("用户名或密码错误")
                }

            }

            override fun onFail(error: String) {
                activity.closeProgress()
                toast("网路错误，请检查网络设置")
            }
        })

    }

}