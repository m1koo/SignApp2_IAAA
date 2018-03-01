package com.fysq.signapp2.MVP.HandWriteModule

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fysq.signapp2.MVP.CompoundPdfPreviewModul.CompoundActivity
import com.fysq.signapp2.R
import com.fysq.signapp2.Utils.Utils
import kotlinx.android.synthetic.main.activity_hand_writing.*


class HandWritingActivity : AppCompatActivity() {

    val TAG = "HandWritingActivity"
    lateinit var progress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_writing)

        val orderId = intent.getStringExtra("id")
        bt_rewrite.setOnClickListener({
            sign_view.clear()
        })

        bt_confirm.setOnClickListener({
            val bit = sign_view.signatureBitmap
            Utils.convertIconToFile(bit)

            val intent = Intent(this,CompoundActivity::class.java)
            intent.putExtra("id",orderId)
            startActivity(intent)
        })
        sign_view.setMaxWidth(8f)
    }

    fun showProgress(msg: String) {
        progress = ProgressDialog.show(this, "提示", msg,
                true, false)
    }

    fun closeProgress() {
        progress.cancel()
    }
}
