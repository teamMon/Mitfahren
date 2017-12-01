package com.example.jpar4.mitfahren.ItemView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.model.Item_ChattingRoomList;
import com.squareup.picasso.Picasso;

/**
 * Created by jpar4 on 2017-11-01.
 */

public class ItemChattingListView extends RelativeLayout implements View.OnClickListener {
    Myapp app;
    Context context;
    Activity mActivity;

    ImageView chatting_room_pic;
    TextView chatting_room_title;
    TextView chatting_room_num;
    TextView tv_unread_msg;// 안읽은 메시지 수

    Item_ChattingRoomList item_chattingRoomList;

    public ItemChattingListView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public ItemChattingListView(Context context, Activity mActivity) {
        super(context);
        this.context = context;
        this.mActivity = mActivity;
        init(context);
    }

    public ItemChattingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //인플래이션
        inflater.inflate(R.layout.item_chatting_roomlist, this, true);
        app = (Myapp)context.getApplicationContext();
        chatting_room_pic = (ImageView) findViewById(R.id.chatting_room_pic);
        chatting_room_title = (TextView) findViewById(R.id.chatting_room_title);
        chatting_room_num = (TextView) findViewById(R.id.chatting_room_num);
        tv_unread_msg =(TextView) findViewById(R.id.tv_unread_msg);



    }

    public void setItem_ChattingRoomList(Item_ChattingRoomList item){
        item_chattingRoomList = item;
        Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/"+item_chattingRoomList.getCar_pic()).into(chatting_room_pic);
        chatting_room_title.setText(item_chattingRoomList.getRoom_title());
        chatting_room_num.setText(item_chattingRoomList.getRoom_num() + "명");
        if(item_chattingRoomList.getNum_unread_msg().equals("0")){
            tv_unread_msg.setVisibility(GONE);
        }else{
            tv_unread_msg.setText(item_chattingRoomList.getNum_unread_msg());
            tv_unread_msg.setVisibility(VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_unread_msg:

                break;


        }
    }
}
