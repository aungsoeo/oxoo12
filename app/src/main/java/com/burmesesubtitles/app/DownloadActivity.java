package com.burmesesubtitles.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.PRDownloader;
import com.downloader.Status;
import com.burmesesubtitles.app.adapters.DownloadHistoryAdapter;
import com.burmesesubtitles.app.adapters.FileDownloadAdapter;
import com.burmesesubtitles.app.database.DatabaseHelper;
import com.burmesesubtitles.app.models.Work;
import com.burmesesubtitles.app.models.VideoFile;
import com.burmesesubtitles.app.service.DownloadWorkManager;
import com.burmesesubtitles.app.service.LiveDataHelper;
import com.burmesesubtitles.app.utils.Constants;
import com.burmesesubtitles.app.utils.ToastMsg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

public class DownloadActivity extends AppCompatActivity implements FileDownloadAdapter.OnProgressUpdateListener {
    public static DownloadActivity instance;
    public static final String ACTION_PLAY_VIDEO = "play_video";
    private RecyclerView downloadRv, downloadedFileRv;
    private ProgressBar progressBar;
    private TextView amountTv;
    private LinearLayout progressLayout;

    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;

    private List<Work> works = new ArrayList<>();
    private TextView downloadStatusTv;
    private TextView downloadedFileTV;
    private Work work;
    private ImageView startPauseIv, cancelIV;
    private boolean isDownloading = true;
    private List<FileDownloadAdapter.ViewHolder> progressViewHolderList;
    private List<FileDownloadAdapter.ViewHolder> actionViewHolderList;
    private int actionPosition;
    private FileDownloadAdapter adapter;
    private DownloadHistoryAdapter downloadHistoryAdapter;
    private List<VideoFile> videoFiles = new ArrayList<>();
    private boolean isDark;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        setContentView(R.layout.activity_download);

        downloadRv = findViewById(R.id.download_rv);
        downloadedFileTV = findViewById(R.id.downloaded_file_tv);
        downloadedFileRv = findViewById(R.id.downloaded_file_rv);
        toolbar = findViewById(R.id.appBar);
        coordinatorLayout = findViewById(R.id.coordinator_lyt);
        progressLayout = findViewById(R.id.progress_layout);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Downloads");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.dark));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        dbHelper = new DatabaseHelper(this);
        works = dbHelper.getAllWork();
        if (works.size() > 0) {
            coordinatorLayout.setVisibility(View.GONE);
        }

        downloadRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FileDownloadAdapter(works, this, isDark);
        adapter.setProgressUpdateListener(this);
        downloadRv.setHasFixedSize(true);
        downloadRv.setAdapter(adapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter(DownloadWorkManager.PROGRESS_RECEIVER));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                startPauseFeedbackReceiver, new IntentFilter(DownloadWorkManager.START_PAUSE_FEEDBACK_STATUS));

        registerReceiver(playVideoBroadcast, new IntentFilter(ACTION_PLAY_VIDEO));

        getDownloadFiles();

        LiveDataHelper.getInstance().observeIsCompleted()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean isCompleted) {
                        // download completed
                        if (isCompleted) {
                            works = dbHelper.getAllWork();
                            Log.e("Download", "complete: " + works.size());
                            adapter.notifyDataSetChanged();
                            videoFiles.clear();
                            downloadedFileRv.removeAllViews();
                            getDownloadFiles();
                            downloadRv.setVisibility(View.GONE);
                        }
                    }
                });

        LiveDataHelper.getInstance().observePercentage()
                .observe(this, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (integer != null) {
                            coordinatorLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }


    @Override
    public void updateProgress(int adapterPos, Work work, List<FileDownloadAdapter.ViewHolder> viewHolderList) {
        this.work = work;
        this.progressViewHolderList = viewHolderList;
    }

    @Override
    public void onItemClick(int position, Work work, ImageView startPauseIv, List<FileDownloadAdapter.ViewHolder> viewHolderList) {
        this.actionViewHolderList = viewHolderList;
        this.startPauseIv = startPauseIv;
        this.actionPosition = position;
        if (work.getAppCloseStatus().equals("true")) {
            resumeDownload(work.getUrl());
            Work w = works.get(position);
            w.setAppCloseStatus("false");
            works.set(position, w);
            adapter.notifyDataSetChanged();

        } else {
            if (PRDownloader.getStatus(work.getDownloadId()) == Status.RUNNING) {
                PRDownloader.pause(work.getDownloadId());
            } else if (PRDownloader.getStatus(work.getDownloadId()) == Status.PAUSED) {
                PRDownloader.resume(work.getDownloadId());
            }
        }
    }

    @Override
    public void OnCancelClick(int position, Work work, ImageView cancelIV, List<FileDownloadAdapter.ViewHolder> viewHolderList) {
        this.actionViewHolderList = viewHolderList;
        this.cancelIV = cancelIV;
        this.actionPosition = position;
        PRDownloader.cancel(work.getDownloadId());
        dbHelper.deleteAllDownloadData();
        works.clear();
        works = dbHelper.getAllWork();
        if (works.size() == 0) {
            downloadRv.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        getDownloadFiles();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("result", 0);
            final int downloadId = intent.getIntExtra("downloadId", 0);
            final String workId = intent.getStringExtra("workId");
            final long downloadedByte = intent.getLongExtra("currentByte", 0);
            final long totalByte = intent.getLongExtra("totalByte", 0);
            Log.e("download", "Byte: : " + resultCode + "");
            if (resultCode == RESULT_OK) {
                Log.e("download", "Byte: : " + totalByte + "");

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    public void run() {

                        double totalKb = totalByte / 1024;
                        double downloadKb = downloadedByte / 1024;

                        double totalMb = totalKb / 1024;
                        double downloadMb = downloadKb / 1024;

                        // set download status
                        if (work != null) {

                            // getting current position
                            int position = 0;
                            for (Work work : works) {
                                if (work.getDownloadId() == downloadId) {
                                    break;
                                }
                                position++;
                            }

                            if (progressViewHolderList != null && position <= progressViewHolderList.size() - 1) {
                                // getting current viewHolder by position
                                FileDownloadAdapter.ViewHolder viewHolder = progressViewHolderList.get(position);
                                downloadStatusTv = viewHolder.downloadStatusTv;
                                amountTv = viewHolder.downloadAmountTv;
                                progressBar = viewHolder.progressBar;

                                if (downloadStatusTv != null) {
                                    if (downloadedByte == totalByte) {
                                        downloadStatusTv.setText(getResources().getString(R.string.download_completed));
                                    } else {
                                        downloadStatusTv.setText(getResources().getString(R.string.downloading));
                                    }
                                }

                                // set download amount
                                if (amountTv != null) {
                                    amountTv.setText(parseDouble(String.format("%.1f", downloadMb)) + " MB / "
                                            + parseDouble(String.format("%.1f", totalMb)) + " MB");
                                }

                                // update progress bar
                                if (progressBar != null) {
                                    progressBar.setMax((int) totalKb);
                                    progressBar.setProgress((int) downloadKb);
                                }
                            }
                        }
                    }
                };
                handler.post(runnable);

                //updateProgress(totalByte, downloadedByte, viewHolder, downloadId);
//                progressTv.setText(downloadedByte+" / "+ totalByte);
//                Toast.makeText(context, "icon changed"+downloadId, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver startPauseFeedbackReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int resultCode = intent.getIntExtra("result", 0);
            int workId = intent.getIntExtra("workId", 0);
            int downloadId = intent.getIntExtra("downloadId", 0);
            String status = intent.getStringExtra("status");
            Log.d("id:", downloadId + " : " + workId);
            if (resultCode == RESULT_OK) {
                if (status.equals("pause")) {
                    isDownloading = false;
                    if (actionViewHolderList != null) {
                        actionViewHolderList.get(actionPosition).downloadStatusTv.setText("Download Paused");
                        actionViewHolderList.get(actionPosition).startPauseIv.setImageDrawable(getResources()
                                .getDrawable(R.drawable.ic_play_circle_tranparent));
                    }
                } else {
                    if (actionViewHolderList != null) {
                        isDownloading = true;
                        actionViewHolderList.get(actionPosition).downloadStatusTv.setText("Downloading...");
                        actionViewHolderList.get(actionPosition).startPauseIv.setImageDrawable(getResources()
                                .getDrawable(R.drawable.ic_pause_circle_transparent));
                    }
                }
            }
        }
    };


    private BroadcastReceiver playVideoBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String fileName = intent.getStringExtra("fileName");

            String url = Constants.getDownloadDir(DownloadActivity.this) + context.getResources().getString(R.string.app_name)
                    + File.separator + fileName;
            Log.e("donwloadDir", "url: " + url);

            File file = new File(url);

            if (!file.exists()) {
                new ToastMsg(DownloadActivity.this).toastIconError(getString(R.string.file_not_found));
                return;
            }

            Log.d("url:", url);

            // hide the progress layout
            SystemClock.sleep(3000);
            progressHideShowControl();


            Intent playIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setDataAndType(Uri.parse(url), "video/*");
            startActivity(playIntent);

        }
    };

    public void resumeDownload(String url) {
        String dir = getExternalCacheDir().toString();

        String fileName = url.substring(url.lastIndexOf('/') + 1);

        File file = new File(dir + "/" + fileName);
        if (file.exists()) {
            Toast.makeText(this, getString(R.string.file_already_downloaded), Toast.LENGTH_SHORT).show();
            return;
        }

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

    public void getDownloadFiles() {
        String path = Constants.getDownloadDir(DownloadActivity.this) + getResources().getString(R.string.app_name);
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        assert files != null;

        for (File file : files) {
            String fileName = file.getName();
            String filePath = file.getPath();
            String extension = fileName.substring(fileName.lastIndexOf("."));

            if (!extension.equals(".temp")) {
                VideoFile vf = new VideoFile();
                vf.setFileName(fileName);
                vf.setLastModified(file.lastModified());
                vf.setTotalSpace(file.length());
                vf.setPath(filePath);
                vf.setFileExtension(extension);
                videoFiles.add(vf);
            }
        }


        if (videoFiles.size() > 0) {
            coordinatorLayout.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            downloadRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
            downloadedFileRv.setLayoutManager(layoutManager);
            downloadHistoryAdapter = new DownloadHistoryAdapter(this, videoFiles);
            downloadedFileRv.setHasFixedSize(true);
            downloadedFileRv.setAdapter(downloadHistoryAdapter);

        } else {
            downloadedFileTV.setVisibility(View.GONE);
            if (works.size() == 0){
                downloadRv.setVisibility(View.GONE);
                downloadedFileTV.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
            }

        }
    }

    public void progressHideShowControl() {
        if (progressLayout.getVisibility() == View.VISIBLE) {
            progressLayout.setVisibility(View.GONE);
        } else {
            progressLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Download", "OnResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(playVideoBroadcast);
        Log.e("Download", "OnDestroy");
    }


}
