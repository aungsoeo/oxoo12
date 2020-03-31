package com.burmesesubtitles.app.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.burmesesubtitles.app.DetailsActivity;
import com.burmesesubtitles.app.R;
import com.burmesesubtitles.app.models.EpiModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.OriginalViewHolder> {

    private List<EpiModel> items = new ArrayList<>();
    private Context ctx;
    final EpisodeAdapter.OriginalViewHolder[] viewHolderArray = {null};
    private EpisodeAdapter.OnItemClickListener mOnItemClickListener;
    EpisodeAdapter.OriginalViewHolder viewHolder;
    int i=0;
    private int seasonNo;

    public interface OnItemClickListener {
        void onItemClick(View view, EpiModel obj, int position, OriginalViewHolder holder);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public EpisodeAdapter(Context context, List<EpiModel> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public EpisodeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EpisodeAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item, parent, false);
        vh = new EpisodeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final EpisodeAdapter.OriginalViewHolder holder, final int position) {

        final EpiModel obj = items.get(position);
        holder.name.setText(obj.getEpi());

        Picasso.get().load(obj.getImageUrl()).placeholder(R.drawable.poster_placeholder)
                .into(holder.episodIv);



        /*if (seasonNo == 0) {
            if (position==i){
                chanColor(viewHolderArray[0],position);
                ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                new DetailsActivity().iniMoviePlayer(obj.getStreamURL(),obj.getServerType(),ctx);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.playStatusTv.setText("Playing");
                holder.playStatusTv.setVisibility(View.VISIBLE);
                viewHolderArray[0] =holder;
                i = items.size()+items.size() + items.size();

            }
        }*/

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DetailsActivity)ctx).hideDescriptionLayout();
                ((DetailsActivity)ctx).showSeriesLayout();
                ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                boolean castSession = ((DetailsActivity)ctx).getCastSession();
                //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                if (!castSession) {
                    new DetailsActivity().iniMoviePlayer(obj.getStreamURL(),obj.getServerType(),ctx);
                } else {
                    ((DetailsActivity)ctx).showQueuePopup(ctx, holder.cardView, ((DetailsActivity)ctx).getMediaInfo());

                }

                chanColor(viewHolderArray[0],position);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.playStatusTv.setText("Playing");
                holder.playStatusTv.setVisibility(View.VISIBLE);


                viewHolderArray[0] =holder;
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, playStatusTv;
        public MaterialRippleLayout cardView;
        public ImageView episodIv;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            playStatusTv = v.findViewById(R.id.play_status_tv);
            cardView=v.findViewById(R.id.lyt_parent);
            episodIv=v.findViewById(R.id.image);
        }
    }

    private void chanColor(EpisodeAdapter.OriginalViewHolder holder, int pos){

        if (holder!=null){
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_20));
            holder.playStatusTv.setVisibility(View.GONE);
        }
    }


}