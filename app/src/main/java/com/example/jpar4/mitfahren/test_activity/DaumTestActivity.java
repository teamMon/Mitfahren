package com.example.jpar4.mitfahren.test_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.jpar4.mitfahren.R;

import net.daum.mf.map.api.MapView;


public class DaumTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_test);

        MapView mapView = new MapView(DaumTestActivity.this);
        //mapView.setDaumMapApiKey("e09aa983c8c50407bad77ec155c67cb4");


        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
    }
}
