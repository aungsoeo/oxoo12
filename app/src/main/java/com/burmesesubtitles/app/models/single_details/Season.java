package com.burmesesubtitles.app.models.single_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Season {

    @SerializedName("seasons_id")
    @Expose
    private String seasonsId;
    @SerializedName("seasons_name")
    @Expose
    private String seasonsName;
    @SerializedName("episodes")
    @Expose
    private List<Episode> episodes = null;

    public String getSeasonsId() {
        return seasonsId;
    }

    public void setSeasonsId(String seasonsId) {
        this.seasonsId = seasonsId;
    }

    public String getSeasonsName() {
        return seasonsName;
    }

    public void setSeasonsName(String seasonsName) {
        this.seasonsName = seasonsName;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}
