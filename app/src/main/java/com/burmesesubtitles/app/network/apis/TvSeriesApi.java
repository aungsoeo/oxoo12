package com.burmesesubtitles.app.network.apis;

import com.burmesesubtitles.app.models.home_content.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface TvSeriesApi {

    @GET("tvseries")
    Call<List<Video>> getTvSeries(@Header("API-KEY") String apiKey,
                                  @Query("page") int page);


}
