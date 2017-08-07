package com.example.jpar4.mitfahren.test_activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.jpar4.mitfahren.R;

import java.util.ArrayList;

public class Search2Activity extends AppCompatActivity {
    ArrayList<ItemTestList> arDessert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_search2);

        // MyDessert 클래스 형태의 데이터 준비
        arDessert = new ArrayList<ItemTestList>();
        ItemTestList mydessert;
        mydessert = new ItemTestList("마카롱 아이스크림");
        arDessert.add(mydessert);

        MyDessertAdapter2 adapter = new MyDessertAdapter2(this, R.layout.test_listview_item_search_option, arDessert);

        ListView list;
        list = (ListView)findViewById(R.id.driver_search_option);
        list.setAdapter(adapter);




    }
}
// 리스트뷰에 출력할 항목 클래스
class ItemTestList{

    int Icon;
    String Name;

    ItemTestList(String aName) {
        Name = aName;
    }

    ItemTestList(int aIcon, String aName) {
        Icon = aIcon;
        Name = aName;
    }
}

// 어댑터 클래스
class MyDessertAdapter2 extends BaseAdapter {

    Context con;
    LayoutInflater inflacter;
    ArrayList<ItemTestList> arD;
    int layout;

    public MyDessertAdapter2(Context context, int alayout, ArrayList<ItemTestList> aarD) {
        con = context;
        inflacter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arD = aarD;
        layout = alayout;
    }

    // 어댑터에 몇 개의 항목이 있는지 조사
    @Override
    public int getCount() {
        return arD.size();
    }

    // position 위치의 항목 Name 반환
    @Override
    public Object getItem(int position) {
        return arD.get(position).Name;
    }

    // position 위치의 항목 ID 반환
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 각 항목의 뷰 생성 후 반환
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflacter.inflate(layout, parent, false);
        }

        return convertView;
    }
}