package com.burmesesubtitles.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.burmesesubtitles.app.ItemMovieActivity;
import com.burmesesubtitles.app.R;
import com.burmesesubtitles.app.models.CommonModels;
import com.burmesesubtitles.app.utils.ItemAnimation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private Context context;
    private List<CommonModels> commonModels;
    private String type;
    private String layout;
    private int c;

    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;

    public GenreAdapter(Context context, List<CommonModels> commonModels, String type, String layout) {
        this.context = context;
        this.commonModels = commonModels;
        this.type = type;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (layout.equals("home")) {
            v = LayoutInflater.from(context).inflate(R.layout.layout_genre_item, parent,
                    false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.layout_genre_item_2, parent,
                    false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CommonModels commonModel = commonModels.get(position);
        if (commonModel != null) {
            holder.cardView.requestFocus();
            holder.nameTv.setText(commonModel.getTitle());
            Picasso.get()
                    .load(commonModel.getImageUrl())
                    .centerCrop()
                    .fit()
                    .placeholder(R.drawable.poster_placeholder)
                    .into(holder.icon);
            holder.cardView.setBackgroundResource(getColor());

            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ItemMovieActivity.class);
                    intent.putExtra("id",commonModel.getId());
                    intent.putExtra("title",commonModel.getTitle());
                    intent.putExtra("type",type);
                    context.startActivity(intent);

                }
            });

        }

        setAnimation(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return commonModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        ImageView icon;
        CardView cardView;
        LinearLayout itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.genre_name_tv);
            icon = itemView.findViewById(R.id.icon);
            cardView = itemView.findViewById(R.id.card_view);
            itemLayout = itemView.findViewById(R.id.item_layout);

        }
    }

    private int getColor(){

        int colorList[] = {R.color.red_400,R.color.blue_400,R.color.indigo_400,R.color.orange_400,R.color.light_green_400,R.color.blue_grey_400};
        int colorList2[] = {R.drawable.gradient_1 ,R.drawable.gradient_2,R.drawable.gradient_3,R.drawable.gradient_4,R.drawable.gradient_5,R.drawable.gradient_6};

        if (c >= 6){
            c = 0;
        }

        int color = colorList2[c];
        c++;

        return color;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }

        });



        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}
