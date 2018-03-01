package com.fysq.signapp2.Base.BaseView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.fysq.signapp2.R
import kotlinx.android.synthetic.main.inflate_toobar.view.*

/**
 * Created by Miko on 2017/11/15.
 */

class MyToolbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : android.support.v7.widget.Toolbar(context, attrs, defStyleAttr) {

    var showBack: Boolean = false
        set(value){
            if(!value){
                back.visibility = View.GONE
            }else{
                back.visibility = View.VISIBLE
            }
            Log.i("xyz","back")
        }
    var title :String = ""
        set(value) {
            tv_title.text = value
            Log.i("xyz","title")
        }
    var back: LinearLayout
    var t:TextView

    init {
        View.inflate(this.context,
                R.layout.inflate_toobar, this)

        back = ln_back
        t = tv_title
    }
}
