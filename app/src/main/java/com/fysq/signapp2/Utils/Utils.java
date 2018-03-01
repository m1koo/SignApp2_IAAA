package com.fysq.signapp2.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.fysq.signapp2.Base.PostBean.NorJson;
import com.fysq.signapp2.MyApp;
import com.fysq.signapp2.R;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Miko on 2017/10/17.
 */

public class Utils {

    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    public static File convertIconToFile(Bitmap bitmap) {

        File file = new File(Environment
                .getExternalStorageDirectory() + "/SignApp2/" + "a.png");//将要保存图片的路径
        file.deleteOnExit();
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFileString(File xmlfile) {
        String xmlString;
        byte[] strBuffer = null;
        int flen = 0;
        try {
            InputStream in = new FileInputStream(xmlfile);
            flen = (int) xmlfile.length();
            strBuffer = new byte[flen];
            in.read(strBuffer, 0, flen);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        xmlString = new String(strBuffer);      //构建String时，可用byte[]类型，

        return xmlString;
    }

    public static int getToolBarHeight() {
        int[] attrs = new int[]{R.attr.actionBarSize};
        TypedArray ta = MyApp.context.obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    public static String getAccount() {
        SharedPreferences sp = MyApp.context
                .getSharedPreferences("user", Context.MODE_PRIVATE);
        String account = sp.getString("account", MyApp.context.getString(R.string.defaultAccount));
        return account;
    }

    public static void setAccount(String account) {
        SharedPreferences sp = MyApp.context
                .getSharedPreferences("user", Context.MODE_PRIVATE);
        sp.edit().putString("account", account).apply();
    }

    public interface PostCall {
        void onSuccess(String response);

        void onFail(String error);

    }


    public static String getMac() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    public static void normalQuery(NorJson norJson, final Utils.PostCall postCall) {

        String postJson = new Gson().toJson(norJson);

        RetrofitParameterBuilder builder = RetrofitParameterBuilder.newBuilder();
        builder.addParameter("query", postJson);

        Utils.normalPost(builder, new Utils.PostCall() {
            @Override
            public void onSuccess(String response) {
                postCall.onSuccess(response);
            }

            @Override
            public void onFail(String error) {
                postCall.onFail(error);
            }
        });
    }

    public static void normalPost(RetrofitParameterBuilder builder, final PostCall postCall) {
        Map<String, RequestBody> params = builder.bulider();

        String url = MyApp.context.getString(R.string.host);

        String host = url.substring(0, url.lastIndexOf("/") + 1);
        String path = url.substring(url.lastIndexOf("/") + 1, url.length());


        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(host)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        IRetroNormalService service = retrofit.create(IRetroNormalService.class);
        service.upload(path, params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                postCall.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                postCall.onFail(t.getMessage());
                Log.i("xyz", "失败");
            }
        });
    }


    private static WindowManager wm;

    public static int getScreenWidth() {
        if (wm == null)
            wm = (WindowManager) MyApp.context
                    .getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);
        return p.x;
    }

    public static int getScreenHeight() {
        if (wm == null)
            wm = (WindowManager) MyApp.context
                    .getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);
        return p.y;
    }

    public static void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }


    public static int dpToPx(int dp) {
        Context context = MyApp.getContextObject();

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp
                , context.getResources().getDisplayMetrics());
    }


    public static void addStatusBar(Window window, int currentColor) {
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                Utils.getStatusBarHeight());

        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(currentColor);
        decorViewGroup.addView(statusBarView);
    }


    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = MyApp.getContextObject().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = MyApp.getContextObject().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int spToPx(int dp) {
        Context context = MyApp.context;

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp
                , context.getResources().getDisplayMetrics());
    }
}
