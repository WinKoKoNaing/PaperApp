package com.pecake.paper.Fragments;

import com.pecake.paper.JSONResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {
    @GET("things/Pg0Z.json")
    Call<JSONResponse> getJSON();
}