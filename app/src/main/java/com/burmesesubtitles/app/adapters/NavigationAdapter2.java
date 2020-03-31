package com.burmesesubtitles.app.adapters;


import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.burmesesubtitles.app.R;
import com.burmesesubtitles.app.models.NavigationModel;

import java.util.ArrayList;
import java.util.List;

public class NavigationAdapter2 extends RecyclerView.Adapter<NavigationAdapter2.OriginalViewHolder> {

    private List<NavigationModel> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    OriginalViewHolder viewHolder;

    public interface OnItemClickListener {
        void onItemClick(View view, NavigationModel obj, int position,OriginalViewHolder holder);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    public NavigationAdapter2(Context context, List<NavigationModel> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_nav_view_2, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final OriginalViewHolder holder, final int position) {

        NavigationModel obj = items.get(position);

        if (position==0){
            viewHolder=holder;
            holder.itemLayout.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
            holder.name.setTextColor(ctx.getResources().getColor(R.color.white));
        }
        holder.name.setText(obj.getTitle());
//        holder.image.setImageResource(obj.getImg());
        holder.image.setImageResource(getImageId(ctx,obj.getImg()));
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(position), position,holder);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;
        public LinearLayout itemLayout;

        public OriginalViewHolder(View v) {
            super(v);
            image =  v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            itemLayout=v.findViewById(R.id.card_view_home);
        }

    }

    public void chanColor(NavigationAdapter.OriginalViewHolder holder,int pos,int bgColor){

        if (pos!=0){
            viewHolder.itemLayout.setBackgroundColor(ctx.getResources().getColor(bgColor));
            viewHolder.name.setTextColor(ctx.getResources().getColor(R.color.grey_60));
        }

        if (holder!=null){
            holder.cardView.setCardBackgroundColor(ctx.getResources().getColor(bgColor));
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_60));
        }

    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}

