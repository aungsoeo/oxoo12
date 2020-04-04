package com.burmesesubtitles.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.burmesesubtitles.app.adapters.CastCrewAdapter;
import com.burmesesubtitles.app.adapters.CommentsAdapter;
import com.burmesesubtitles.app.adapters.EpisodeAdapter;
import com.burmesesubtitles.app.adapters.DownloadAdapter;
import com.burmesesubtitles.app.adapters.HomePageAdapter;
import com.burmesesubtitles.app.adapters.LiveTvHomeAdapter;
import com.burmesesubtitles.app.adapters.ProgramAdapter;
import com.burmesesubtitles.app.adapters.ServerApater;
import com.burmesesubtitles.app.database.DatabaseHelper;
import com.burmesesubtitles.app.models.CastCrew;
import com.burmesesubtitles.app.models.GetCommentsModel;
import com.burmesesubtitles.app.models.CommonModels;
import com.burmesesubtitles.app.models.EpiModel;
import com.burmesesubtitles.app.models.PostCommentModel;
import com.burmesesubtitles.app.models.Program;
import com.burmesesubtitles.app.models.SubtitleModel;
import com.burmesesubtitles.app.models.single_details.Cast;
import com.burmesesubtitles.app.models.single_details.Director;
import com.burmesesubtitles.app.models.single_details.DownloadLink;
import com.burmesesubtitles.app.models.single_details.Episode;
import com.burmesesubtitles.app.models.single_details.Genre;
import com.burmesesubtitles.app.models.single_details.RelatedMovie;
import com.burmesesubtitles.app.models.single_details.Season;
import com.burmesesubtitles.app.models.single_details.SingleDetails;
import com.burmesesubtitles.app.models.single_details.Subtitle;
import com.burmesesubtitles.app.models.single_details.Video;
import com.burmesesubtitles.app.network.RetrofitClient;
import com.burmesesubtitles.app.network.apis.CommentApi;
import com.burmesesubtitles.app.network.apis.FavouriteApi;
import com.burmesesubtitles.app.network.apis.SingleDetailsApi;
import com.burmesesubtitles.app.network.apis.SubscriptionApi;
import com.burmesesubtitles.app.network.model.ActiveStatus;
import com.burmesesubtitles.app.network.model.AdsConfig;
import com.burmesesubtitles.app.network.model.FavoriteModel;
import com.burmesesubtitles.app.service.DownloadWorkManager;
import com.burmesesubtitles.app.utils.PreferenceUtils;
import com.burmesesubtitles.app.utils.ApiResources;
import com.burmesesubtitles.app.utils.ads.BannerAds;
import com.burmesesubtitles.app.utils.Constants;
import com.burmesesubtitles.app.utils.ads.PopUpAds;
import com.burmesesubtitles.app.utils.ToastMsg;
import com.burmesesubtitles.app.utils.Tools;
import com.burmesesubtitles.app.utils.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.google.android.gms.ads.AdActivity.CLASS_NAME;

public class DetailsActivity extends AppCompatActivity implements CastPlayer.SessionAvailabilityListener, ProgramAdapter.OnProgramClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PRELOAD_TIME_S = 20;
    public static final String TAG = DetailsActivity.class.getSimpleName();
    private TextView tvName, tvDirector, tvRelease, tvCast, tvDes, tvGenre, tvRelated;
    private RecyclerView rvDirector, rvServer, rvRelated, rvComment, castRv;
    private Spinner seasonSpinner;
    private LinearLayout seasonSpinnerContainer;
    public static RelativeLayout lPlay;
    private RelativeLayout contentDetails;
    private LinearLayout subscriptionLayout, topbarLayout;
    private Button subscribeBt;
    private ImageView backIv, subBackIv;

    private ServerApater serverAdapter;
    private DownloadAdapter internalDownloadAdapter, externalDownloadAdapter;
    private HomePageAdapter relatedAdapter;
    private LiveTvHomeAdapter relatedTvAdapter;
    private CastCrewAdapter castCrewAdapter;

    int start = 0;
    private List<CommonModels> listServer = new ArrayList<>();
    private List<CommonModels> listRelated = new ArrayList<>();
    private List<GetCommentsModel> listComment = new ArrayList<>();
    private List<CommonModels> listDownload = new ArrayList<>();
    private List<CommonModels> listInternalDownload = new ArrayList<>();
    private List<CommonModels> listExternalDownload = new ArrayList<>();
    private List<CastCrew> castCrews = new ArrayList<>();
    private String strDirector = "", strCast = "", strGenre = "";
    public static LinearLayout llBottom, llBottomParent, llcomment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String type = "", id = "";
    private ImageView imgAddFav, shareIv2;
    public static ImageView imgBack, serverIv;
    private Button watchNowBt, downloadBt;
    private ImageView posterIv, thumbIv, descriptionBackIv;
    private String V_URL = "";
    public static WebView webView;
    public static ProgressBar progressBar;
    private boolean isFav = false;
    private TextView chromeCastTv;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Button btnComment;
    private EditText etComment;
    private CommentsAdapter commentsAdapter;
    private RelativeLayout adView;
    private InterstitialAd mInterstitialAd;
    private LinearLayout download_text;


    public static SimpleExoPlayer player;
    public static PlayerView simpleExoPlayerView;
    public PlayerControlView castControlView;
    public static SubtitleView subtitleView;

    public static ImageView imgFull;
    public ImageView aspectRatioIv, externalPlayerIv, volumControlIv;
    private LinearLayout volumnControlLayout;
    private SeekBar volumnSeekbar;
    private TextView volumnTv;
    public MediaRouteButton mediaRouteButton;
    private CastContext castContext;

    public static boolean isPlaying, isFullScr;
    public static View playerLayout;

    private int playerHeight;
    public static boolean isVideo = true;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String strSubtitle = "Null";
    public static MediaSource mediaSource = null;
    public static ImageView imgSubtitle;
    public ImageView radioPlayImage;
    private List<SubtitleModel> listSub = new ArrayList<>();
    private AlertDialog alertDialog;
    private String mediaUrl;
    private boolean tv = false;
    private String download_check = "";

    private String season;
    private String episod;
    private String movieTitle;
    private String seriesTitle;

    private CastPlayer castPlayer;
    private boolean castSession;
    private String title;
    String castImageUrl;

    private LinearLayout tvLayout, sheduleLayout, tvTopLayout;
    private TextView tvTitleTv, watchStatusTv, timeTv, programTv, proGuideTv, watchLiveTv;
    private ProgramAdapter programAdapter;
    List<Program> programs = new ArrayList<>();
    private RecyclerView programRv;
    private ImageView tvThumbIv, shareIv;

    private LinearLayout exoRewind, exoForward, seekbarLayout;
    ImageView exoDownloadIv;
    private TextView liveTv;


    boolean isDark;
    private OrientationEventListener myOrientationEventListener;
    private String serverType;

    private boolean fullScreenByClick;
    private String currentProgramTime;
    private String currentProgramTitle;
    private String userId;

    private String youtubeDownloadUr;
    private String urlType = "";
    private RelativeLayout descriptionLayout;
    private MaterialRippleLayout descriptionContatainer;
    private TextView dGenryTv;
    private RecyclerView internalServerRv, externalServerRv, serverRv;
    private LinearLayout internalDownloadLayout, externalDownloadLayout;
    private boolean activeMovie;

    private TextView sereisTitleTv;
    private RelativeLayout seriestLayout;
    private ImageView favIv;

    private RelativeLayout mRlTouch;
    private boolean intLeft, intRight;
    private int sWidth, sHeight;
    private long diffX, diffY;
    private Display display;
    private Point size;
    private float downX, downY;
    private AudioManager mAudioManager;
    private int aspectClickCount = 1;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        db = new DatabaseHelper(DetailsActivity.this);

        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "details_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        adView = findViewById(R.id.adView);
        llBottom = findViewById(R.id.llbottom);
        tvDes = findViewById(R.id.tv_details);
        tvCast = findViewById(R.id.tv_cast);
        tvRelease = findViewById(R.id.tv_release_date);
        tvName = findViewById(R.id.text_name);
        tvDirector = findViewById(R.id.tv_director);
        tvGenre = findViewById(R.id.tv_genre);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        imgAddFav = findViewById(R.id.add_fav);
        imgBack = findViewById(R.id.img_back);
        radioPlayImage = findViewById(R.id.radioPlayImage);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        llBottomParent = findViewById(R.id.llbottomparent);
        lPlay = findViewById(R.id.play);
        rvRelated = findViewById(R.id.rv_related);
        tvRelated = findViewById(R.id.tv_related);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        btnComment = findViewById(R.id.btn_comment);
        etComment = findViewById(R.id.et_comment);
        rvComment = findViewById(R.id.recyclerView_comment);
        llcomment = findViewById(R.id.llcomments);
        simpleExoPlayerView = findViewById(R.id.video_view);
        subtitleView = findViewById(R.id.subtitle);
        playerLayout = findViewById(R.id.player_layout);
        imgFull = findViewById(R.id.img_full_scr);
        aspectRatioIv = findViewById(R.id.aspect_ratio_iv);
        externalPlayerIv = findViewById(R.id.external_player_iv);
        volumControlIv = findViewById(R.id.volumn_control_iv);
        volumnControlLayout = findViewById(R.id.volumn_layout);
        volumnSeekbar = findViewById(R.id.volumn_seekbar);
        volumnTv = findViewById(R.id.volumn_tv);
        rvServer = findViewById(R.id.rv_server_list);
        seasonSpinner = findViewById(R.id.season_spinner);
        seasonSpinnerContainer = findViewById(R.id.spinner_container);
        imgSubtitle = findViewById(R.id.img_subtitle);
        download_text = findViewById(R.id.download_text);
        mediaRouteButton = findViewById(R.id.media_route_button);
        chromeCastTv = findViewById(R.id.chrome_cast_tv);
        castControlView = findViewById(R.id.cast_control_view);
        tvLayout = findViewById(R.id.tv_layout);
        sheduleLayout = findViewById(R.id.p_shedule_layout);
        tvTitleTv = findViewById(R.id.tv_title_tv);
        programRv = findViewById(R.id.program_guide_rv);
        tvTopLayout = findViewById(R.id.tv_top_layout);
        tvThumbIv = findViewById(R.id.tv_thumb_iv);
        shareIv = findViewById(R.id.share_iv);
        watchStatusTv = findViewById(R.id.watch_status_tv);
        timeTv = findViewById(R.id.time_tv);
        programTv = findViewById(R.id.program_type_tv);
        exoRewind = findViewById(R.id.rewind_layout);
        exoForward = findViewById(R.id.forward_layout);
        seekbarLayout = findViewById(R.id.seekbar_layout);
        liveTv = findViewById(R.id.live_tv);
        castRv = findViewById(R.id.cast_rv);
        proGuideTv = findViewById(R.id.pro_guide_tv);
        watchLiveTv = findViewById(R.id.watch_live_tv);

        contentDetails = findViewById(R.id.content_details);
        subscriptionLayout = findViewById(R.id.subscribe_layout);
        subscribeBt = findViewById(R.id.subscribe_bt);
        backIv = findViewById(R.id.des_back_iv);
        subBackIv = findViewById(R.id.back_iv);
        topbarLayout = findViewById(R.id.topbar);

        descriptionLayout = findViewById(R.id.description_layout);
        descriptionContatainer = findViewById(R.id.lyt_parent);
        watchNowBt = findViewById(R.id.watch_now_bt);
        downloadBt = findViewById(R.id.download_bt);
        posterIv = findViewById(R.id.poster_iv);
        thumbIv = findViewById(R.id.image_thumb);
        descriptionBackIv = findViewById(R.id.back_iv);
        dGenryTv = findViewById(R.id.genre_tv);
        serverIv = findViewById(R.id.img_server);

        seriestLayout = findViewById(R.id.series_layout);
        favIv = findViewById(R.id.add_fav2);
        sereisTitleTv = findViewById(R.id.seriest_title_tv);
        shareIv2 = findViewById(R.id.share_iv2);


        if (isDark) {
            tvTopLayout.setBackgroundColor(getResources().getColor(R.color.black_window_light));
            sheduleLayout.setBackground(getResources().getDrawable(R.drawable.rounded_black_transparent));
            etComment.setBackground(getResources().getDrawable(R.drawable.round_grey_transparent));
            btnComment.setTextColor(getResources().getColor(R.color.grey_20));
            topbarLayout.setBackgroundColor(getResources().getColor(R.color.dark));
            subscribeBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_dark));

            descriptionContatainer.setBackground(getResources().getDrawable(R.drawable.gradient_black_transparent));
        }
        // chrome cast
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mediaRouteButton);
        castContext = CastContext.getSharedInstance(this);
        castPlayer = new CastPlayer(castContext);
        castPlayer.setSessionAvailabilityListener(this);

        // cast button will show if the cast device will be available
        if (castContext.getCastState() != CastState.NO_DEVICES_AVAILABLE)
            mediaRouteButton.setVisibility(View.VISIBLE);
        // start the shimmer effect
        shimmerFrameLayout.startShimmer();
        playerHeight = lPlay.getLayoutParams().height;
        progressBar.setMax(100); // 100 maximum value for the progress value
        progressBar.setProgress(50);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeMovie) {
                    setPlayerNormalScreen();
                    player.setPlayWhenReady(false);
                    player.stop();
                    showDescriptionLayout();
                    activeMovie = false;
                } else {
                    finish();
                }
            }
        });


        type = getIntent().getStringExtra("vType");
        id = getIntent().getStringExtra("id");
        castSession = getIntent().getBooleanExtra("castSession", false);

        // getting user login info for favourite button visibility
         userId = db.getUserData().getUserId();
        if (PreferenceUtils.isLoggedIn(DetailsActivity.this)) {
            imgAddFav.setVisibility(VISIBLE);
        } else {
            imgAddFav.setVisibility(GONE);
        }

        commentsAdapter = new CommentsAdapter(this, listComment);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setHasFixedSize(true);
        rvComment.setNestedScrollingEnabled(false);
        rvComment.setAdapter(commentsAdapter);
        getComments();
        imgFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controlFullScreenPlayer();

            }
        });
        imgSubtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DetailsActivity.this, listSub);

            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreferenceUtils.isLoggedIn(DetailsActivity.this)) {
                    startActivity(new Intent(DetailsActivity.this, LoginActivity.class));
                    new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.login_first));
                } else if (etComment.getText().toString().equals("")) {
                    new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.comment_empty));
                } else {
                    String comment = etComment.getText().toString();
                    addComment( id, PreferenceUtils.getUserId(DetailsActivity.this), comment);
                }
            }
        });

        imgAddFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    removeFromFav();
                } else {
                    addToFav();
                }
            }
        });

        // its for tv series only when description layout visibility gone.
        favIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    removeFromFav();
                } else {
                    addToFav();
                }
            }
        });


        if (!isNetworkAvailable()) {
            new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.no_internet));
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clear_previous();
                initGetData();
            }
        });

        loadAd();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void controlFullScreenPlayer() {
        if (isFullScr) {
            fullScreenByClick = false;
            isFullScr = false;
            swipeRefreshLayout.setVisibility(VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            if (isVideo) {
                lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
            } else {
                lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
            }

            // reset the orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        } else {

            fullScreenByClick = true;
            isFullScr = true;
            swipeRefreshLayout.setVisibility(GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            if (isVideo) {
                lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            } else {
                lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            }

            // reset the orientation
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        initGetData();

        if (mAudioManager != null) {
            volumnSeekbar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            int currentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            volumnSeekbar.setProgress(currentVolumn);
        }

        volumnSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    //volumnTv.setText(i+"");
                    mAudioManager.setStreamVolume(player.getAudioStreamType(), i, 0);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        volumControlIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                volumnControlLayout.setVisibility(VISIBLE);

            }
        });

        aspectRatioIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aspectClickCount == 1) {
                    //Toast.makeText(DetailsActivity.this, "Fill", Toast.LENGTH_SHORT).show();
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    aspectClickCount = 2;
                } else if (aspectClickCount == 2) {
                    //Toast.makeText(DetailsActivity.this, "Fit", Toast.LENGTH_SHORT).show();
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    aspectClickCount = 3;
                } else if (aspectClickCount == 3) {
                    //Toast.makeText(DetailsActivity.this, "Zoom", Toast.LENGTH_SHORT).show();
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    aspectClickCount = 1;
                }

            }
        });

        externalPlayerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaUrl != null) {
                    if (!tv) {
                        // set player normal/ potrait screen if not tv
                        descriptionLayout.setVisibility(VISIBLE);
                        setPlayerNormalScreen();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(mediaUrl), "video/*");
                    startActivity(Intent.createChooser(intent, "Complete action using"));
                }

            }
        });

        watchNowBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listServer.isEmpty()) {
                    if (listServer.size() == 1) {
                        preparePlayer(listServer.get(0));
                        descriptionLayout.setVisibility(GONE);
                        lPlay.setVisibility(VISIBLE);
                    }else {
                        openServerDialog();
                    }
                }else{
                    Toast.makeText(DetailsActivity.this, R.string.no_video_found, Toast.LENGTH_SHORT).show();
                }
            }
        });

        downloadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listInternalDownload.isEmpty() || !listExternalDownload.isEmpty()) {

                        openDownloadServerDialog();}

            }
        });

        watchLiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideExoControlForTv();
                iniMoviePlayer(mediaUrl, serverType, DetailsActivity.this);

                watchStatusTv.setText(getString(R.string.watching_on) + " " + getString(R.string.app_name));
                watchLiveTv.setVisibility(GONE);

                timeTv.setText(currentProgramTime);
                programTv.setText(currentProgramTitle);
            }
        });

        shareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.share(DetailsActivity.this, title);
            }
        });

        shareIv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title == null) {
                    new ToastMsg(DetailsActivity.this).toastIconError("Title should not be empty.");
                    return;
                }
                Tools.share(DetailsActivity.this, title);
            }
        });

        castPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == CastPlayer.STATE_READY) {
                    progressBar.setVisibility(View.GONE);

                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));

                } else if (playbackState == CastPlayer.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                } else if (playbackState == CastPlayer.STATE_BUFFERING) {
                    progressBar.setVisibility(VISIBLE);

                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                } else {
                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                }

            }
        });

        serverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServerDialog();
            }
        });

        simpleExoPlayerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                Log.e("Visibil", String.valueOf(visibility));
                if (visibility == 0) {
                    imgBack.setVisibility(VISIBLE);

                    if (type.equals("tv") || type.equals("tvseries")) {
                        imgFull.setVisibility(VISIBLE);
                    } else {
                        imgFull.setVisibility(GONE);
                    }

                    // invisible download icon for live tv
                    if (download_check.equals("1")) {
                        if (!tv) {
                            if (activeMovie) {
                                serverIv.setVisibility(VISIBLE);
                            }
                        } else {
                        }
                    } else {
                    }

                    if (listSub.size() != 0) {
                        imgSubtitle.setVisibility(VISIBLE);
                    }
                    //imgSubtitle.setVisibility(VISIBLE);
                } else {
                    imgBack.setVisibility(GONE);
                    imgFull.setVisibility(GONE);
                    imgSubtitle.setVisibility(GONE);
                    volumnControlLayout.setVisibility(GONE);
                }
            }
        });

        subscribeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userId == null) {
                    new ToastMsg(DetailsActivity.this).toastIconError(getResources().getString(R.string.subscribe_error));
                    startActivity(new Intent(DetailsActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(DetailsActivity.this, PurchasePlanActivity.class));
                }

            }
        });
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        subBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void setPlayerNormalScreen() {
        swipeRefreshLayout.setVisibility(VISIBLE);
        lPlay.setVisibility(GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (isVideo) {
            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));

        } else {
            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void setPlayerFullScreen() {
        swipeRefreshLayout.setVisibility(GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (isVideo) {
            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        } else {
            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }

    private void openDownloadServerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_download_server_dialog, null);
        internalDownloadLayout = view.findViewById(R.id.internal_download_layout);
        externalDownloadLayout = view.findViewById(R.id.external_download_layout);
        if (listExternalDownload.isEmpty()) {
            externalDownloadLayout.setVisibility(GONE);
        }
        if (listInternalDownload.isEmpty()) {
            internalDownloadLayout.setVisibility(GONE);
        }
        internalServerRv = view.findViewById(R.id.internal_download_rv);
        externalServerRv = view.findViewById(R.id.external_download_rv);
        internalDownloadAdapter = new DownloadAdapter(this, listInternalDownload, true);
        internalServerRv.setLayoutManager(new LinearLayoutManager(this));
        internalServerRv.setHasFixedSize(true);
        internalServerRv.setAdapter(internalDownloadAdapter);

        externalDownloadAdapter = new DownloadAdapter(this, listExternalDownload, true);
        externalServerRv.setLayoutManager(new LinearLayoutManager(this));
        externalServerRv.setHasFixedSize(true);
        externalServerRv.setAdapter(externalDownloadAdapter);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openServerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_server_dialog, null);
        serverRv = view.findViewById(R.id.serverRv);
        serverAdapter = new ServerApater(this, listServer, "movie");
        serverRv.setLayoutManager(new LinearLayoutManager(this));
        serverRv.setHasFixedSize(true);
        serverRv.setAdapter(serverAdapter);

        ImageView closeIv = view.findViewById(R.id.close_iv);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final ServerApater.OriginalViewHolder[] viewHolder = {null};
        serverAdapter.setOnItemClickListener(new ServerApater.OnItemClickListener() {
            @Override
            public void onItemClick(View view, CommonModels obj, int position, ServerApater.OriginalViewHolder holder) {
                preparePlayer(obj);

                serverAdapter.chanColor(viewHolder[0], position);
                holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                viewHolder[0] = holder;
            }

            @Override
            public void getFirstUrl(String url) {
                mediaUrl = url;
            }

            @Override
            public void hideDescriptionLayout() {
                descriptionLayout.setVisibility(GONE);
                lPlay.setVisibility(VISIBLE);
                dialog.dismiss();

            }
        });

    }

    private void preparePlayer(CommonModels obj){
        activeMovie = true;
        setPlayerFullScreen();
        mediaUrl = obj.getStremURL();
        if (!castSession) {
            iniMoviePlayer(obj.getStremURL(), obj.getServerType(), DetailsActivity.this);
            listSub.clear();
            listSub.addAll(obj.getListSub());

            if (listSub.size() != 0) {
                imgSubtitle.setVisibility(VISIBLE);
            }

        } else {
            if (obj.getServerType().toLowerCase().equals("embed")) {

                castSession = false;
                castPlayer.setSessionAvailabilityListener(null);
                castPlayer.release();

                // invisible control ui of exoplayer
                player.setPlayWhenReady(true);
                simpleExoPlayerView.setUseController(true);

                // invisible control ui of casting
                castControlView.setVisibility(GONE);
                chromeCastTv.setVisibility(GONE);


            } else {
                showQueuePopup(DetailsActivity.this, null, getMediaInfo());
            }
        }
    }

    void clear_previous() {

        strCast = "";
        strDirector = "";
        strGenre = "";
        listDownload.clear();
        listInternalDownload.clear();
        listExternalDownload.clear();
        programs.clear();
        castCrews.clear();
    }

    public void showDialog(Context context, List<SubtitleModel> list) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_subtitle, viewGroup, false);
        ImageView cancel = dialogView.findViewById(R.id.cancel);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        SubtitleAdapter adapter = new SubtitleAdapter(context, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

    @Override
    public void onCastSessionAvailable() {
        castSession = true;

        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
        //movieMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, "Test Artist");
        movieMetadata.addImage(new WebImage(Uri.parse(castImageUrl)));
        MediaInfo mediaInfo = new MediaInfo.Builder(mediaUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(movieMetadata).build();

        //array of media sources
        final MediaQueueItem[] mediaItems = {new MediaQueueItem.Builder(mediaInfo).build()};

        castPlayer.loadItems(mediaItems, 0, 3000, Player.REPEAT_MODE_OFF);

        // visible control ui of casting
        castControlView.setVisibility(VISIBLE);
        castControlView.setPlayer(castPlayer);
        castControlView.setVisibilityListener(new PlaybackControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == GONE) {
                    castControlView.setVisibility(VISIBLE);
                    chromeCastTv.setVisibility(VISIBLE);
                }
            }
        });

        // invisible control ui of exoplayer
        player.setPlayWhenReady(false);
        simpleExoPlayerView.setUseController(false);
    }

    @Override
    public void onCastSessionUnavailable() {
        // make cast session false
        castSession = false;


        // invisible control ui of exoplayer
        player.setPlayWhenReady(true);
        simpleExoPlayerView.setUseController(true);

        // invisible control ui of casting
        castControlView.setVisibility(GONE);
        chromeCastTv.setVisibility(GONE);
    }

    public void initServerTypeForTv(String serverType) {
        this.serverType = serverType;
    }

    @Override
    public void onProgramClick(Program program) {
        if (program.getProgramStatus().equals("onaired")) {
            showExoControlForTv();
            iniMoviePlayer(program.getVideoUrl(), "tv", this);
            timeTv.setText(program.getTime());
            programTv.setText(program.getTitle());
        } else {
            new ToastMsg(DetailsActivity.this).toastIconError("Not Yet");
        }
    }

    private class SubtitleAdapter extends RecyclerView.Adapter<SubtitleAdapter.OriginalViewHolder> {

        private List<SubtitleModel> items = new ArrayList<>();
        private Context ctx;

        public SubtitleAdapter(Context context, List<SubtitleModel> items) {
            this.items = items;
            ctx = context;
        }


        @Override
        public SubtitleAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SubtitleAdapter.OriginalViewHolder vh;
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subtitle, parent, false);
            vh = new SubtitleAdapter.OriginalViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(SubtitleAdapter.OriginalViewHolder holder, final int position) {

            final SubtitleModel obj = items.get(position);
            holder.name.setText(obj.getLang());

            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setSelectedSubtitle(mediaSource, obj.getUrl(), ctx);
                    alertDialog.cancel();

                }
            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class OriginalViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            private View lyt_parent;


            public OriginalViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.name);
                lyt_parent = v.findViewById(R.id.lyt_parent);
            }
        }

    }


    private void loadAd() {
        AdsConfig adsConfig = db.getConfigurationData().getAdsConfig();
        if (adsConfig.getAdsEnable().equals("1")) {

            if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.ADMOB)) {
                BannerAds.ShowAdmobBannerAds(this, adView);
                PopUpAds.ShowAdmobInterstitialAds(this);

            } else if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.START_APP)) {
                BannerAds.showStartAppBanner(DetailsActivity.this, adView);
                PopUpAds.showStartappInterstitialAds(DetailsActivity.this);

            } else if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.NETWORK_AUDIENCE)) {
                BannerAds.showFANBanner(this, adView);
                PopUpAds.showFANInterstitialAds(DetailsActivity.this);
            }

        }

    }

    private void initGetData() {
        if (!type.equals("tv")) {

            //----related rv----------
            relatedAdapter = new HomePageAdapter(this, listRelated);
            rvRelated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                    false));
            rvRelated.setHasFixedSize(true);
            rvRelated.setAdapter(relatedAdapter);

            if (type.equals("tvseries")) {

                seasonSpinnerContainer.setVisibility(VISIBLE);

                rvServer.setVisibility(VISIBLE);
                serverIv.setVisibility(GONE);

                rvRelated.removeAllViews();
                listRelated.clear();
                rvServer.removeAllViews();
                listServer.clear();
                listServer.clear();

                downloadBt.setVisibility(GONE);
                watchNowBt.setVisibility(GONE);

                // cast & crew adapter
                castCrewAdapter = new CastCrewAdapter(this, castCrews);
                castRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                castRv.setHasFixedSize(true);
                castRv.setAdapter(castCrewAdapter);

                getSeriesData(type, id);

                if (listSub.size() == 0) {
                    imgSubtitle.setVisibility(GONE);
                }

            } else {
                imgFull.setVisibility(GONE);
                listServer.clear();
                rvRelated.removeAllViews();
                listRelated.clear();
                if (listSub.size() == 0) {
                    imgSubtitle.setVisibility(GONE);
                }

                // cast & crew adapter
                castCrewAdapter = new CastCrewAdapter(this, castCrews);
                castRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                castRv.setHasFixedSize(true);
                castRv.setAdapter(castCrewAdapter);

                getData(type, id);

                final ServerApater.OriginalViewHolder[] viewHolder = {null};
            }

            if (PreferenceUtils.isLoggedIn(DetailsActivity.this)) {
                getFavStatus();
            }

        } else {

            tv = true;
            imgSubtitle.setVisibility(GONE);
            llcomment.setVisibility(GONE);
            serverIv.setVisibility(GONE);

            rvServer.setVisibility(VISIBLE);
            descriptionLayout.setVisibility(GONE);
            lPlay.setVisibility(VISIBLE);

            // hide exo player some control
            hideExoControlForTv();

            tvLayout.setVisibility(VISIBLE);

            // hide program guide if its disable from api
            if (!PreferenceUtils.isProgramGuideEnabled(DetailsActivity.this)) {
                proGuideTv.setVisibility(GONE);
                programRv.setVisibility(GONE);

            }

            watchStatusTv.setText(getString(R.string.watching_on) + " " + getString(R.string.app_name));

            tvRelated.setText(getString(R.string.all_tv_channel));

            rvServer.removeAllViews();
            listServer.clear();
            rvRelated.removeAllViews();
            listRelated.clear();

            programAdapter = new ProgramAdapter(programs, this);
            programAdapter.setOnProgramClickListener(this);
            programRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            programRv.setHasFixedSize(true);
            programRv.setAdapter(programAdapter);

            //----related rv----------
            relatedTvAdapter = new LiveTvHomeAdapter(this, listRelated, TAG);
            rvRelated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvRelated.setHasFixedSize(true);
            rvRelated.setAdapter(relatedTvAdapter);

            imgAddFav.setVisibility(GONE);

            serverAdapter = new ServerApater(this, listServer, "tv");
            rvServer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvServer.setHasFixedSize(true);
            rvServer.setAdapter(serverAdapter);
            getTvData(type, id);
            llBottom.setVisibility(GONE);

            final ServerApater.OriginalViewHolder[] viewHolder = {null};
            serverAdapter.setOnItemClickListener(new ServerApater.OnItemClickListener() {
                @Override
                public void onItemClick(View view, CommonModels obj, int position, ServerApater.OriginalViewHolder holder) {
                    mediaUrl = obj.getStremURL();

                    if (!castSession) {
                        iniMoviePlayer(obj.getStremURL(), obj.getServerType(), DetailsActivity.this);

                    } else {

                        if (obj.getServerType().toLowerCase().equals("embed")) {

                            castSession = false;
                            castPlayer.setSessionAvailabilityListener(null);
                            castPlayer.release();

                            // invisible control ui of exoplayer
                            player.setPlayWhenReady(true);
                            simpleExoPlayerView.setUseController(true);

                            // invisible control ui of casting
                            castControlView.setVisibility(GONE);
                            chromeCastTv.setVisibility(GONE);
                        } else {
                            showQueuePopup(DetailsActivity.this, null, getMediaInfo());
                        }
                    }

                    serverAdapter.chanColor(viewHolder[0], position);
                    holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                    viewHolder[0] = holder;
                }

                @Override
                public void getFirstUrl(String url) {
                    mediaUrl = url;
                }

                @Override
                public void hideDescriptionLayout() {

                }
            });


        }
    }

    private void openWebActivity(String s, Context context, String type) {

        if (isPlaying) {
            player.release();
        }
        progressBar.setVisibility(GONE);
        playerLayout.setVisibility(GONE);

        webView.loadUrl(s);
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setVisibility(VISIBLE);


    }

    public void iniMoviePlayer(String url, String type, Context context) {
        Log.e("vTYpe :: ", type);
        urlType = type;
        if (type.equals("embed") || type.equals("vimeo") || type.equals("gdrive") || type.equals("youtube-live")) {
            isVideo = false;
            openWebActivity(url, context, type);
        } else {
            isVideo = true;
            initVideoPlayer(url, context, type);
        }
    }

    public void initVideoPlayer(String url, Context context, String type) {
        progressBar.setVisibility(VISIBLE);

        if (player != null) {
            player.release();
        }

        webView.setVisibility(GONE);
        playerLayout.setVisibility(VISIBLE);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new
                AdaptiveTrackSelection.Factory(bandwidthMeter);

        DefaultTrackSelector trackSelector = new
                DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        player.setPlayWhenReady(true);
        simpleExoPlayerView.setPlayer(player);
        Uri uri = Uri.parse(url);

        if (type.equals("hls")) {
            mediaSource = hlsMediaSource(uri, context);

        } else if (type.equals("youtube")) {
            Log.e("youtube url  :: ", url);
            extractYoutubeUrl(url, context, 18);
        } else if (type.equals("youtube-live")) {
            Log.e("youtube url  :: ", url);
            extractYoutubeUrl(url, context, 133);
        } else if (type.equals("rtmp")) {
            mediaSource = rtmpMediaSource(uri);
        } else {
            mediaSource = mediaSource(uri, context);
        }

        //Toast.makeText(context, "castSession:"+getCastSessionObj()+"", Toast.LENGTH_SHORT).show();
        player.prepare(mediaSource, true, false);

        player.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == Player.STATE_READY) {

                    isPlaying = true;
                    progressBar.setVisibility(View.GONE);
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    isPlaying = false;
                } else if (playbackState == Player.STATE_BUFFERING) {
                    isPlaying = false;
                    progressBar.setVisibility(VISIBLE);
                } else {
                    // player paused in any state
                    isPlaying = false;
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void extractYoutubeUrl(String url, final Context context, final int tag) {
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = tag;
                    String downloadUrl = ytFiles.get(itag).getUrl();
                    youtubeDownloadUr = downloadUrl;
                    Log.e("YOUTUBE::", String.valueOf(downloadUrl));
                    try {

                        MediaSource mediaSource = mediaSource(Uri.parse(downloadUrl), context);
                        player.prepare(mediaSource, true, false);
                        if (Config.YOUTUBE_VIDEO_AUTO_PLAY) {
                            player.setPlayWhenReady(true);
                        } else {
                            player.setPlayWhenReady(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }.extract(url, true, true);
    }

    private MediaSource rtmpMediaSource(Uri uri) {
        MediaSource videoSource = null;
        RtmpDataSourceFactory dataSourceFactory = new RtmpDataSourceFactory();
        videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);

        return videoSource;
    }

    private MediaSource hlsMediaSource(Uri uri, Context context) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "BS"), bandwidthMeter);

        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);


        return videoSource;
    }

    private MediaSource mediaSource(Uri uri, Context context) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer")).
                createMediaSource(uri);
    }

    public void setSelectedSubtitle(MediaSource mediaSource, String subtitle, Context context) {
        MergingMediaSource mergedSource;
        if (subtitle != null) {
            Uri subtitleUri = Uri.parse(subtitle);

            Format subtitleFormat = Format.createTextSampleFormat(
                    null, // An identifier for the track. May be null.
                    MimeTypes.TEXT_VTT, // The mime type. Must be set correctly.
                    Format.NO_VALUE, // Selection flags for the track.
                    "en"); // The subtitle language. May be null.

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, CLASS_NAME), new DefaultBandwidthMeter());


            MediaSource subtitleSource = new SingleSampleMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);


            mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
            player.prepare(mergedSource, false, false);
            //resumePlayer();

        } else {
            Toast.makeText(context, "there is no subtitle", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFav() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.addToFavorite(Config.API_KEY, userId, id);
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(Call<FavoriteModel> call, retrofit2.Response<FavoriteModel> response) {
                if (response.code() == 200){
                    if (response.body().getStatus().equalsIgnoreCase("success")){
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.body().getMessage());
                        isFav = true;
                        imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                    } else {
                        new ToastMsg(DetailsActivity.this).toastIconError(response.body().getMessage());
                    }
                }else {
                    new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(Call<FavoriteModel> call, Throwable t) {
                new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.error_toast));

            }
        });

    }

    private void paidControl(String isPaid) {
        if (isPaid.equals("1")) {
            if (PreferenceUtils.isLoggedIn(DetailsActivity.this)) {
                if (PreferenceUtils.isActivePlan(DetailsActivity.this)) {
                    if (PreferenceUtils.isValid(DetailsActivity.this)) {
                        contentDetails.setVisibility(VISIBLE);
                        subscriptionLayout.setVisibility(GONE);
                        Log.e("SUBCHECK", "validity: " + PreferenceUtils.isValid(DetailsActivity.this));

                    } else {
                        Log.e("SUBCHECK", "not valid");
                        /*contentDetails.setVisibility(GONE);
                        subscriptionLayout.setVisibility(VISIBLE);*/
                        PreferenceUtils.updateSubscriptionStatus(DetailsActivity.this);
                        //paidControl(isPaid);
                    }
                } else {
                    Log.e("SUBCHECK", "not active plan");
                    contentDetails.setVisibility(GONE);
                    subscriptionLayout.setVisibility(VISIBLE);
                }
            }else {
                startActivity(new Intent(DetailsActivity.this, FirebaseSignUpActivity.class));
                finish();
            }

        } else {
            //free content
            contentDetails.setVisibility(VISIBLE);
            subscriptionLayout.setVisibility(GONE);
        }
    }

    private void getActiveStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(com.burmesesubtitles.app.Config.API_KEY, userId);
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, retrofit2.Response<ActiveStatus> response) {
                ActiveStatus activeStatus = response.body();
                if (!activeStatus.getStatus().equals("active")) {
                    contentDetails.setVisibility(GONE);
                    subscriptionLayout.setVisibility(VISIBLE);
                } else {
                    contentDetails.setVisibility(VISIBLE);
                    subscriptionLayout.setVisibility(GONE);
                }

                PreferenceUtils.updateSubscriptionStatus(DetailsActivity.this);
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void getTvData(final String vtype, final String vId) {
        String type = "?type=" + vtype;
        String id = "&id=" + vId;
        String url = new ApiResources().getDetails() + type + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);
                try {
                    paidControl(response.getString("is_paid"));

                    title = response.getString("tv_name");
                    tvName.setText(title);
                    tvName.setVisibility(GONE);
                    tvTitleTv.setText(title);

                    tvDes.setText(response.getString("description"));
                    V_URL = response.getString("stream_url");
                    castImageUrl = response.getString("thumbnail_url");

                    Picasso.get().load(response.getString("thumbnail_url")).placeholder(R.drawable.album_art_placeholder)
                            .into(tvThumbIv);

                    CommonModels model = new CommonModels();
                    model.setTitle("HD");
                    model.setStremURL(V_URL);
                    model.setServerType(response.getString("stream_from"));
                    listServer.add(model);

                    currentProgramTime = response.getString("current_program_time");
                    currentProgramTitle = response.getString("current_program_title");

                    timeTv.setText(currentProgramTime);
                    programTv.setText(currentProgramTitle);

                    if (PreferenceUtils.isProgramGuideEnabled(DetailsActivity.this)) {
                        JSONArray programGuideArr = response.getJSONArray("program_guide");
                        for (int i = 0; i < programGuideArr.length(); i++) {
                            JSONObject jsonObject = programGuideArr.getJSONObject(i);
                            Program program = new Program();
                            program.setId(jsonObject.getString("id"));
                            program.setTitle(jsonObject.getString("title"));
                            program.setProgramStatus(jsonObject.getString("program_status"));
                            program.setTime(jsonObject.getString("time"));
                            program.setVideoUrl(jsonObject.getString("video_url"));

                            programs.add(program);
                        }

                        if (programs.size() <= 0) {
                            proGuideTv.setVisibility(GONE);
                            programRv.setVisibility(GONE);
                        } else {
                            proGuideTv.setVisibility(VISIBLE);
                            programRv.setVisibility(VISIBLE);
                            programAdapter.notifyDataSetChanged();
                        }
                    }
                    //all tv channel data
                    JSONArray jsonArray = response.getJSONArray("all_tv_channel");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster_url"));
                        models.setTitle(jsonObject.getString("tv_name"));
                        models.setVideoType("tv");
                        models.setIsPaid(jsonObject.getString("is_paid"));
                        models.setId(jsonObject.getString("live_tv_id"));
                        listRelated.add(models);
                    }
                    if (listRelated.size() == 0) {
                        tvRelated.setVisibility(GONE);
                    }
                    relatedTvAdapter.notifyDataSetChanged();
                    //additional media source data
                    JSONArray serverArray = response.getJSONArray("additional_media_source");
                    for (int i = 0; i < serverArray.length(); i++) {
                        JSONObject jsonObject = serverArray.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setTitle(jsonObject.getString("label"));
                        models.setStremURL(jsonObject.getString("url"));
                        models.setServerType(jsonObject.getString("source"));


                        listServer.add(models);
                    }
                    serverAdapter.notifyDataSetChanged();


                } catch (Exception e) {

                } finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                String credentials = RetrofitClient.API_USER_NAME + ":" + RetrofitClient.API_PASSWORD;
                headers.put("API-KEY", Config.API_KEY);
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void getSeriesData(String vtype, String vId) {
        final List<String> seasonList = new ArrayList<>();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SingleDetailsApi api = retrofit.create(SingleDetailsApi.class);
        Call<SingleDetails> call = api.getSingleDetails(Config.API_KEY, vtype, vId);
        call.enqueue(new Callback<SingleDetails>() {
            @Override
            public void onResponse(Call<SingleDetails> call, retrofit2.Response<SingleDetails> response) {
                if (response.code() == 200){
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);

                    SingleDetails singleDetails = response.body();
                    String isPaid = singleDetails.getIsPaid();
                    paidControl(isPaid);

                    title = singleDetails.getTitle();
                    sereisTitleTv.setText(title);
                    castImageUrl = singleDetails.getThumbnailUrl();
                    seriesTitle = title;
                    tvName.setText(title);
                    tvRelease.setText("Release On " + singleDetails.getRelease());
                    tvDes.setText(singleDetails.getDescription());

                    Picasso.get().load(singleDetails.getPosterUrl()).placeholder(R.drawable.album_art_placeholder_large)
                            .into(posterIv);
                    Picasso.get().load(singleDetails.getThumbnailUrl()).placeholder(R.drawable.poster_placeholder)
                            .into(thumbIv);

                    download_check = singleDetails.getEnableDownload();

                    //----director---------------
                    for (int i = 0; i < singleDetails.getDirector().size(); i++) {
                        Director director = singleDetails.getDirector().get(i);
                        if (i == singleDetails.getDirector().size() - 1) {
                            strDirector = strDirector + director.getName();
                        } else {
                            strDirector = strDirector + director.getName() + ", ";
                        }
                    }
                    tvDirector.setText(strDirector);

                    //----cast---------------
                    for (int i = 0; i < singleDetails.getCast().size(); i++) {
                        Cast cast = singleDetails.getCast().get(i);

                        CastCrew castCrew = new CastCrew();
                        castCrew.setId(cast.getStarId());
                        castCrew.setName(cast.getName());
                        castCrew.setUrl(cast.getUrl());
                        castCrew.setImageUrl(cast.getImageUrl());
                        castCrews.add(castCrew);
                    }
                    castCrewAdapter.notifyDataSetChanged();
                    //---genre---------------
                    for (int i = 0; i < singleDetails.getGenre().size(); i++) {
                        Genre genre = singleDetails.getGenre().get(i);
                        if (i == singleDetails.getCast().size() - 1) {
                            strGenre = strGenre + genre.getName();
                        } else {
                            if (i == singleDetails.getGenre().size() - 1) {
                                strGenre = strGenre + genre.getName();
                            } else {
                                strGenre = strGenre + genre.getName() + ", ";
                            }
                        }
                    }
                    setGenreText();

                    //set realease year, imdb rating
                    dGenryTv.setText( singleDetails.getRelease().substring(0,4)+"  |  IMDB - "+singleDetails.getImdb().toString()+"/10");


                    //----related tv series---------------
                    for (int i = 0; i < singleDetails.getRelatedTvseries().size(); i++) {
                        RelatedMovie relatedTvSeries = singleDetails.getRelatedTvseries().get(i);

                        CommonModels models = new CommonModels();
                        models.setTitle(relatedTvSeries.getTitle());
                        models.setImageUrl(relatedTvSeries.getThumbnailUrl());
                        models.setId(relatedTvSeries.getVideosId());
                        models.setVideoType("tvseries");
                        models.setIsPaid(relatedTvSeries.getIsPaid());
                        listRelated.add(models);
                    }
                    if (listRelated.size() == 0) {
                        tvRelated.setVisibility(GONE);
                    }
                    relatedAdapter.notifyDataSetChanged();

                    //----seasson------------
                    for (int i = 0; i <singleDetails.getSeason().size(); i++) {
                        Season season = singleDetails.getSeason().get(i);

                        CommonModels models = new CommonModels();
                        String season_name = season.getSeasonsName();
                        models.setTitle(season.getSeasonsName());
                        seasonList.add("Season: " + season.getSeasonsName());

                        //----episode------
                        List<EpiModel> epList = new ArrayList<>();
                        epList.clear();
                        for (int j = 0; j <singleDetails.getSeason().get(i).getEpisodes().size(); j++) {
                            Episode episode = singleDetails.getSeason().get(i).getEpisodes().get(j);

                            EpiModel model = new EpiModel();
                            model.setSeson(season_name);
                            model.setEpi(episode.getEpisodesName());
                            model.setStreamURL(episode.getFileUrl());
                            model.setServerType(episode.getFileType());
                            model.setImageUrl(episode.getImageUrl());
                            epList.add(model);
                        }
                        models.setListEpi(epList);
                        listServer.add(models);
                        setSeasonData(seasonList);

                    }
                }
            }

            @Override
            public void onFailure(Call<SingleDetails> call, Throwable t) {

            }
        });
    }

    public void setSeasonData(List<String> seasonData) {

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, seasonData);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        seasonSpinner.setAdapter(aa);

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rvServer.removeAllViewsInLayout();
                rvServer.setLayoutManager(new LinearLayoutManager(DetailsActivity.this,
                        RecyclerView.HORIZONTAL, false));
                EpisodeAdapter episodeAdapter = new EpisodeAdapter(DetailsActivity.this,
                        listServer.get(i).getListEpi());
                rvServer.setAdapter(episodeAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setGenreText() {

        tvGenre.setText(strGenre);

        dGenryTv.setText(strGenre);

    }

    private void getData(String vtype, String vId) {
        strCast = "";
        strDirector = "";
        strGenre = "";

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SingleDetailsApi api = retrofit.create(SingleDetailsApi.class);
        Call<SingleDetails> call = api.getSingleDetails(Config.API_KEY, vtype, vId);
        call.enqueue(new Callback<SingleDetails>() {
            @Override
            public void onResponse(Call<SingleDetails> call, retrofit2.Response<SingleDetails> response) {
                if (response.code() == 200){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    SingleDetails singleDetails = response.body();
                    paidControl(singleDetails.getIsPaid());
                    Log.e("Download", "size: " + singleDetails.getDownloadLinks().size());
                    Log.e("Download", "size: " + singleDetails.getTitle());

                    download_check = singleDetails.getEnableDownload();
                    castImageUrl = singleDetails.getThumbnailUrl();
                    if (download_check.equals("1")) {
                        download_text.setVisibility(VISIBLE);
                        downloadBt.setVisibility(VISIBLE);
                    } else {
                        download_text.setVisibility(GONE);
                        downloadBt.setVisibility(GONE);
                    }
                    title = singleDetails.getTitle();
                    movieTitle = title;

                    tvName.setText(title);
                    tvRelease.setText("Release On " + singleDetails.getRelease());
                    tvDes.setText(singleDetails.getDescription());


                    Picasso.get().load(singleDetails.getPosterUrl()).placeholder(R.drawable.album_art_placeholder_large)
                            .into(posterIv);
                    Picasso.get().load(singleDetails.getThumbnailUrl()).placeholder(R.drawable.poster_placeholder)
                            .into(thumbIv);

                    //----director---------------
                    for (int i = 0; i < singleDetails.getDirector().size(); i++) {
                        Director director = response.body().getDirector().get(i);
                        if (i == singleDetails.getDirector().size() - 1) {
                            strDirector = strDirector + director.getName();
                        } else {
                            strDirector = strDirector + director.getName() + ", ";
                        }
                    }
                    tvDirector.setText(strDirector);

                    //----cast---------------
                    for (int i = 0; i < singleDetails.getCast().size(); i++) {
                        Cast cast = singleDetails.getCast().get(i);

                        CastCrew castCrew = new CastCrew();
                        castCrew.setId(cast.getStarId());
                        castCrew.setName(cast.getName());
                        castCrew.setUrl(cast.getUrl());
                        castCrew.setImageUrl(cast.getImageUrl());

                        castCrews.add(castCrew);

                    }
                    castCrewAdapter.notifyDataSetChanged();

                    //---genre---------------
                    for (int i = 0; i < singleDetails.getGenre().size(); i++) {
                        Genre genre = singleDetails.getGenre().get(i);
                        if (i == singleDetails.getCast().size() - 1) {
                            strGenre = strGenre + genre.getName();
                        } else {
                            if (i == singleDetails.getGenre().size() - 1) {
                                strGenre = strGenre + genre.getName();
                            } else {
                                strGenre = strGenre + genre.getName() + ", ";
                            }
                        }
                    }
                    tvGenre.setText(strGenre);
                    //set realease year, imdb rating
                    dGenryTv.setText( singleDetails.getRelease().substring(0,4)+"  |  IMDB - "+singleDetails.getImdb().toString()+"/10");

                    //-----server----------
                    List<Video> serverList = new ArrayList<>();
                    serverList.addAll(singleDetails.getVideos());
                    for (int i = 0; i < serverList.size(); i++){
                        Video video = serverList.get(i);

                        CommonModels models = new CommonModels();
                        models.setTitle(video.getLabel());
                        models.setStremURL(video.getFileUrl());
                        models.setServerType(video.getFileType());

                        if (video.getFileType().equals("mp4")) {
                            V_URL = video.getFileUrl();
                        }

                        //----subtitle-----------
                        List<Subtitle> subArray = new ArrayList<>();
                        subArray.addAll(singleDetails.getVideos().get(i).getSubtitle());
                        if (subArray.size() != 0) {

                            List<SubtitleModel> list = new ArrayList<>();
                            for (int j = 0; j < subArray.size(); j++) {
                                Subtitle subtitle = subArray.get(j);
                                SubtitleModel subtitleModel = new SubtitleModel();
                                subtitleModel.setUrl(subtitle.getUrl());
                                subtitleModel.setLang(subtitle.getLanguage());
                                list.add(subtitleModel);
                            }
                            if (i == 0) {
                                listSub.addAll(list);
                            }
                            models.setListSub(list);
                        } else {
                            models.setSubtitleURL(strSubtitle);
                        }
                        listServer.add(models);
                    }

                    if (serverAdapter != null) {
                        serverAdapter.notifyDataSetChanged();
                    }

                    //----related post---------------
                    for (int i = 0; i < singleDetails.getRelatedMovie().size(); i++) {
                        RelatedMovie relatedMovie = singleDetails.getRelatedMovie().get(i);
                        CommonModels models = new CommonModels();
                        models.setTitle(relatedMovie.getTitle());
                        models.setImageUrl(relatedMovie.getThumbnailUrl());
                        models.setId(relatedMovie.getVideosId());
                        models.setVideoType("movie");
                        models.setIsPaid(relatedMovie.getIsPaid());
                        models.setIsPaid(relatedMovie.getIsPaid());
                        listRelated.add(models);
                    }

                    if (listRelated.size() == 0) {
                        tvRelated.setVisibility(GONE);
                    }
                    relatedAdapter.notifyDataSetChanged();

                    //----download list---------
                    listExternalDownload.clear();
                    listInternalDownload.clear();
                    for (int i = 0; i < singleDetails.getDownloadLinks().size(); i++) {
                        DownloadLink downloadLink = singleDetails.getDownloadLinks().get(i);

                        CommonModels models = new CommonModels();
                        models.setTitle(downloadLink.getLabel());
                        models.setStremURL(downloadLink.getDownloadUrl());
                        models.setFileSize(downloadLink.getFileSize());
                        models.setResulation(downloadLink.getResolution());
                        models.setInAppDownload(downloadLink.isInAppDownload());
                        if (downloadLink.isInAppDownload()) {
                            listInternalDownload.add(models);
                        } else {
                            listExternalDownload.add(models);
                        }
                    }

                }else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<SingleDetails> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getFavStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.verifyFavoriteList(Config.API_KEY, userId, id);
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(Call<FavoriteModel> call, retrofit2.Response<FavoriteModel> response) {
                if (response.code() == 200){
                    if (response.body().getStatus().equalsIgnoreCase("success")){
                        isFav = true;
                        imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                        imgAddFav.setVisibility(VISIBLE);
                    } else {
                        isFav = false;
                        imgAddFav.setBackgroundResource(R.drawable.ic_favorite_border_white);
                        imgAddFav.setVisibility(VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<FavoriteModel> call, @NotNull Throwable t) {

            }
        });

    }

    private void removeFromFav() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.removeFromFavorite(Config.API_KEY, userId, id);
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(Call<FavoriteModel> call, retrofit2.Response<FavoriteModel> response) {
                if (response.code() == 200){
                    if (response.body().getStatus().equalsIgnoreCase("success")){
                        isFav = false;
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.body().getMessage());
                        imgAddFav.setBackgroundResource(R.drawable.ic_favorite_border_white);
                    } else {
                        isFav = true;
                        new ToastMsg(DetailsActivity.this).toastIconError(response.body().getMessage());
                        imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                    }
                }
            }

            @Override
            public void onFailure(Call<FavoriteModel> call, Throwable t) {
                new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.fetch_error));
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addComment(String videoId, String userId, final String comments) {

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CommentApi api = retrofit.create(CommentApi.class);
        Call<PostCommentModel> call = api.postComment(Config.API_KEY, videoId, userId, comments);
        call.enqueue(new Callback<PostCommentModel>() {
            @Override
            public void onResponse(Call<PostCommentModel> call, retrofit2.Response<PostCommentModel> response) {
                if (response.body().getStatus().equals("success")){
                    rvComment.removeAllViews();
                    listComment.clear();
                    getComments();
                    etComment.setText("");
                    new ToastMsg(DetailsActivity.this).toastIconSuccess(response.body().getMessage());
                }else {
                    new ToastMsg(DetailsActivity.this).toastIconError(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostCommentModel> call, Throwable t) {

            }
        });
    }

    private void getComments() {

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CommentApi api = retrofit.create(CommentApi.class);
        Call<List<GetCommentsModel>> call = api.getAllComments(Config.API_KEY, id);
        call.enqueue(new Callback<List<GetCommentsModel>>() {
            @Override
            public void onResponse(Call<List<GetCommentsModel>> call, retrofit2.Response<List<GetCommentsModel>> response) {
                if (response.code() == 200) {
                    listComment.addAll(response.body());

                    commentsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<GetCommentsModel>> call, Throwable t) {

            }
        });

    }

    public void hideDescriptionLayout() {
        descriptionLayout.setVisibility(GONE);
        lPlay.setVisibility(VISIBLE);
    }

    public void showSeriesLayout() {
        seriestLayout.setVisibility(VISIBLE);
    }

    public void showDescriptionLayout() {
        descriptionLayout.setVisibility(VISIBLE);
        lPlay.setVisibility(GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e("ACTIVITY:::", "PAUSE" + isPlaying);

        if (isPlaying && player != null) {

            //Log.e("PLAY:::","PAUSE");

            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //castManager.removeProgressWatcher(this);

        Log.e("ACTIVITY:::", "STOP" + isPlaying);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetCastPlayer();
        releasePlayer();

    }

    @Override
    public void onBackPressed() {

        if (activeMovie) {

            setPlayerNormalScreen();

            player.setPlayWhenReady(false);
            player.stop();
            showDescriptionLayout();
            activeMovie = false;
        } else {
            releasePlayer();
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //startPlayer();
        if (player != null) {
            if (type.equals("youtube") || type.equals("youtube-live")) {
                if (Config.YOUTUBE_VIDEO_AUTO_PLAY) {
                    player.setPlayWhenReady(true);
                } else {
                    player.setPlayWhenReady(false);
                }
            } else {
                player.setPlayWhenReady(true);
            }

        }

    }

    public void releasePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.stop();
            player.release();
            player = null;
            simpleExoPlayerView.setPlayer(null);
            simpleExoPlayerView = null;
        }
    }

    public void setMediaUrlForTvSeries(String url, String season, String episod) {
        mediaUrl = url;
        this.season = season;
        this.episod = episod;
    }

    public boolean getCastSession() {
        return castSession;
    }

    public void resetCastPlayer() {
        if (castPlayer != null) {
            castPlayer.setPlayWhenReady(false);
            castPlayer.release();
        }
    }

    public void showQueuePopup(final Context context, View view, final MediaInfo mediaInfo) {
        CastSession castSession =
                CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
        if (castSession == null || !castSession.isConnected()) {
            Log.w(TAG, "showQueuePopup(): not connected to a cast device");
            return;
        }
        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            Log.w(TAG, "showQueuePopup(): null RemoteMediaClient");
            return;
        }
        MediaQueueItem queueItem = new MediaQueueItem.Builder(mediaInfo).setAutoplay(
                true).setPreloadTime(PRELOAD_TIME_S).build();
        MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};
        remoteMediaClient.queueLoad(newItemArray, 0,
                MediaStatus.REPEAT_MODE_REPEAT_OFF, null);

    }

    public void playNextCast(MediaInfo mediaInfo) {

        //simpleExoPlayerView.setPlayer(castPlayer);
        simpleExoPlayerView.setUseController(false);
        castControlView.setVisibility(VISIBLE);
        castControlView.setPlayer(castPlayer);
        //simpleExoPlayerView.setDefaultArtwork();
        castControlView.setVisibilityListener(new PlaybackControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == GONE) {
                    castControlView.setVisibility(VISIBLE);
                    chromeCastTv.setVisibility(VISIBLE);
                }
            }
        });
        CastSession castSession =
                CastContext.getSharedInstance(this).getSessionManager().getCurrentCastSession();

        if (castSession == null || !castSession.isConnected()) {
            Log.w(TAG, "showQueuePopup(): not connected to a cast device");
            return;
        }

        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();

        if (remoteMediaClient == null) {
            Log.w(TAG, "showQueuePopup(): null RemoteMediaClient");
            return;
        }
        MediaQueueItem queueItem = new MediaQueueItem.Builder(mediaInfo).setAutoplay(
                true).setPreloadTime(PRELOAD_TIME_S).build();
        MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};

        remoteMediaClient.queueLoad(newItemArray, 0,
                MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
        castPlayer.setPlayWhenReady(true);

    }

    public MediaInfo getMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
        //movieMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, "Test Artist");
        movieMetadata.addImage(new WebImage(Uri.parse(castImageUrl)));
        MediaInfo mediaInfo = new MediaInfo.Builder(mediaUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(movieMetadata).build();

        return mediaInfo;

    }

    public void downloadVideo(final String url) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    public void run() {
                        downloadFile(url);
                    }
                };
                handler.post(runnable);

            } else {
                requestPermission(); // Code for permission
            }
        } else {

            // Code for Below 23 API Oriented Device
            // Do next code

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                public void run() {
                    downloadFile(url);
                }
            };
            handler.post(runnable);
        }


    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new ToastMsg(DetailsActivity.this).toastIconSuccess("Now You can download.");
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void downloadFile(String url) {
        String fileName = "";
        int notificationId = new Random().nextInt(100 - 1) - 1;
        Log.d("id:", notificationId + "");

        if (url == null || url.isEmpty()) {
            return;
        }

        if (type.equals("movie")) {
            fileName = tvName.getText().toString();
        } else {
            fileName = seriesTitle + "_" + season + "_" + episod;
        }

        String path = Constants.getDownloadDir(DetailsActivity.this);

        String fileExt = url.substring(url.lastIndexOf('.')); // output like .mkv
        fileName = fileName + fileExt;

        fileName = fileName.replaceAll(" ", "_");
        fileName = fileName.replaceAll(":", "_");

        File file = new File(path, "e_" + fileName); // e_ for encode
        if (file.exists()) {
            new ToastMsg(DetailsActivity.this).toastIconError(getString(R.string.file_already_downloaded));
            return;
        }

        //download with workManager
        String dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        Data data = new Data.Builder()
                .putString("url", url)
                .putString("dir", dir)
                .putString("fileName", fileName)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DownloadWorkManager.class)
                .setInputData(data)
                .build();

        String workId = request.getId().toString();
        Constants.workId = workId;
        WorkManager.getInstance().enqueue(request);
    }


    public void hideExoControlForTv() {
        exoRewind.setVisibility(GONE);
        exoForward.setVisibility(GONE);
        liveTv.setVisibility(VISIBLE);
        seekbarLayout.setVisibility(GONE);
    }

    public void showExoControlForTv() {
        exoRewind.setVisibility(VISIBLE);
        exoForward.setVisibility(VISIBLE);
        liveTv.setVisibility(GONE);
        seekbarLayout.setVisibility(VISIBLE);
        watchLiveTv.setVisibility(VISIBLE);
        liveTv.setVisibility(GONE);
        watchStatusTv.setText(getResources().getString(R.string.watching_catch_up_tv));
    }

    private void getScreenSize() {
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        sWidth = size.x;
        sHeight = size.y;
        //Toast.makeText(this, "fjiaf", Toast.LENGTH_SHORT).show();
    }

    public class RelativeLayoutTouchListener implements View.OnTouchListener {


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //touch is start
                    downX = event.getX();
                    downY = event.getY();
                    if (event.getX() < (sWidth / 2)) {

                        //here check touch is screen left or right side
                        intLeft = true;
                        intRight = false;

                    } else if (event.getX() > (sWidth / 2)) {

                        //here check touch is screen left or right side
                        intLeft = false;
                        intRight = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_MOVE:

                    //finger move to screen
                    float x2 = event.getX();
                    float y2 = event.getY();

                    diffX = (long) (Math.ceil(event.getX() - downX));
                    diffY = (long) (Math.ceil(event.getY() - downY));

                    if (Math.abs(diffY) > Math.abs(diffX)) {
                        if (intLeft) {
                            //if left its for brightness

                            if (downY < y2) {
                                //down swipe brightness decrease
                            } else if (downY > y2) {
                                //up  swipe brightness increase
                            }

                        } else if (intRight) {

                            //if right its for audio
                            if (downY < y2) {
                                //down swipe volume decrease
                                mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);

                            } else if (downY > y2) {
                                //up  swipe volume increase
                                mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                            }
                        }
                    }
            }
            return true;
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }
}

