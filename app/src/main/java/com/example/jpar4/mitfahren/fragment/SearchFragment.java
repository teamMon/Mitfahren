package com.example.jpar4.mitfahren.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jpar4.mitfahren.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private RecyclerView searchList;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_search, container, false);
        searchList = (RecyclerView) view.findViewById(R.id.driver_search_list);
        searchList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

}
