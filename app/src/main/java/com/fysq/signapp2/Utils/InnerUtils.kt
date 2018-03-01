package com.fysq.signapp2.Utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import com.fysq.signapp2.Base.BaseMVP.BasePresenter
import com.fysq.signapp2.MyApp

/**
 * Created by Miko on 2017/10/17.
 *
 */
inline fun Fragment.dp(dp: Float): Int {

    val context = MyApp.getContextObject()

    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            context.getResources().getDisplayMetrics()).toInt()

}

inline fun Activity.dp(dp: Float): Int {

    val context = MyApp.getContextObject()

    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            context.getResources().getDisplayMetrics()).toInt()

}

inline fun BasePresenter.toast(str: String) {
    Toast.makeText(MyApp.context, str, Toast.LENGTH_SHORT).show()
}

inline fun BasePresenter.log(str: String) {
    Log.i("xyz", str)
}