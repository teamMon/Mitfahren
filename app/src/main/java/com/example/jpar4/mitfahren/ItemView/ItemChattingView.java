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
import com.example.jpar4.mitfahren.model.Item_Chatting;
import com.squareup.picasso.Picasso;

import static com.example.jpar4.mitfahren.R.id.invitable_user_check;

/**
 * Created by jpar4 on 2017-11-02.
 */

public class ItemChattingView extends RelativeLayout implements View.OnClickListener{

    Myapp app;
    Context context;
    Activity mActivity;

    ImageView msg_sender_pic;
    TextView msg_sender_name, msg_received_content, msg_received_time, msg_sent_content, msg_sent_time;

    Item_Chatting item_chatting;

    public ItemChattingView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public ItemChattingView(Context context, Activity mActivity) {
        super(context);
        this.context = context;
        this.mActivity = mActivity;
        init(context);
    }

    public ItemChattingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //인플래이션
        inflater.inflate(R.layout.item_chatting_msg, this, true);
        app = (Myapp) context.getApplicationContext();
        msg_sender_pic = (ImageView) findViewById(R.id.msg_sender_pic);
        msg_sender_name = (TextView) findViewById(R.id.msg_sender_name);
        msg_received_content = (TextView) findViewById(R.id.msg_received_content);
        msg_received_time = (TextView) findViewById(R.id.msg_received_time);
        msg_sent_content = (TextView) findViewById(R.id.msg_sent_content);
        msg_sent_time = (TextView) findViewById(R.id.msg_sent_time);


    }

    public void setItem_Chatting(Item_Chatting item) {
        item_chatting = item;
       // Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + Item_Chatting.getUser_pic()).into(invitable_user_pic);
        if(item_chatting.isSent()){
            msg_sent_content.setVisibility(VISIBLE);
            msg_sent_time.setVisibility(VISIBLE);
            msg_sender_pic.setVisibility(GONE);
            msg_sender_name.setVisibility(GONE);
            msg_received_content.setVisibility(GONE);
            msg_received_time.setVisibility(GONE);
            msg_sent_content.setText(item_chatting.getSent_content());
            msg_sent_time.setText(item_chatting.getSent_time());

        }else{
            msg_sender_pic.setVisibility(VISIBLE);
            msg_sender_name.setVisibility(VISIBLE);
            msg_received_content.setVisibility(VISIBLE);
            msg_received_time.setVisibility(VISIBLE);
            msg_sent_content.setVisibility(GONE);
            msg_sent_time.setVisibility(GONE);

            msg_sender_name.setText(item_chatting.getSender_name());
            msg_received_content.setText(item_chatting.getReceived_content());
            msg_received_time.setText(item_chatting.getReceived_time());
            Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getSender_pic()).into(msg_sender_pic);

        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case invitable_user_check:
                // Toast.makeText(app, " "+item_invitable.isItem_checked(), Toast.LENGTH_SHORT).show();
                //Item_Chatting.setItem_checked(!Item_Chatting.isItem_checked());
                break;

        }
    }
}
