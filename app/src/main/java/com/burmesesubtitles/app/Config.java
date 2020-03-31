package com.burmesesubtitles.app;

public class Config {

    // copy your api url from php admin dashboard & paste below
    public static final String API_SERVER_URL = "https://app.burmesesubtitles.com/backend/rest-api/";

    //copy your api key from php admin dashboard & paste below
    public static final String API_KEY = "t7h7f90egre0yqa0ny5hp864";

    //copy your terms url from php admin dashboard & paste below
    public static final String TERMS_URL = "https://app.burmesesubtitles.com/backend/terms/";

    //youtube video auto play
    public static boolean YOUTUBE_VIDEO_AUTO_PLAY = false;

    //default theme
    public static boolean DEFAULT_DARK_THEME_ENABLE = true;

    // First, you have to configure firebase to enable facebook, phone and google login
    // facebook authentication
    public static final boolean ENABLE_FACEBOOK_LOGIN = true;

    //Phone authentication
    public static final boolean ENABLE_PHONE_LOGIN = true;

    //Google authentication
    public static final boolean ENABLE_GOOGLE_LOGIN = true;
}
