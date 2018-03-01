package com.fysq.signapp2.MVP.LoginModule;

import android.util.Log;

import com.fysq.signapp2.Base.PostBean.NorJson;
import com.fysq.signapp2.Base.PostBean.Params;
import com.fysq.signapp2.MyApp;
import com.fysq.signapp2.R;
import com.fysq.signapp2.Utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miko on 2017/10/18.
 */

public class LoginService {

    private static final String TAG = "LoginService";
    void startLogin(String username, String password, final Utils.PostCall postCall) {
        NorJson norJson = new NorJson();
        norJson.setMac(Utils.getMac());
        norJson.setAccount("AN12345");
        norJson.setId(MyApp.context.getString(R.string.id_login));
        List<Params> paramsList = new ArrayList<>();
        Params param1 = new Params();
        param1.setParam1("帐号");
        param1.setParam2(username);

        Params param2 = new Params();
        param2.setParam1("密码");
        param2.setParam2(password);
        paramsList.add(param1);
        paramsList.add(param2);

        norJson.setParam_list(paramsList);

        String loginPostJson = new Gson().toJson(norJson);

        Log.d("LoginService",loginPostJson);

        Utils.normalQuery(norJson,postCall);
    }
}
