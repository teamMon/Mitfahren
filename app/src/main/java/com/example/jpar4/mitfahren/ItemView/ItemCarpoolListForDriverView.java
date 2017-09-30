package com.example.jpar4.mitfahren.ItemView;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.model.Item_New_Driver_Info;
import com.squareup.picasso.Picasso;

/**
 * Created by jpar4 on 2017-09-19.
 */

public class ItemCarpoolListForDriverView extends LinearLayout implements View.OnClickListener{

    //singer_item.xml의 짝으로 만들어진 소스코드 그렇기 때문에 xml 레이아웃의 최상위 레이아웃인 LinearLayout의 클래스를 상속받아야한다.
    Context context;

    ImageView iv_item_carpool_info_main, iv_item_carpool_info_status;
    TextView tv_item_carpool_info_start;
    TextView tv_item_carpool_info_arrive, tv_item_carpool_info_current_people, tv_item_carpool_info_date;
    Button btn_carpool_cancel, btn_carpool_recruit_complete, btn_item_carpool_info, tv_item_carpool_info_status;
    Item_New_Driver_Info item_new_driver_info;
    FrameLayout item_driver_info_top;

    public ItemCarpoolListForDriverView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public ItemCarpoolListForDriverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //인플래이션
        inflater.inflate(R.layout.item_carpool_list_for_driver, this, true);
        tv_item_carpool_info_start = (TextView) findViewById(R.id.tv_item_carpool_info_start);
        tv_item_carpool_info_arrive = (TextView) findViewById(R.id.tv_item_carpool_info_arrive);
        btn_carpool_cancel = (Button) findViewById(R.id.btn_carpool_cancel);
        btn_carpool_cancel.setOnClickListener(this);
        btn_carpool_recruit_complete = (Button) findViewById(R.id.btn_carpool_recruit_complete);
        btn_carpool_recruit_complete.setOnClickListener(this);
        btn_item_carpool_info = (Button) findViewById(R.id.btn_item_carpool_info);
        btn_item_carpool_info.setOnClickListener(this);
        tv_item_carpool_info_status = (Button) findViewById(R.id.tv_item_carpool_info_status);
        iv_item_carpool_info_main = (ImageView) findViewById(R.id.iv_item_carpool_info_main);
        iv_item_carpool_info_status = (ImageView) findViewById(R.id.iv_item_carpool_info_status);
        tv_item_carpool_info_current_people = (TextView) findViewById(R.id.tv_item_carpool_info_current_people);
        tv_item_carpool_info_date = (TextView) findViewById(R.id.tv_item_carpool_info_date);
        item_driver_info_top = (FrameLayout) findViewById(R.id.item_driver_info_top);
    }

    public void setStart(String start) {
        String[] arr_result = start.split(" ");

        tv_item_carpool_info_start.setText(arr_result[1]+" "+arr_result[2]+" "+arr_result[3]);
        tv_item_carpool_info_start.setSelected(true);

    }
    public void setArrive(String arrive) {
        String[] arr_result = arrive.split(" ");
        tv_item_carpool_info_arrive.setText(arr_result[1]+" "+arr_result[2]+" "+arr_result[3]);
        tv_item_carpool_info_arrive.setSelected(true);
    }

    public void setItem_new_driver_info(Item_New_Driver_Info item){

        item_new_driver_info=item;
        //iv_driver_info_main
       Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/"+item_new_driver_info.getUser_car_photo()).into(iv_item_carpool_info_main);
        tv_item_carpool_info_current_people.setText("인원 : " + item_new_driver_info.getUser_having_rider()+ " / " +item_new_driver_info.getUser_with_poeple());
        //TEST CODE
        tv_item_carpool_info_date.setText(item_new_driver_info.getUser_start_date());
        if(item_new_driver_info.getUser_carpool_complete().equals("모집중")){
            item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_searching));
            iv_item_carpool_info_status.setImageResource(R.drawable.usersearch64);
            tv_item_carpool_info_status.setBackgroundResource(R.drawable.search_rounded_corners_bg);
            tv_item_carpool_info_status.setText(item_new_driver_info.getUser_carpool_complete());
        }else if(item_new_driver_info.getUser_carpool_complete().equals("모집완료됨")){
            item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_searchdone));
            iv_item_carpool_info_status.setImageResource(R.drawable.searchdone64);
            tv_item_carpool_info_status.setBackgroundResource(R.drawable.searchdone_rounded_corners_bg);
            tv_item_carpool_info_status.setText(item_new_driver_info.getUser_carpool_complete());
        }else if(item_new_driver_info.getUser_carpool_complete().equals("카풀취소됨")){
            item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_cancel));
            iv_item_carpool_info_status.setImageResource(R.drawable.cancelbuttonx64);
            tv_item_carpool_info_status.setBackgroundResource(R.drawable.cancel_rounded_corners_bg);
            tv_item_carpool_info_status.setText(item_new_driver_info.getUser_carpool_complete());
        }else if(item_new_driver_info.getUser_carpool_complete().equals("카풀완료됨")){
            item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_complete));
            iv_item_carpool_info_status.setImageResource(R.drawable.checked64);
            tv_item_carpool_info_status.setBackgroundResource(R.drawable.complete_rounded_corners_bg);
            tv_item_carpool_info_status.setText(item_new_driver_info.getUser_carpool_complete());
        }
    }



    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_carpool_cancel: //카풀 취소
               /* intent = new Intent(TestCarpoolItemView.this, NewUserPageActivity.class);
                startActivity(intent);
*/
                break;
            case R.id.btn_carpool_recruit_complete: // 카풀 완료 버튼
      /*          intent = new Intent(context,  NewDriverInfoActivity.class);
                intent.putExtra("data", item_new_driver_info);
                Log.e("ddd driver_info", item_new_driver_info.getUser_email().toString());
                Log.e("ddd driver_info", item_new_driver_info.getUser_start_date().toString());
                context.startActivity(intent);*/
                break;
            case R.id.btn_item_carpool_info: // 카풀 정보보기
                break;
        }
    }
}

