package com.example.jpar4.mitfahren.ItemView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.model.Item_Invitable;
import com.squareup.picasso.Picasso;

/**
 * Created by jpar4 on 2017-11-01.
 */

public class ItemChatterView extends LinearLayout implements View.OnClickListener {
    Myapp app;
    Context context;
    Activity mActivity;

    ImageView invitable_user_pic;
    TextView invitable_user_name;
    public CheckBox invitable_user_check;

    Item_Invitable item_invitable;

    public ItemChatterView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public ItemChatterView(Context context, Activity mActivity) {
        super(context);
        this.context = context;
        this.mActivity = mActivity;
        init(context);
    }

    public ItemChatterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //인플래이션
        inflater.inflate(R.layout.item_chatting_invitable, this, true);
        app = (Myapp)context.getApplicationContext();
        invitable_user_pic = (ImageView) findViewById(R.id.invitable_user_pic);
        invitable_user_name = (TextView) findViewById(R.id.invitable_user_name);
        invitable_user_check = (CheckBox) findViewById(R.id.invitable_user_check);
        invitable_user_check.setOnClickListener(this);


    }

    public void setItem_Invitable(Item_Invitable item){
        item_invitable = item;
        Picasso.with(context).load("http://ec2-13-124-251-123.ap-northeast-2.compute.amazonaws.com/upload/"+item_invitable.getUser_pic()).into(invitable_user_pic);
        invitable_user_name.setText(item_invitable.getUser_name());
        invitable_user_check.setVisibility(GONE); // 무조건 안보이게

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invitable_user_check:

                break;

        }
    }
}
