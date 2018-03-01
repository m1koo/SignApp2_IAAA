package com.fysq.signapp2.MVP.CompoundPdfPreviewModul

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.fysq.signapp2.Base.PostBean.NorJson
import com.fysq.signapp2.Base.PostBean.Params
import com.fysq.signapp2.Base.PostBean.ResultJson
import com.fysq.signapp2.MVP.MainModule.MainActivity
import com.fysq.signapp2.MVP.OrderPreviewModule.OrderService
import com.fysq.signapp2.MyApp
import com.fysq.signapp2.R
import com.fysq.signapp2.Utils.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_compound.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File

class CompoundActivity : AppCompatActivity() {

    //初始化为""，当界面加载完毕即pdf保存到本地后，初始化
    var path: String = ""

    var dialog: ProgressDialog? = null
    private fun doUpload() {
        dialog = ProgressDialog.show(this, "请稍后..", "正在上传签名文件",
                true, false)!!
        upload()
    }

    fun upload() {
        val sp = MyApp.context.getSharedPreferences("order", Context.MODE_PRIVATE)
        val orderId = sp.getString("orderId", "error")

        val norJson = NorJson()
        norJson.mac = Utils.getMac()
        norJson.account = Utils.getAccount()
        norJson.id = "android_mid_upload"
        val paramsList = ArrayList<Params>()

        val param2 = Params()
        param2.param1 = "出库单号"
        param2.param2 = orderId
        paramsList.add(param2)

        norJson.param_list = paramsList

        val url = this.getString(R.string.host)
        val host = url.substring(0, url.lastIndexOf("/") + 1)
        val p = url.substring(url.lastIndexOf("/") + 1, url.length)


        val retrofit = Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        val service = retrofit.create(IUpldIconService::class.java!!)

        val file = File(this.path)
        if (!file.exists()) {
            Toast.makeText(this, "所选文件不存在", Toast.LENGTH_SHORT).show()
            return
        }

        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("file", "file_$orderId.pdf", requestFile)


        val call = service.doUpload(Gson().toJson(norJson), p, body)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                dialog!!.cancel()
                if (response == null) {
                    toast("响应为空，请与程序员联系")
                    return
                }
                val re = response.body()

                val resultJson = Gson().fromJson<ResultJson>(re,
                        ResultJson::class.java)
                val message = resultJson.message
                if (message == null || message == "") {
                    toast("响应信息为空，请与程序员联系")
                    return
                }
                var d = AlertDialog.Builder(bt_upload.context)
                        .setMessage(message)
                        .setPositiveButton("确定") { dialog, which ->
                            val intent = Intent(bt_upload.context, MainActivity::class.java)
                            intent.putExtra("openScanner", true)
                            startActivity(intent)

                        }.create()
                d.show()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                dialog!!.cancel()

                var d = AlertDialog.Builder(bt_upload.context)
                        .setMessage("未知网络错误,检查网络设置")
                        .setPositiveButton("确定") { dialog, which ->
                            dialog.cancel()
                        }.create()
                d.show()
                toast("未知网络错误")
            }
        })

    }
    private fun initToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        //添加返回按钮并添加响应
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener({ finish() })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compound)

        val id = intent.getStringExtra("id")
        val service = OrderService()

        initToolbar()

        bt_upload.setOnClickListener({

            AlertDialog.Builder(bt_upload.context).setTitle("确认上传签名文件吗")
                    .setPositiveButton("确认") { dialog, _
                        ->
                        dialog.cancel()
                        doUpload()
                    }.setNegativeButton("取消") { dialog, _
                ->
                dialog.cancel()
            }.create().show()
        })


        service.getOrderMsg(id, object : Utils.PostCall {
            override fun onSuccess(response: String?) {

                val re = response!!

                val resultBean = Gson().fromJson<ResultJson>(re, ResultJson::class.java)

                if (resultBean == null) {
                    AlertDialog.Builder(bt_upload.context).setTitle("提示")
                            .setMessage("获取订单错误，二维码不正确")
                            .setPositiveButton("确认") { dialog, _ ->
                                dialog.dismiss()
                                finish()
                            }.create().show()
                    return
                }

                val orderMsg = resultBean.data

                doAsync {
                    path = OrderService().parseOrderXml(orderMsg, true)

                    uiThread {

                        pdfView.fromFile(File(path)).enableDoubletap(true)
                                .enableSwipe(true)
                                .load()
                        rl_progress.visibility = View.GONE
                        bt_upload.visibility = View.VISIBLE
                    }
                }


            }

            override fun onFail(error: String?) {

                rl_progress.visibility = View.GONE
                bt_upload.visibility = View.GONE

                AlertDialog.Builder(bt_upload.context).setTitle("提示")
                        .setMessage("出现未知网络错误，请检查网络设置")
                        .setPositiveButton("确认") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }.create().show()
            }

        })
    }
}
