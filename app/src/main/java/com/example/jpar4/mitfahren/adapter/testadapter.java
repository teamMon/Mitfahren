package com.example.jpar4.mitfahren.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.jpar4.mitfahren.R;

/**
 * Created by jpar4 on 2017-08-07.
 */

public class testadapter extends BaseAdapter {
    int pos;
    Context context;

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        pos = position;
        context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.test_listview_item_search_option, parent, false);
        }


        return null;
    }
}
