package com.burmesesubtitles.app.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.islamkhsh.CardSliderViewPager;
import com.ixidev.gdpr.GDPRChecker;
import com.burmesesubtitles.app.Config;
import com.burmesesubtitles.app.ItemMovieActivity;
import com.burmesesubtitles.app.ItemSeriesActivity;
import com.burmesesubtitles.app.ItemTVActivity;
import com.burmesesubtitles.app.MainActivity;
import com.burmesesubtitles.app.R;
import com.burmesesubtitles.app.adapters.CountryAdapter;
import com.burmesesubtitles.app.adapters.GenreAdapter;
import com.burmesesubtitles.app.adapters.GenreHomeAdapter;
import com.burmesesubtitles.app.adapters.HomePageAdapter;
import com.burmesesubtitles.app.adapters.LiveTvHomeAdapter;
import com.burmesesubtitles.app.adapters.SliderAdapter;
import com.burmesesubtitles.app.database.DatabaseHelper;
import com.burmesesubtitles.app.models.CommonModels;
import com.burmesesubtitles.app.models.GenreModel;
import com.burmesesubtitles.app.models.home_content.AllCountry;
import com.burmesesubtitles.app.models.home_content.AllGenre;
import com.burmesesubtitles.app.models.home_content.FeaturedTvChannel;
import com.burmesesubtitles.app.models.home_content.FeaturesGenreAndMovie;
import com.burmesesubtitles.app.models.home_content.HomeContent;
import com.burmesesubtitles.app.models.home_content.LatestMovie;
import com.burmesesubtitles.app.models.home_content.LatestTvseries;
import com.burmesesubtitles.app.models.home_content.Slider;
import com.burmesesubtitles.app.models.home_content.Video;
import com.burmesesubtitles.app.network.RetrofitClient;
import com.burmesesubtitles.app.network.apis.HomeContentApi;
import com.burmesesubtitles.app.network.model.AdsConfig;
import com.burmesesubtitles.app.utils.ads.BannerAds;
import com.burmesesubtitles.app.utils.Constants;
import com.burmesesubtitles.app.utils.NetworkInst;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class HomeFragment extends Fragment {

    CardSliderViewPager cViewPager;
    private ArrayList<CommonModels> listSlider = new ArrayList<>();
    private Timer timer;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerViewMovie, recyclerViewTv, recyclerViewTvSeries, recyclerViewGenre;
    private RecyclerView genreRv;
    private RecyclerView countryRv;
    private GenreAdapter genreAdapter;
    private CountryAdapter countryAdapter;
    private RelativeLayout genreLayout, countryLayout;
    private HomePageAdapter adapterMovie, adapterSeries;
    private LiveTvHomeAdapter adapterTv;
    private List<CommonModels> listMovie = new ArrayList<>();
    private List<CommonModels> listTv = new ArrayList<>();
    private List<CommonModels> listSeries = new ArrayList<>();
    private List<CommonModels> genreList = new ArrayList<>();
    private List<CommonModels> countryList = new ArrayList<>();
    private Button btnMoreMovie, btnMoreTv, btnMoreSeries;

    private TextView tvNoItem;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;

    private RelativeLayout adView, adView1;
    private List<GenreModel> listGenre = new ArrayList<>();

    private GenreHomeAdapter genreHomeAdapter;
    private View sliderLayout;

    private MainActivity activity;
    private LinearLayout searchRootLayout;

    private CardView searchBar;
    private ImageView menuIv, searchIv;
    private TextView pageTitle;
    private DatabaseHelper db = new DatabaseHelper(getContext());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(getContext());

        adView              = view.findViewById(R.id.adView);
        adView1             = view.findViewById(R.id.adView1);
        btnMoreSeries       = view.findViewById(R.id.btn_more_series);
        btnMoreTv           = view.findViewById(R.id.btn_more_tv);
        btnMoreMovie        = view.findViewById(R.id.btn_more_movie);
        shimmerFrameLayout  = view.findViewById(R.id.shimmer_view_container);
        tvNoItem            = view.findViewById(R.id.tv_noitem);
        coordinatorLayout   = view.findViewById(R.id.coordinator_lyt);
        swipeRefreshLayout  = view.findViewById(R.id.swipe_layout);
        scrollView          = view.findViewById(R.id.scrollView);
        sliderLayout        = view.findViewById(R.id.slider_layout);
        genreRv             = view.findViewById(R.id.genre_rv);
        countryRv           = view.findViewById(R.id.country_rv);
        genreLayout         = view.findViewById(R.id.genre_layout);
        countryLayout       = view.findViewById(R.id.country_layout);
        cViewPager          = view.findViewById(R.id.c_viewPager);
        searchRootLayout    = view.findViewById(R.id.search_root_layout);
        searchBar           = view.findViewById(R.id.search_bar);
        menuIv              = view.findViewById(R.id.bt_menu);
        pageTitle           = view.findViewById(R.id.page_title_tv);
        searchIv           = view.findViewById(R.id.search_iv);


        if (db.getConfigurationData().getAppConfig().getGenreVisible()) {
            genreLayout.setVisibility(View.VISIBLE);
        }
        if (db.getConfigurationData().getAppConfig().getCountryVisible()) {
            countryLayout.setVisibility(View.VISIBLE);
        }

        pageTitle.setText(getResources().getString(R.string.home));

        if (activity.isDark) {
            pageTitle.setTextColor(activity.getResources().getColor(R.color.white));
            searchBar.setCardBackgroundColor(activity.getResources().getColor(R.color.black_window_light));
            menuIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_menu));
            searchIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_search_white));
        }

        //----init timer slider--------------------
        timer = new Timer();

        //----btn click-------------
        btnClick();

        // --- genre recycler view ---------
        genreRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        genreRv.setHasFixedSize(true);
        genreRv.setNestedScrollingEnabled(false);
        genreAdapter = new GenreAdapter(getActivity(), genreList, "genre", "home");
        genreRv.setAdapter(genreAdapter);

        // --- country recycler view ---------
        countryRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        countryRv.setHasFixedSize(true);
        countryRv.setNestedScrollingEnabled(false);
        countryAdapter = new CountryAdapter(getActivity(), countryList, "home");
        countryRv.setAdapter(countryAdapter);

        //----featured tv recycler view-----------------
        recyclerViewTv = view.findViewById(R.id.recyclerViewTv);
        recyclerViewTv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTv.setHasFixedSize(true);
        recyclerViewTv.setNestedScrollingEnabled(false);
        adapterTv = new LiveTvHomeAdapter(getContext(), listTv, "MainActivity");
        recyclerViewTv.setAdapter(adapterTv);

        //----movie's recycler view-----------------
        recyclerViewMovie = view.findViewById(R.id.recyclerView);
        recyclerViewMovie.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMovie.setHasFixedSize(true);
        recyclerViewMovie.setNestedScrollingEnabled(false);
        adapterMovie = new HomePageAdapter(getContext(), listMovie);
        recyclerViewMovie.setAdapter(adapterMovie);

        //----series's recycler view-----------------
        recyclerViewTvSeries = view.findViewById(R.id.recyclerViewTvSeries);
        recyclerViewTvSeries.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTvSeries.setHasFixedSize(true);
        recyclerViewTvSeries.setNestedScrollingEnabled(false);
        adapterSeries = new HomePageAdapter(getActivity(), listSeries);
        recyclerViewTvSeries.setAdapter(adapterSeries);

        //----genre's recycler view--------------------
        recyclerViewGenre = view.findViewById(R.id.recyclerView_by_genre);
        recyclerViewGenre.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGenre.setHasFixedSize(true);
        recyclerViewGenre.setNestedScrollingEnabled(false);
        genreHomeAdapter = new GenreHomeAdapter(getContext(), listGenre);
        recyclerViewGenre.setAdapter(genreHomeAdapter);

        shimmerFrameLayout.startShimmer();

        if (new NetworkInst(getContext()).isNetworkAvailable()) {

            getHomeContent();

        } else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                recyclerViewMovie.removeAllViews();
                recyclerViewTv.removeAllViews();
                recyclerViewTvSeries.removeAllViews();
                recyclerViewGenre.removeAllViews();
                genreRv.removeAllViews();
                countryRv.removeAllViews();

                genreList.clear();
                countryList.clear();
                listMovie.clear();
                listSeries.clear();
                listSlider.clear();
                listTv.clear();
                listGenre.clear();


                if (new NetworkInst(getContext()).isNetworkAvailable()) {

                    getHomeContent();

                } else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }
            }
        });


        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateSearchBar(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateSearchBar(true);
                }
            }
        });

        getAdDetails();
    }


    private void getHomeContent() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        HomeContentApi api = retrofit.create(HomeContentApi.class);
        Call<HomeContent> call = api.getHomeContent(Config.API_KEY);
        call.enqueue(new Callback<HomeContent>() {
            @Override
            public void onResponse(Call<HomeContent> call, retrofit2.Response<HomeContent> response) {
                 if (response.code() == 200){
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    coordinatorLayout.setVisibility(View.GONE);

                    //slider data
                    Slider slider = response.body().getSlider();
                    if (slider.getSliderType().equalsIgnoreCase("disable")) {
                        sliderLayout.setVisibility(View.GONE);
                    }else if (slider.getSliderType().equalsIgnoreCase("movie")){

                    }else if (slider.getSliderType().equalsIgnoreCase("image")){

                    }

                    SliderAdapter sliderAdapter = new SliderAdapter(slider.getSlide());
                    cViewPager.setAdapter(sliderAdapter);
                    sliderAdapter.notifyDataSetChanged();

                    //genre data
                     if (db.getConfigurationData().getAppConfig().getGenreVisible()) {
                         for (int i = 0; i < response.body().getAllGenre().size(); i++) {
                             AllGenre genre = response.body().getAllGenre().get(i);
                             CommonModels models = new CommonModels();
                             models.setId(genre.getGenreId());
                             models.setTitle(genre.getName());
                             models.setImageUrl(genre.getImageUrl());
                             genreList.add(models);
                         }
                         genreAdapter.notifyDataSetChanged();
                     }

                     //country data
                     if (db.getConfigurationData().getAppConfig().getCountryVisible()) {
                         for (int i = 0; i < response.body().getAllCountry().size(); i++) {
                             AllCountry country = response.body().getAllCountry().get(i);
                             CommonModels models = new CommonModels();
                             models.setId(country.getCountryId());
                             models.setTitle(country.getName());
                             models.setImageUrl(country.getImageUrl());
                             countryList.add(models);
                         }
                         countryAdapter.notifyDataSetChanged();
                     }

                     //tv channel data
                     for (int i = 0; i < response.body().getFeaturedTvChannel().size(); i++){
                         FeaturedTvChannel tvChannel = response.body().getFeaturedTvChannel().get(i);
                         CommonModels models = new CommonModels();
                         models.setImageUrl(tvChannel.getPosterUrl());
                         models.setTitle(tvChannel.getTvName());
                         models.setVideoType("tv");
                         models.setId(tvChannel.getLiveTvId());
                         models.setIsPaid(tvChannel.getIsPaid());
                         listTv.add(models);
                     }
                     adapterTv.notifyDataSetChanged();

                     //latest movies data
                     for (int i = 0; i < response.body().getLatestMovies().size(); i++){
                         LatestMovie movie = response.body().getLatestMovies().get(i);
                         CommonModels models = new CommonModels();
                         models.setImageUrl(movie.getThumbnailUrl());
                         models.setTitle(movie.getTitle());
                         models.setVideoType("movie");
                         models.setReleaseDate(movie.getRelease());
                         models.setQuality(movie.getVideoQuality());
                         models.setId(movie.getVideosId());
                         models.setIsPaid(movie.getIsPaid());
                         listMovie.add(models);
                     }
                     adapterMovie.notifyDataSetChanged();

                     //latest tv series
                     for (int i = 0; i < response.body().getLatestTvseries().size(); i++){
                         LatestTvseries tvSeries = response.body().getLatestTvseries().get(i);
                         CommonModels models = new CommonModels();
                         models.setImageUrl(tvSeries.getThumbnailUrl());
                         models.setTitle(tvSeries.getTitle());
                         models.setVideoType("tvseries");
                         models.setReleaseDate(tvSeries.getRelease());
                         models.setQuality(tvSeries.getVideoQuality());
                         models.setId(tvSeries.getVideosId());
                         models.setIsPaid(tvSeries.getIsPaid());
                         listSeries.add(models);
                     }
                     adapterSeries.notifyDataSetChanged();

                     //get data by genre
                     for (int i = 0; i < response.body().getFeaturesGenreAndMovie().size(); i++){
                         FeaturesGenreAndMovie genreAndMovie = response.body().getFeaturesGenreAndMovie().get(i);
                         GenreModel models = new GenreModel();

                         models.setName(genreAndMovie.getName());
                         models.setId(genreAndMovie.getGenreId());
                         List<CommonModels> listGenreMovie = new ArrayList<>();
                         for (int j = 0; j < genreAndMovie.getVideos().size(); j++){
                             Video video = genreAndMovie.getVideos().get(j);
                             CommonModels commonModels = new CommonModels();

                             commonModels.setId(video.getVideosId());
                             commonModels.setTitle(video.getTitle());
                             commonModels.setIsPaid(video.getIsPaid());

                             if (video.getIsTvseries().equals("0")) {
                                 commonModels.setVideoType("movie");
                             } else {
                                 commonModels.setVideoType("tvseries");
                             }

                             commonModels.setReleaseDate(video.getRelease());
                             commonModels.setQuality(video.getVideoQuality());
                             commonModels.setImageUrl(video.getThumbnailUrl());

                             listGenreMovie.add(commonModels);
                         }
                         models.setList(listGenreMovie);

                         listGenre.add(models);
                         genreHomeAdapter.notifyDataSetChanged();
                     }

                }else {
                     swipeRefreshLayout.setRefreshing(false);
                     shimmerFrameLayout.stopShimmer();
                     shimmerFrameLayout.setVisibility(View.GONE);
                     coordinatorLayout.setVisibility(View.VISIBLE);
                     scrollView.setVisibility(View.GONE);
                 }

            }

            @Override
            public void onFailure(Call<HomeContent> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);

            }
        });
    }

    private void loadAd() {
        AdsConfig adsConfig = new DatabaseHelper(getContext()).getConfigurationData().getAdsConfig();
        if (adsConfig.getAdsEnable().equals("1")) {

            if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.ADMOB)) {
                BannerAds.ShowAdmobBannerAds(getContext(), adView);
                BannerAds.ShowAdmobBannerAds(getContext(), adView1);

            } else if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.START_APP)) {
                BannerAds.showStartAppBanner(getContext(), adView);

            } else if(adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.NETWORK_AUDIENCE)) {
                BannerAds.showFANBanner(getContext(), adView);
                BannerAds.showFANBanner(getContext(), adView1);
            }
        }
    }

    private void btnClick() {

        btnMoreMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemMovieActivity.class);
                intent.putExtra("title", "Movies");
                getActivity().startActivity(intent);
            }
        });
        btnMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemTVActivity.class);
                intent.putExtra("title", "Live TV");
                getActivity().startActivity(intent);
            }
        });

        btnMoreSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemSeriesActivity.class);
                intent.putExtra("title", "TV Series");
                getActivity().startActivity(intent);
            }
        });

    }

    private void getAdDetails() {
        DatabaseHelper db = new DatabaseHelper(getContext());
        AdsConfig adsConfig = db.getConfigurationData().getAdsConfig();

        new GDPRChecker()
                .withContext(activity)
                .withPrivacyUrl(Config.TERMS_URL) // your privacy url
                .withPublisherIds(adsConfig.getAdmobAppId()) // your admob account Publisher id
                .withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
                .check();

        loadAd();
    }

    @Override
    public void onStart() {
        super.onStart();

        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openDrawer();
            }
        });


        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.goToSearchActivity();
            }
        });

        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * searchRootLayout.getHeight()) : 0;
        searchRootLayout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


}
