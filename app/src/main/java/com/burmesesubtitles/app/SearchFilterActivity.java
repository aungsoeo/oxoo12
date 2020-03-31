package com.burmesesubtitles.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jem.rubberpicker.RubberRangePicker;
import com.burmesesubtitles.app.adapters.SearchGenreAdapter;

import org.jetbrains.annotations.NotNull;

public class SearchFilterActivity extends AppCompatActivity implements SearchGenreAdapter.OnItemClickListener {

    private Toolbar mToolbar;
    private String genreName;



    private RubberRangePicker rangeView;
    private TextView minTv, maxTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        initViews();

        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        rangeView.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(@NotNull RubberRangePicker rubberRangePicker, int i, int i1, boolean b) {
                minTv.setText(i+"");
                maxTv.setText(i1+"");
            }

            @Override
            public void onStartTrackingTouch(@NotNull RubberRangePicker rubberRangePicker, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull RubberRangePicker rubberRangePicker, boolean b) {

            }
        });


    }

    private void initViews() {

        mToolbar = findViewById(R.id.toolbar);
        rangeView = findViewById(R.id.rangeSeekbar);
        minTv = findViewById(R.id.min_tv);
        maxTv = findViewById(R.id.max_tv);

    }

    public void btToggleClick(View view) {
        if (view instanceof Button) {
            Button b = (Button) view;
            if (b.isSelected()) {
                b.setTextColor(getResources().getColor(R.color.grey_40));
            } else {
                b.setTextColor(Color.WHITE);
            }
            b.setSelected(!b.isSelected());
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_filter_menu, menu);


        return true;

    }

    @Override
    public void onItemClick(String genreName , String type) {
        this.genreName = genreName;

        //Toast.makeText(this, "Name: "+ genreName +" type:"+ type, Toast.LENGTH_SHORT).show();

    }
}
