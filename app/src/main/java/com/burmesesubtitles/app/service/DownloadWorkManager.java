package com.burmesesubtitles.app.service;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.burmesesubtitles.app.R;
import com.burmesesubtitles.app.database.DatabaseHelper;
import com.burmesesubtitles.app.models.Work;
import com.burmesesubtitles.app.utils.Constants;
import com.burmesesubtitles.app.utils.ToastMsg;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class DownloadWorkManager extends Worker {
    private LiveDataHelper liveDataHelper;
    public static final String START_PAUSE_ACTION = "startPause";
    public static final String START_PAUSE_STATUS = "startPauseStatus";
    public static final String START_PAUSE_FEEDBACK_STATUS = "startPauseFeedbackStatus";
    public static final String PROGRESS_RECEIVER = "progress_receiver";

    String fileName;
    int downloadId;

    Context context;

    DatabaseHelper helper;
    private boolean isDownloading;
    private String workId;

    private long downloadByte, totalByte;

    public DownloadWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        helper = new DatabaseHelper(context);
        workId = Constants.workId;
        liveDataHelper = LiveDataHelper.getInstance();

        Data data = getInputData();
        final String url = data.getString("url");
        final String dir = data.getString("dir");
        final String fileName = data.getString("fileName");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Enabling database for resume support even after the application is killed:
                final PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                        .setDatabaseEnabled(true)
                        .setReadTimeout(30_000)
                        .setConnectTimeout(30_000)
                        .build();
                PRDownloader.initialize(getApplicationContext(), config);
                String path = Constants.getDownloadDir(context) + context.getResources().getString(R.string.app_name);
                downloadId = PRDownloader.download(url, path, fileName)
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                isDownloading = true;
                                Intent intent = new Intent(START_PAUSE_FEEDBACK_STATUS);
                                intent.putExtra("result", RESULT_OK);
                                intent.putExtra("downloadId", downloadId);
                                intent.putExtra("status", "start");
                                intent.putExtra("fileName", fileName);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                                // set app close status false
                                Work work = helper.getWorkByDownloadId(downloadId);
                                work.setAppCloseStatus("false");
                                // save the data to the database
                                helper.updateWork(work);
                                new ToastMsg(context).toastIconSuccess("Download started.");

                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {
                                isDownloading = false;

                                Intent intent = new Intent(START_PAUSE_FEEDBACK_STATUS);
                                intent.putExtra("result", RESULT_OK);
                                intent.putExtra("downloadId", downloadId);
                                intent.putExtra("status", "pause");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                                Work work = helper.getWorkByDownloadId(downloadId);

                                work.setDownloadSize(downloadByte + "");
                                work.setTotalSize(totalByte + "");
                                work.setDownloadStatus("paused");
                                work.setAppCloseStatus("false");
                                work.setFileName(fileName);
                                // save the data to the database
                                helper.updateWork(work);

                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                isDownloading = false;
                                helper.deleteAllDownloadData();
                                new ToastMsg(context).toastIconSuccess("Download canceled");
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
                                Intent intent = new Intent(PROGRESS_RECEIVER);
                                intent.putExtra("result", RESULT_OK);
                                intent.putExtra("downloadId", downloadId);
                                intent.putExtra("currentByte", progress.currentBytes);
                                intent.putExtra("workId", workId);
                                intent.putExtra("totalByte", progress.totalBytes);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                                downloadByte = progress.currentBytes;
                                totalByte = progress.totalBytes;
                                final int currentProgress = (int) ((progress.currentBytes / 1024) / 1024); //mb
                                liveDataHelper.updatePercentage(currentProgress); //mb);
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                helper.deleteByDownloadId(downloadId);
                                new ToastMsg(context).toastIconSuccess("Download Completed");
                                liveDataHelper.completeStatus(true);
                            }

                            @Override
                            public void onError(Error error) {
                                error.getConnectionException().printStackTrace();
                                new ToastMsg(context).toastIconError("something went wrong");
                                helper.deleteAllDownloadData();
                            }
                        });

                boolean isDuplicationFound = false;
                List<Work> workList = helper.getAllWork();
                for (Work w : workList) {
                    if (w.getDownloadId() == downloadId) {
                        isDuplicationFound = true;
                    }
                }

                if (!isDuplicationFound) {
                    Work work = new Work();
                    work.setWorkId(Constants.workId);
                    work.setDownloadId(downloadId);
                    work.setFileName(fileName);
                    work.setUrl(url);
                    work.setAppCloseStatus("false");
                    long v = helper.insertWork(work);
                }
            }
        }).start();

        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();


    }
}
