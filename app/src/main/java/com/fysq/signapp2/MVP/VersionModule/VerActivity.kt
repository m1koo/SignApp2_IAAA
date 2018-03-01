package com.fysq.signapp2.MVP.VersionModule

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fysq.signapp2.R
import com.fysq.signapp2.Utils.Utils
import kotlinx.android.synthetic.main.activity_ver.*

class VerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver)

        val version = Utils.getVerName(this)
        val versionStr = "当前版本：" + version
        tv_currentVersion.text = versionStr
    }
}
