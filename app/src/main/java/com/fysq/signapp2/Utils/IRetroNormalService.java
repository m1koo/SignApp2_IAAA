package com.fysq.signapp2.Utils;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
  * Created by Miko on 2017/7/25.
 */

public interface IRetroNormalService {
    @Multipart
    @POST("{path}")
    Call<String> upload(@Path("path") String path,
                        @PartMap Map<String, RequestBody> params);

}
