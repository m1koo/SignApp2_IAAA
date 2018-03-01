package com.fysq.signapp2.MVP.LoginModule

import android.content.Intent
import com.fysq.signapp2.Base.BaseMVP.BasePresenter
import com.fysq.signapp2.Base.BaseMVP.BaseView

/**
 * Created by Miko on 2017/3/27.
 */

public interface LoginContract {
    interface View : BaseView<Presenter> {
        fun startActivity(intent: Intent)
        fun showProgress(msg: String)
        fun closeProgress()
        fun closeKeyboard()
        fun getUsername():String
        fun getPassword():String
        fun finishActivity()
    }

    interface Presenter : BasePresenter {
        fun login()
    }
}
