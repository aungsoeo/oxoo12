package com.burmesesubtitles.app.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RadioCategory implements Serializable {

    @SerializedName("radio_category_id")
    @Expose
    private String radioCategoryId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("radios")
    @Expose
    private List<RadioModel> radios = null;

    public String getRadioCategoryId() {
        return radioCategoryId;
    }

    public void setRadioCategoryId(String radioCategoryId) {
        this.radioCategoryId = radioCategoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RadioModel> getRadios() {
        return radios;
    }

    public void setRadios(List<RadioModel> radios) {
        this.radios = radios;
    }



}
