package com.burmesesubtitles.app.utils;

import android.content.Context;
import android.os.Environment;

import com.burmesesubtitles.app.models.single_details.Country;
import com.burmesesubtitles.app.models.single_details.Genre;
import com.burmesesubtitles.app.network.model.TvCategory;

import java.io.File;
import java.util.List;

public class Constants {

    public static final String ADMOB = "admob";
    public static final String START_APP = "startApp";
    public static final String NETWORK_AUDIENCE = "fan";

    public static String workId;

    //public static String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().toString()+File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator;

    public static String getDownloadDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator;
    }

    public static final String USER_LOGIN_STATUS = "login_status";

    public static List<Genre> genreList = null;
    public static List<Country> countryList = null;
    public static List<TvCategory> tvCategoryList = null;

}
