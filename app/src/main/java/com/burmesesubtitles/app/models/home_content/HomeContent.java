
package com.burmesesubtitles.app.models.home_content;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HomeContent {

    @SerializedName("slider")
    @Expose
    private Slider slider;
    @SerializedName("all_country")
    @Expose
    private List<AllCountry> allCountry = null;
    @SerializedName("all_genre")
    @Expose
    private List<AllGenre> allGenre = null;
    @SerializedName("featured_tv_channel")
    @Expose
    private List<FeaturedTvChannel> featuredTvChannel = null;
    @SerializedName("latest_movies")
    @Expose
    private List<LatestMovie> latestMovies = null;
    @SerializedName("latest_tvseries")
    @Expose
    private List<LatestTvseries> latestTvseries = null;
    @SerializedName("features_genre_and_movie")
    @Expose
    private List<FeaturesGenreAndMovie> featuresGenreAndMovie = null;

    public Slider getSlider() {
        return slider;
    }

    public void setSlider(Slider slider) {
        this.slider = slider;
    }

    public List<AllCountry> getAllCountry() {
        return allCountry;
    }

    public void setAllCountry(List<AllCountry> allCountry) {
        this.allCountry = allCountry;
    }

    public List<AllGenre> getAllGenre() {
        return allGenre;
    }

    public void setAllGenre(List<AllGenre> allGenre) {
        this.allGenre = allGenre;
    }

    public List<FeaturedTvChannel> getFeaturedTvChannel() {
        return featuredTvChannel;
    }

    public void setFeaturedTvChannel(List<FeaturedTvChannel> featuredTvChannel) {
        this.featuredTvChannel = featuredTvChannel;
    }

    public List<LatestMovie> getLatestMovies() {
        return latestMovies;
    }

    public void setLatestMovies(List<LatestMovie> latestMovies) {
        this.latestMovies = latestMovies;
    }

    public List<LatestTvseries> getLatestTvseries() {
        return latestTvseries;
    }

    public void setLatestTvseries(List<LatestTvseries> latestTvseries) {
        this.latestTvseries = latestTvseries;
    }

    public List<FeaturesGenreAndMovie> getFeaturesGenreAndMovie() {
        return featuresGenreAndMovie;
    }

    public void setFeaturesGenreAndMovie(List<FeaturesGenreAndMovie> featuresGenreAndMovie) {
        this.featuresGenreAndMovie = featuresGenreAndMovie;
    }

}
