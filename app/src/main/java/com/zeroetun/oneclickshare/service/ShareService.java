package com.zeroetun.oneclickshare.service;

import android.net.Uri;
import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by françois on 28/01/2017.
 */

public class ShareService {


    public interface ApiInterface {
        @Multipart
        @POST("/upload")
        Call<ResponseBody> upload (@Part MultipartBody.Part file );
    }

    public interface TestInterface {
        @GET("/info.0.json")
        public Call<ResponseBody> get();
    }

    private static final String SERVER_URL = "https://oneclick-yyxrgmmxrr.now.sh";


    private static final String TEST_URL = "https://oneclick-tqwprecjwd.now.sh";


    private Uri mUri;

    public ShareService(Uri uri){
        mUri = uri;
    }

    public static ApiInterface servicesInterface = getService();
    public static TestInterface testInterface = getTestService();


    private static ApiInterface getService() {
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
        ).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .client(client)
                 .build();

        return retrofit.create(ApiInterface.class);

    }


    private static TestInterface getTestService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TEST_URL)
                .build();

        return retrofit.create(TestInterface.class);

    }

    public void upload() {

        File file = new File(mUri.getPath());
        MultipartBody.Part fbody = MultipartBody.Part.createFormData("file",file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        ApiInterface api = servicesInterface;
        Call<ResponseBody> call = api.upload(fbody);
        Log.i("Upload","start upload" );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("Upload","success : " + response.message()  + " / code :" + response.code() );
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("Upload","failure",t);
            }
        });

    }
}
