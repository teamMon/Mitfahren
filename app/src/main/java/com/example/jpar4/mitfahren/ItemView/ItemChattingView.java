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

    ImageView img_received_content;// 받은 이미지
    TextView img_received_time;// 받은 이미지 시간
    ImageView img_sent_content;// 보낸 이미지
    TextView img_sent_time;// 보낸 이미지 시간

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

        img_received_content = (ImageView) findViewById(R.id.img_received_content);
        img_received_time = (TextView) findViewById(R.id.img_received_time);

        img_sent_content = (ImageView) findViewById(R.id.img_sent_content);
        img_sent_time = (TextView) findViewById(R.id.img_sent_time);

    }

    public void setItem_Chatting(Item_Chatting item) { //img_file_name: chatting_img_20171117/z014615.jpg
        item_chatting = item;
       // Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + Item_Chatting.getUser_pic()).into(invitable_user_pic);
        if(item_chatting.isSent()){ //보내는거
            if(item_chatting.getImg_file_name() != null){ // 보낸 이미지 주소가 있으면 = 이미지 전송
                msg_sent_content.setVisibility(GONE);
                msg_sent_time.setVisibility(GONE);
                msg_sender_pic.setVisibility(GONE);
                msg_sender_name.setVisibility(GONE);
                msg_received_content.setVisibility(GONE);
                msg_received_time.setVisibility(GONE);
                img_received_content.setVisibility(GONE);
                img_received_time.setVisibility(GONE);

                img_sent_content.setVisibility(VISIBLE); //보낸 이미지
                img_sent_time.setVisibility(VISIBLE); //보낸 이미지 시간

                Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getImg_file_name()).into(img_sent_content);
                img_sent_time.setText(item_chatting.getSent_time());

            }
            else{ // 메시지 전송
                img_sent_content.setVisibility(GONE);
                img_sent_time.setVisibility(GONE);
                msg_sent_content.setVisibility(VISIBLE); // 보낸 메시지
                msg_sent_time.setVisibility(VISIBLE); // 보낸 메시지 시간

                msg_sender_pic.setVisibility(GONE);
                msg_sender_name.setVisibility(GONE);
                msg_received_content.setVisibility(GONE);
                msg_received_time.setVisibility(GONE);
                img_received_content.setVisibility(GONE);
                img_received_time.setVisibility(GONE);

                msg_sent_content.setText(item_chatting.getSent_content());
                msg_sent_time.setText(item_chatting.getSent_time());
            }


        }else{ // 빋는거
            if(item_chatting.getImg_file_name() != null) { // 받은 이미지 주소가 있으면 = 이미지 받음
                img_received_content.setVisibility(VISIBLE); //받은 이미지
                img_received_time.setVisibility(VISIBLE); // 받은 이미지 시간
                msg_sender_pic.setVisibility(VISIBLE); // 보낸 사람 프로필
                msg_sender_name.setVisibility(VISIBLE); // 보낸 사람 이름
                msg_received_content.setVisibility(GONE);
                msg_received_time.setVisibility(GONE);

                msg_sent_content.setVisibility(GONE);
                msg_sent_time.setVisibility(GONE);
                img_sent_content.setVisibility(GONE);
                img_sent_time.setVisibility(GONE);

                Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getImg_file_name()).into(img_received_content);
                img_received_time.setText(item_chatting.getReceived_time());

                msg_sender_name.setText(item_chatting.getSender_name());
                Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getSender_pic()).into(msg_sender_pic);
            }
            else{// 메시지 받음
                img_received_content.setVisibility(GONE);
                img_received_time.setVisibility(GONE);
                msg_sender_pic.setVisibility(VISIBLE); // 보낸 사람 프로필
                msg_sender_name.setVisibility(VISIBLE); // 보낸 사람 이름
                msg_received_content.setVisibility(VISIBLE); // 받은 메시지
                msg_received_time.setVisibility(VISIBLE); // 받은 메시지 시간

                msg_sent_content.setVisibility(GONE);
                msg_sent_time.setVisibility(GONE);
                img_sent_content.setVisibility(GONE);
                img_sent_time.setVisibility(GONE);

                msg_sender_name.setText(item_chatting.getSender_name());
                msg_received_content.setText(item_chatting.getReceived_content());
                msg_received_time.setText(item_chatting.getReceived_time());
                Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getSender_pic()).into(msg_sender_pic);
            }
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
