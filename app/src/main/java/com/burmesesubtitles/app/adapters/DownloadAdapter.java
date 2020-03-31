package com.burmesesubtitles.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.burmesesubtitles.app.R;
import com.burmesesubtitles.app.models.CommonModels;
import com.burmesesubtitles.app.service.DownloadWorkManager;
import com.burmesesubtitles.app.utils.Constants;
import com.burmesesubtitles.app.utils.ToastMsg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.OriginalViewHolder> {

    private List<CommonModels> items = new ArrayList<>();
    private Context ctx;
    private boolean isDialog;
    private View v = null;

    private ServerApater.OnItemClickListener mOnItemClickListener;

    private DownloadAdapter.OriginalViewHolder viewHolder;


    public DownloadAdapter(Context ctx, List<CommonModels> items,  boolean isDialog) {
        this.ctx = ctx;
        this.items = items;
        this.isDialog = isDialog;
    }

    @Override
    public DownloadAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DownloadAdapter.OriginalViewHolder vh;
        if (isDialog){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_download_item_vertical, parent, false);
        }else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_download_item, parent, false);
        }
        vh = new DownloadAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DownloadAdapter.OriginalViewHolder holder, final int position) {

        final CommonModels obj = items.get(position);
        holder.name.setText(obj.getTitle());
        holder.resolution.setText(obj.getResulation() + "," );
        holder.size.setText(obj.getFileSize());

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obj.isInAppDownload()) {
                    //in app download enabled
                    downloadFileInsideApp(obj.getTitle(), obj.getStremURL());
                } else {
                    String url = obj.getStremURL();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    ctx.startActivity(i);
                }

            }
        });

    }

    private void downloadFileInsideApp(String title, String streamURL) {
        String fileName = title.toString();
        int notificationId = new Random().nextInt(100 - 1) - 1;
        if (streamURL == null || streamURL.isEmpty()) {
            return;
        }
        String path = Constants.getDownloadDir(ctx) + ctx.getResources().getString(R.string.app_name);

        String fileExt = streamURL.substring(streamURL.lastIndexOf('.')); // output like .mkv
        fileName = fileName + fileExt;

        fileName = fileName.replaceAll(" ", "_");
        fileName = fileName.replaceAll(":", "_");

        File file = new File(path, fileName); // e_ for encode
        if (file.exists()) {
            new ToastMsg(ctx).toastIconError("File already exist.");
            return;
        }
        String dir = ctx.getExternalCacheDir().toString();
        Data data = new Data.Builder()
                .putString("url", streamURL)
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


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, resolution, size;
        public LinearLayout itemLayout;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            resolution = v.findViewById(R.id.resolution_tv);
            size = v.findViewById(R.id.size_tv);
            itemLayout=v.findViewById(R.id.item_layout);
        }
    }

}