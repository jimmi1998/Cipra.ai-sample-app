package com.cipra.app.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("takehome/signin")
    Call<ResponseBody> signin(@Query("email") String email, @Query("password") String password);
}
