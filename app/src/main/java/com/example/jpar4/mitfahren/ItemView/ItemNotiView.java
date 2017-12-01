package com.example.jpar4.mitfahren.ItemView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.model.Item_Notification;
import com.example.jpar4.mitfahren.service.MyService;
import com.squareup.picasso.Picasso;

/**
 * Created by jpar4 on 2017-09-19.
 */

public class ItemNotiView extends LinearLayout implements View.OnClickListener{

    //singer_item.xml의 짝으로 만들어진 소스코드 그렇기 때문에 xml 레이아웃의 최상위 레이아웃인 LinearLayout의 클래스를 상속받아야한다.
    Myapp app;
    Context context;
    MyService service;

    ImageView iv_item_noti_main, iv_item_noti_profile_pic;
    TextView tv_item_noti_title, tv_item_noti_date;
    TextView tv_item_noti_content;
    Item_Notification item_notification;


    public ItemNotiView(Context context) {
        super(context);
        app = (Myapp)getContext();
        this.context = context;
        init(context);
    }

    public ItemNotiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //인플래이션
        inflater.inflate(R.layout.item_notification_list, this, true);
        tv_item_noti_title = (TextView) findViewById(R.id.tv_item_noti_title);
        tv_item_noti_date = (TextView) findViewById(R.id.tv_item_noti_date);
        tv_item_noti_content = (TextView) findViewById(R.id.tv_item_noti_content);
        iv_item_noti_main = (ImageView) findViewById(R.id.iv_item_noti_main);
        iv_item_noti_profile_pic = (ImageView) findViewById(R.id.iv_item_noti_profile_pic);

    }


    public void setItem_notitication(Item_Notification item){

        item_notification=item;
        tv_item_noti_title.setText(item.getNoti_title());
        tv_item_noti_content.setText(item.getNoti_content());
        tv_item_noti_date.setText(item.getNoti_date());
        Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/"+item.getNoti_profile_pic()).into(iv_item_noti_profile_pic);
        if(item.getNoti_checked().equals("Y")){
            iv_item_noti_main.setBackgroundColor(Color.WHITE);
        }
        else{
            iv_item_noti_main.setBackgroundResource(R.drawable.carpool_before_item_click_bg);
        }


        //iv_driver_info_main
    //   Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/"+item_new_driver_info.getUser_car_photo()).into(iv_driver_info_main);
/*        tv_driver_list_people.setText("인원 : " + item_new_driver_info.getUser_having_rider()+ " / " +item_new_driver_info.getUser_with_poeple());
        //tv_driver_list_people.setText(item_new_driver_info.getUser_with_poeple()+"명");
        tv_item_driver_list_date.setText(item_new_driver_info.getUser_start_date());
        tv_distanceinM.setText(item_new_driver_info.getDistance_option()+item_new_driver_info.getDistance_meters()+"M");*/
    }

    public void deliveryService(MyService service){
        this.service = service;
    }

    public void setItemBackgrouncColorWhite(){
        iv_item_noti_main.setBackgroundColor(Color.WHITE);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_driver_info_carpool_apply: //카풀신청

                break;
            case R.id.btn_driver_info_carpool_info: // 확인버튼을 누르면 다시 메인페이지로 이동할 수 있도록 함.

/*              intent = new Intent(context,  NewDriverInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를없앤다.
                intent.putExtra("data", item_new_driver_info);
                Log.e("ddd driver_info", item_new_driver_info.getUser_email().toString());
                Log.e("ddd driver_info", item_new_driver_info.getUser_start_date().toString());
                context.startActivity(intent);*/
                break;
        }
    }
}

