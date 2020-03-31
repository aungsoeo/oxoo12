package com.burmesesubtitles.app.network.apis;

import com.burmesesubtitles.app.network.model.SearchModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SearchApi {

    @GET("search")
    Call<SearchModel> getSearchData(@Header("API-KEY") String apiKey,
                                    @Query("q") String query,
                                    @Query("type") String type,
                                    @Query("range_to") int rangeTo,
                                    @Query("range_from") int rangeFrom,
                                    @Query("tv_category_id") int tvCategoryId,
                                    @Query("genre_id") int genreId,
                                    @Query("country_id") int countryId);
}
