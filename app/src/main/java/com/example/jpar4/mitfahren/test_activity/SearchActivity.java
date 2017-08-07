package com.example.jpar4.mitfahren.test_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;


import com.example.jpar4.mitfahren.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    ExpandableListView driverSearchOption;

    /*driverSearchOptionGroup 상위 리스트 제목 검색설정, 검색조건*/
    private ArrayList<String> driverSearchOptionGroup = new ArrayList<>();
    /*하위녀석들*/
    private HashMap<String, ArrayList<String>> arrayChild = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_search);
        driverSearchOption = (ExpandableListView) this.findViewById(R.id.driver_search_option);
        driverSearchOptionGroup.add("검색설정");
        driverSearchOptionGroup.add("검색조건");






    }
}
