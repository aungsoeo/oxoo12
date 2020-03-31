
package com.burmesesubtitles.app.models.home_content;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slider {

    @SerializedName("slider_type")
    @Expose
    private String sliderType;
    @SerializedName("slide")
    @Expose
    private ArrayList<Slide> slide = null;

    public String getSliderType() {
        return sliderType;
    }

    public void setSliderType(String sliderType) {
        this.sliderType = sliderType;
    }

    public ArrayList<Slide> getSlide() {
        return slide;
    }

    public void setSlide(ArrayList<Slide> slide) {
        this.slide = slide;
    }

}
