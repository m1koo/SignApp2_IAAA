package com.fysq.signapp2.MVP.OrderPreviewModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.fysq.signapp2.Base.PostBean.NorJson
import com.fysq.signapp2.Base.PostBean.Params
import com.fysq.signapp2.Base.PostBean.ResultJson
import com.fysq.signapp2.MVP.CompoundPdfPreviewModul.IUpldIconService
import com.fysq.signapp2.MVP.HandWriteModule.HandWritingActivity
import com.fysq.signapp2.MyApp
import com.fysq.signapp2.R
import com.fysq.signapp2.Utils.DeleteFileUtil
import com.fysq.signapp2.Utils.Utils
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_order_preview.*
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
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

class OrderPreviewActivity : AppCompatActivity() {

    lateinit var dialog: AlertDialog
    var orderNum = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_preview)

        rl_progress.visibility = View.VISIBLE
        tv_hint.text = "正在获取货单信息.."
        rl_bottom.visibility = View.GONE

        orderNum = intent.getStringExtra("orderNumber")

        initList()

        bt_sign.setOnClickListener {
            val intent = Intent(this, HandWritingActivity::class.java)
            intent.putExtra("id", orderNum)
            startActivity(intent)
        }
        bt_camera.setOnClickListener({
            PictureSelector.create(this)
                    .openCamera(PictureMimeType.ofImage())
                    .previewImage(true)
                    .forResult(PictureConfig.CHOOSE_REQUEST)
        })
        bt_cancle.setOnClickListener({
            finish()
        })
        initToolbar()

    }


    private fun initToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        //添加返回按钮并添加响应
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener({ finish() })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    val compressedPath = selectList[0].path

                    dialog = AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("确认要上传此签名文件么？")
                            .setPositiveButton("确认") { dialog, which ->
                                upLoadPhoto(compressedPath)
                                dialog.dismiss()
                            }.setNegativeButton("取消") { dialog, which ->
                        dialog.dismiss()
                    }.create()
                    dialog.show()
                }
            }
        }
    }

    fun upLoadPhoto(path: String) {

        rl_progress.visibility = View.VISIBLE
        tv_hint.text = "正在上传请稍后.."
        rl_bottom.visibility = View.GONE
        pdfView.visibility = View.GONE

        var targetPath = Environment
                .getExternalStorageDirectory().path + "/SignApp2/"
//        压缩


        Luban.with(MyApp.context)
                .load(path)                                   // 传人要压缩的图片列表
                .setTargetDir(targetPath) // 设置压缩后文件存储位置
                .setCompressListener(object : OnCompressListener { //设置回调
                    override fun onStart() {
                    }

                    override fun onSuccess(file: File) {
                        var parent = File(path).parent
                        DeleteFileUtil.deleteDirectory(parent)
                        upLoad(file)
                    }

                    override fun onError(e: Throwable) {
                        rl_progress.visibility = View.INVISIBLE
                        rl_bottom.visibility = View.VISIBLE
                        pdfView.visibility = View.VISIBLE
                    }
                }).launch()    //启动压缩z

    }

    fun upLoad(file: File) {
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
        val path = url.substring(url.lastIndexOf("/") + 1, url.length)


        val retrofit = Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        val service = retrofit.create(IUpldIconService::class.java!!)

        if (!file.exists()) {
            rl_progress.visibility = View.INVISIBLE
            rl_bottom.visibility = View.VISIBLE
            pdfView.visibility = View.VISIBLE
            Toast.makeText(this, "所选文件不存在", Toast.LENGTH_SHORT).show()
            return
        }

        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("camera", "camera_$orderId.jpg", requestFile)

        val call = service.doUpload(Gson().toJson(norJson), path, body)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                rl_progress.visibility = View.INVISIBLE
                rl_bottom.visibility = View.VISIBLE
                pdfView.visibility = View.VISIBLE
                if (response == null) {
                    toast("后台程序出现未知错误，请与管理员联系")
                    return
                }
                val re = response.body()

                val resultJson = Gson().fromJson<ResultJson>(re,
                        ResultJson::class.java)
                val message = resultJson.message
                if (message == null || message == "") {
                    toast("后台程序出现未知错误，请与管理员联系")
                    return
                }
                toast(message)

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                toast("未知网络错误,请检查网络设置")
                rl_progress.visibility = View.INVISIBLE
                rl_bottom.visibility = View.VISIBLE
                pdfView.visibility = View.VISIBLE
            }
        })


    }

    private fun initList() {
        val service = OrderService()
        service.getOrderMsg(orderNum, object : Utils.PostCall {
            override fun onSuccess(response: String?) {

                Log.i("xyz", response)
                val re = response!!

                val resultBean = Gson().fromJson<ResultJson>(re, ResultJson::class.java)

                if (resultBean == null || resultBean.data == "") {
                    AlertDialog.Builder(bt_sign.context).setTitle("提示")
                            .setMessage("扫描的二维码或条码不正确")
                            .setPositiveButton("确认") { dialog, _ ->
                                dialog.dismiss()
                                finish()
                            }.create().show()
                    return
                }

                val orderMsg = resultBean.data

                doAsync {
                    val path = OrderService().parseOrderXml(orderMsg, false)

                    uiThread {

                        pdfView.fromFile(File(path)).enableDoubletap(true)
                                .enableSwipe(true)
                                .load()
                        rl_progress.visibility = View.GONE
                        rl_bottom.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFail(error: String?) {

                rl_progress.visibility = View.GONE
                rl_bottom.visibility = View.GONE

                AlertDialog.Builder(bt_sign.context).setTitle("提示")
                        .setMessage("出现未知网络错误，请检查网络设置")
                        .setPositiveButton("确认") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }.create().show()
            }

        })
    }
}
