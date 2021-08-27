package com.toolsbox.contractor.common.api;




import com.toolsbox.contractor.common.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Administrator on 09/16/2017.
 */
public class ApiClient {
    public static final String CONTENT_TYPE         = "application/json";
    public static final String HEADERFIELD_ACCEPT   = "application/json";

    private static ApiInterface apiService;

    public static ApiInterface getApiClient() {
        if (apiService == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .readTimeout(20, TimeUnit.SECONDS).connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS));
            OkHttpClient okHttpClient = clientBuilder.build();
            Retrofit restAdapter = new Retrofit.Builder()
                    .baseUrl(Constant.ROOT_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = restAdapter.create(ApiInterface.class);
        }
        return apiService;
    }


}
