package com.fysq.signapp2.MVP.CompoundPdfPreviewModul;/*
 * Created by zd on 2016/7/18.
 */

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface IUpldIconService {
    @Multipart
    @POST("{path}")
    Call<String> doUpload(@Part("query") String json, @Path("path") String path,
                          @Part MultipartBody.Part file);
}
