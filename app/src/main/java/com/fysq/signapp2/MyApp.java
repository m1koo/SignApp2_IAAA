package com.fysq.signapp2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.fysq.signapp2.Base.PostBean.UpdateJson;
import com.fysq.signapp2.Utils.MyActivityManager;
import com.fysq.signapp2.Utils.Utils;
import com.google.gson.Gson;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Miko on 2017/10/17.
 */

public class MyApp extends MultiDexApplication {
    public static Context context;

    public static Context getContextObject() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        initCurrentActivityListener();

        initHotFix();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }


    private void initHotFix() {
        String code = Utils.getVerName(getApplicationContext());
        SophixManager.getInstance().setContext(this)
                .setAppVersion(code)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.i("xyz", "补丁加载成功");
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            Log.i("xyz", "表明新补丁生效需要重启. 开发者可提示用户或者强制重启");

                            final Activity acti = MyActivityManager
                                    .getInstance().getCurrentActivity();

                            if (acti == null) {
                                Log.i("xyz", "null");
                                return;
                            }

                            acti.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    AlertDialog dialog = new AlertDialog
                                            .Builder(acti)
                                            .setTitle("发现必要的版本更新")
                                            .setMessage("请重启App应用更新")
                                            .setPositiveButton("确认重启", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    android.os.Process.killProcess(android.os.Process.myPid());
                                                }
                                            })
                                            .create();
                                    dialog.show();

                                }
                            });


                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                            Log.i("xyz", "其它错误信息, 查看PatchStatus类说明");
                        }
                    }
                }).initialize();
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    private void initCurrentActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                MyActivityManager.getInstance().setCurrentActivity(activity);

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
