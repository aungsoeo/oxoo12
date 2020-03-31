package com.burmesesubtitles.app.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.burmesesubtitles.app.R;
import com.burmesesubtitles.app.models.Work;

import java.util.ArrayList;
import java.util.List;

public class FileDownloadAdapter extends RecyclerView.Adapter<FileDownloadAdapter.ViewHolder> {

    private final boolean isDark;
    private List<Work> works;
    private Context context;
    private ArrayList<ViewHolder> viewHolders = new ArrayList<>();

    private OnProgressUpdateListener progressUpdateListener;

    public FileDownloadAdapter(List<Work> works, Context context, boolean isDark) {
        this.works = works;
        this.context = context;
        this.isDark = isDark;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.layout_file_download_item, parent,
                false);

        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        viewHolders.add(holder);

        Work work = works.get(position);
        holder.fileNameTv.setText(work.getFileName());

        if (work.getDownloadStatus() != null) {
            if (work.getDownloadSize() != null && work.getTotalSize() != null) {
                double downloadedByte = Double.valueOf(work.getDownloadSize());
                double totalByte = Double.valueOf(work.getTotalSize());
                double totalKb = totalByte / 1024;
                double downloadKb = downloadedByte / 1024;

                double totalMb = totalKb / 1024;
                double downloadMb = downloadKb / 1024;

                holder.progressBar.setMax((int) totalKb);
                holder.progressBar.setProgress((int) downloadKb);

                holder.downloadAmountTv.setText(Double.parseDouble(String.format("%.1f", downloadMb)) + " MB / "
                        + Double.parseDouble(String.format("%.1f", totalMb)) + " MB");

                holder.downloadStatusTv.setText(work.getDownloadStatus());
                holder.startPauseIv.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.ic_play_circle_tranparent));

            }
        }

        if (progressUpdateListener != null) {
            progressUpdateListener.updateProgress(position, work, viewHolders);
        }

    }

    @Override
    public int getItemCount() {
        return works.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fileNameTv, downloadAmountTv, downloadStatusTv;
        public ImageView startPauseIv, closeIV;
        public ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileNameTv = itemView.findViewById(R.id.file_name_tv);
            startPauseIv = itemView.findViewById(R.id.play_pause_iv);
            closeIV = itemView.findViewById(R.id.close_iv);
            downloadAmountTv = itemView.findViewById(R.id.download_amount_tv);
            progressBar = itemView.findViewById(R.id.progressBarOne);
            downloadStatusTv = itemView.findViewById(R.id.download_status_tv);


            startPauseIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (progressUpdateListener != null) {
                        progressUpdateListener.onItemClick(getAdapterPosition(), works.get(getAdapterPosition()),
                                startPauseIv, viewHolders);
                    }
                }
            });

            closeIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (progressUpdateListener != null) {
                        progressUpdateListener.OnCancelClick(getAdapterPosition(), works.get(getAdapterPosition()),
                                startPauseIv, viewHolders);
                    }
                }
            });

        }
    }

    public interface OnProgressUpdateListener {
        void updateProgress(int adapterPos, Work work, List<ViewHolder> viewHolderList);

        void onItemClick(int position, Work work, ImageView startPauseIv, List<ViewHolder> viewHolderList);

        void OnCancelClick(int position, Work work, ImageView cancelIV, List<ViewHolder> viewHolderList);
    }


    public void setProgressUpdateListener(OnProgressUpdateListener progressUpdateListener) {
        this.progressUpdateListener = progressUpdateListener;
    }
}
