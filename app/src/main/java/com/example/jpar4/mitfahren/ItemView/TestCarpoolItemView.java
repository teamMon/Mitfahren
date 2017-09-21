package com.example.jpar4.mitfahren.ItemView;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.model.Item_New_Driver_Info;
import com.example.jpar4.mitfahren.tmap_test.NewDriverInfoActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by jpar4 on 2017-09-19.
 */

public class TestCarpoolItemView extends LinearLayout implements View.OnClickListener{

    //singer_item.xml의 짝으로 만들어진 소스코드 그렇기 때문에 xml 레이아웃의 최상위 레이아웃인 LinearLayout의 클래스를 상속받아야한다.
    Context context;

    ImageView iv_driver_info_main;
    TextView tv_driver_list_start;
    TextView tv_driver_list_arrive, tv_driver_list_people;
    Button btn_driver_info_carpool_apply, btn_driver_info_carpool_info;
    Item_New_Driver_Info item_new_driver_info;

    public TestCarpoolItemView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public TestCarpoolItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //인플래이션
        inflater.inflate(R.layout.item_driver_list, this, true);
        tv_driver_list_start = (TextView) findViewById(R.id.tv_driver_list_start);
        tv_driver_list_arrive = (TextView) findViewById(R.id.tv_driver_list_arrive);
        btn_driver_info_carpool_apply = (Button) findViewById(R.id.btn_driver_info_carpool_apply);
        btn_driver_info_carpool_apply.setOnClickListener(this);
        btn_driver_info_carpool_info = (Button) findViewById(R.id.btn_driver_info_carpool_info);
        btn_driver_info_carpool_info.setOnClickListener(this);
        iv_driver_info_main = (ImageView) findViewById(R.id.iv_driver_info_main);
        tv_driver_list_people = (TextView) findViewById(R.id.tv_driver_list_people);
    }

    public void setStart(String start) {
        String[] arr_result = start.split(" ");

        tv_driver_list_start.setText(arr_result[1]+" "+arr_result[2]+" "+arr_result[3]);
        tv_driver_list_start.setSelected(true);

    }
    public void setArrive(String arrive) {
        String[] arr_result = arrive.split(" ");
        tv_driver_list_arrive.setText(arr_result[1]+" "+arr_result[2]+" "+arr_result[3]);
        tv_driver_list_arrive.setSelected(true);
    }

    public void setItem_new_driver_info(Item_New_Driver_Info item){

        item_new_driver_info=item;
        //iv_driver_info_main
       Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/"+item_new_driver_info.getUser_car_photo()).into(iv_driver_info_main);
        tv_driver_list_people.setText(item_new_driver_info.getUser_with_poeple()+"명");
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_driver_info_carpool_apply: //사용자 정보보기
               /* intent = new Intent(TestCarpoolItemView.this, NewUserPageActivity.class);
                startActivity(intent);
*/
                break;
            case R.id.btn_driver_info_carpool_info: // 확인버튼을 누르면 다시 메인페이지로 이동할 수 있도록 함.
                intent = new Intent(context,  NewDriverInfoActivity.class);
                intent.putExtra("data", item_new_driver_info);
                Log.e("ddd driver_info", item_new_driver_info.getUser_email().toString());
                Log.e("ddd driver_info", item_new_driver_info.getUser_start_date().toString());
                context.startActivity(intent);
                break;
        }
    }
}

