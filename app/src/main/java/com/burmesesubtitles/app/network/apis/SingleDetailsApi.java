package com.burmesesubtitles.app.network.apis;


import com.burmesesubtitles.app.models.single_details.SingleDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SingleDetailsApi {

    @GET("single_details")
    Call<SingleDetails> getSingleDetails(@Header("API-KEY") String key,
                                         @Query("type") String type,
                                         @Query("id") String id);



}
