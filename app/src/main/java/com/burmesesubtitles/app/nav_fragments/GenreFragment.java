package com.burmesesubtitles.app.nav_fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.burmesesubtitles.app.Config;
import com.burmesesubtitles.app.MainActivity;
import com.burmesesubtitles.app.R;
import com.burmesesubtitles.app.adapters.GenreAdapter;
import com.burmesesubtitles.app.database.DatabaseHelper;
import com.burmesesubtitles.app.models.CommonModels;
import com.burmesesubtitles.app.models.home_content.AllGenre;
import com.burmesesubtitles.app.network.RetrofitClient;
import com.burmesesubtitles.app.network.apis.GenreApi;
import com.burmesesubtitles.app.network.model.AdsConfig;
import com.burmesesubtitles.app.utils.ApiResources;
import com.burmesesubtitles.app.utils.ads.BannerAds;
import com.burmesesubtitles.app.utils.Constants;
import com.burmesesubtitles.app.utils.NetworkInst;
import com.burmesesubtitles.app.utils.SpacingItemDecoration;
import com.burmesesubtitles.app.utils.ToastMsg;
import com.burmesesubtitles.app.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class GenreFragment extends Fragment {

    ShimmerFrameLayout shimmerFrameLayout;
    private ApiResources apiResources;
    private RecyclerView recyclerView;
    private List<CommonModels> list = new ArrayList<>();
    private GenreAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private TextView tvNoItem;

    private RelativeLayout adView;

    private MainActivity activity;

    private LinearLayout searchRootLayout;

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    private CardView searchBar;
    private ImageView menuIv, searchIv;
    private TextView pageTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.layout_country,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.genre));

        adView=view.findViewById(R.id.adView);
        coordinatorLayout=view.findViewById(R.id.coordinator_lyt);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout=view.findViewById(R.id.swipe_layout);
        recyclerView=view.findViewById(R.id.recyclerView);
        tvNoItem=view.findViewById(R.id.tv_noitem);
        searchRootLayout=view.findViewById(R.id.search_root_layout);
        searchBar           = view.findViewById(R.id.search_bar);
        menuIv              = view.findViewById(R.id.bt_menu);
        pageTitle           = view.findViewById(R.id.page_title_tv);
        searchIv            = view.findViewById(R.id.search_iv);

        pageTitle.setText(getContext().getResources().getString(R.string.genre));


        if (activity.isDark) {
            pageTitle.setTextColor(activity.getResources().getColor(R.color.white));
            searchBar.setCardBackgroundColor(activity.getResources().getColor(R.color.black_window_light));
            menuIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_menu));
            searchIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_search_white));
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 10), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new GenreAdapter(activity, list,"genre", "");
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    animateSearchBar(true);
                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    animateSearchBar(false);
                    controlsVisible = true;
                    scrolledDistance = 0;
                }

                if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                    scrolledDistance += dy;
                }


            }
        });

        apiResources=new ApiResources();

        shimmerFrameLayout.startShimmer();



        if (new NetworkInst(getContext()).isNetworkAvailable()){
            getAllGenre();
        }else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                coordinatorLayout.setVisibility(View.GONE);

                recyclerView.removeAllViews();
                list.clear();
                mAdapter.notifyDataSetChanged();

                if (new NetworkInst(getContext()).isNetworkAvailable()){
                    getAllGenre();
                }else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

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

    }

    private void loadAd(){
        AdsConfig adsConfig = new DatabaseHelper(getContext()).getConfigurationData().getAdsConfig();
        if (adsConfig.getAdsEnable().equals("1")) {

            if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.ADMOB)) {
                BannerAds.ShowAdmobBannerAds(activity, adView);

            } else if (adsConfig.getMobileAdsNetwork().equals(Constants.START_APP)) {
                BannerAds.showStartAppBanner(activity, adView);

            } else if(adsConfig.getMobileAdsNetwork().equals(Constants.NETWORK_AUDIENCE)) {
                BannerAds.showFANBanner(getContext(), adView);
            }
        }
    }


    private void getAllGenre(){
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        GenreApi api = retrofit.create(GenreApi.class);
        Call<List<AllGenre>> call = api.getGenre(Config.API_KEY);
        call.enqueue(new Callback<List<AllGenre>>() {
            @Override
            public void onResponse(Call<List<AllGenre>> call, retrofit2.Response<List<AllGenre>> response) {
                if (response.code() == 200){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.body().size() == 0){
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }else {
                        coordinatorLayout.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < response.body().size(); i++) {
                        AllGenre genre = response.body().get(i);
                        CommonModels models = new CommonModels();
                        models.setId(genre.getGenreId());
                        models.setTitle(genre.getName());
                        models.setImageUrl(genre.getImageUrl());
                        list.add(models);
                    }
                    mAdapter.notifyDataSetChanged();
                }else {
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<AllGenre>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));
                coordinatorLayout.setVisibility(View.VISIBLE);
            }
        });
    }


    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * searchRootLayout.getHeight()) : 0;
        searchRootLayout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }



}
