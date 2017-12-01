package com.example.jpar4.mitfahren.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.jpar4.mitfahren.ItemView.ItemChattingView;
import com.example.jpar4.mitfahren.model.Item_Chatting;

import java.util.ArrayList;

/**
 * Created by jpar4 on 2017-11-22.
 */

/*------------------------------------------------------------------채팅창 어댑터-------------*/
public class ChattingAdapter extends BaseAdapter{
    ArrayList<Item_Chatting> items = new ArrayList<>(); // 아이템 보관하기 위한 소스
    Context context;
    Activity mActivity;
    ChattingAdapter mChattingAdapter;


    ChattingAdapter(){

    }
    public  ChattingAdapter(Context context, Activity mActivity){
        this.context = context;
        this.mActivity = mActivity;
        this.mChattingAdapter = this;
    }

    public void clearItems(){
        items.clear();

    }

    public void deleteItem(int position){
        items.remove(position);
    }
    @Override
    public int getCount() { // 리스트뷰가 어댑터한테 리스트 몇 개가지고 있냐고 물어보면 실행하는 메소드
        return items.size();
    }

    public void addItem(Item_Chatting item) { // 데이터를 추가해주는 메서드
        items.add(item);
        //  notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) { // position == index
        return items.get(position); // 실제값을 넘겨줌
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //View convertView 리스트가 많아지면 메모리가 많이 먹음, 그래서 재사용을 함으로써 재사용을 위한 인자
        final int mPosition = position;
        ItemChattingView view = null;
        if (convertView == null) { // 뷰가 없으면 생성
            view = new ItemChattingView(context, mActivity, mChattingAdapter, position);
        } else {// 재사용
            view = (ItemChattingView) convertView;
        }

        Item_Chatting curItem = items.get(position);// 현재 아이템
        view.setItem_Chatting(curItem, position);
        //view.setTag(position);
         /*   view.setItem_Invitable(curItem);
            if(isThereCheckBox){// true면 checkBox보이고 아니면 안보이게
                view.invitable_user_check.setVisibility(View.VISIBLE);
            }else{
                view.invitable_user_check.setVisibility(View.GONE);
            }*/


        return view;
    }

    public void updatelist(){
        this.notifyDataSetChanged();
    }

}
    /*------------------------------------------------------------------채팅창 어댑터-------------*/