package com.fysq.signapp2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.edu.pku.pkuiaaa_android.IAAA_Authen;

public class IAAAActivity extends AppCompatActivity {

    static final String EXTRA_iAAA_APPID = "iAAA_APPID";

    static final String RESULT_CANCEL = "cancel";
    static final String RESULT_SUCCESS = "success";

    static final String EXTRA_iAAA_RESULT = "iAAA_RESULT";
    static final String EXTRA_iAAA_UID = "iAAA_UID";
    static final String EXTRA_iAAA_TOKEN = "iAAA_TOKEN";

    final int ACTIVITY_iAAA = 1000;  //用户自行定义
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iaaa);
        Intent intent = new Intent(this, IAAA_Authen.class);
        intent.putExtra(EXTRA_iAAA_APPID, "reagent");   // 填写已备案的APP ID, 其中EXTRA_iAAA_APPID请见下方定义

//intent.putExtra(EXTRA_iAAA_UID, "TestUserName"); //传入用户名。如不需要，可以忽略，其中EXTRA_iAAA_UID请见下方定义

        startActivityForResult(intent, ACTIVITY_iAAA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACTIVITY_iAAA) {

            if (resultCode == Activity.RESULT_OK) {

                Bundle extras = data.getExtras();

                String result = extras.getString(EXTRA_iAAA_RESULT);

                if(result.equals(RESULT_CANCEL)) {
                    Log.i("xyz","用户取消登录！");
                }else {

                    String uid = extras.getString(EXTRA_iAAA_UID);

                    String token = extras.getString(EXTRA_iAAA_TOKEN);

                    Log.i("xyz","登录成功！" + "uid: " + uid + "  " + "token: " + token);
                }

            }else {

                Log.i("xyz","操作失败！");
            }
        }
    }


}
