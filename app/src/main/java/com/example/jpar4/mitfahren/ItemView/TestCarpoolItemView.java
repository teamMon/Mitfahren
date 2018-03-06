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
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.model.Item_New_Driver_Info;
import com.example.jpar4.mitfahren.service.MyService;
import com.example.jpar4.mitfahren.tmap_test.NewDriverInfoActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by jpar4 on 2017-09-19.
 */

public class TestCarpoolItemView extends LinearLayout implements View.OnClickListener{

    //singer_item.xml의 짝으로 만들어진 소스코드 그렇기 때문에 xml 레이아웃의 최상위 레이아웃인 LinearLayout의 클래스를 상속받아야한다.
    Myapp app;
    Context context;
    MyService service;

    ImageView iv_driver_info_main;
    TextView tv_driver_list_start;
    TextView tv_driver_list_arrive, tv_driver_list_people, tv_item_driver_list_date;
    Button btn_driver_info_carpool_apply, btn_driver_info_carpool_info;
    Item_New_Driver_Info item_new_driver_info;
    TextView tv_distanceinM;

    public TestCarpoolItemView(Context context) {
        super(context);
        app = (Myapp)getContext();
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
        tv_item_driver_list_date = (TextView) findViewById(R.id.tv_item_driver_list_date);
        btn_driver_info_carpool_apply = (Button) findViewById(R.id.btn_driver_info_carpool_apply);
        btn_driver_info_carpool_apply.setOnClickListener(this);
        btn_driver_info_carpool_info = (Button) findViewById(R.id.btn_driver_info_carpool_info);
        btn_driver_info_carpool_info.setOnClickListener(this);
        iv_driver_info_main = (ImageView) findViewById(R.id.iv_driver_info_main);
        tv_driver_list_people = (TextView) findViewById(R.id.tv_driver_list_people);
                /*거리표시*/
        tv_distanceinM = (TextView)findViewById(R.id.tv_distanceinM);

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
       Picasso.with(context).load("http://ec2-13-124-251-123.ap-northeast-2.compute.amazonaws.com/upload/"+item_new_driver_info.getUser_car_photo()).into(iv_driver_info_main);
        tv_driver_list_people.setText("인원 : " + item_new_driver_info.getUser_having_rider()+ " / " +item_new_driver_info.getUser_with_poeple());
        //tv_driver_list_people.setText(item_new_driver_info.getUser_with_poeple()+"명");
        tv_item_driver_list_date.setText(item_new_driver_info.getUser_start_date());
        tv_distanceinM.setText(item_new_driver_info.getDistance_option()+item_new_driver_info.getDistance_meters()+"M");
    }

    public void deliveryService(MyService service){
        this.service = service;
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_driver_info_carpool_apply: //카풀신청
                Log.e("ddd ddd", item_new_driver_info.getDriver_info_id());
                Log.e("ddd ddd", item_new_driver_info.getRider_start_lat());

                if(app.isLoginOK()){ // 로그인 일때만 신청 메시지 보내기
                    JSONObject sendMsgObj = new JSONObject();
                    try{
                        /*
                        * 보내는 정보
                        * 1. 메시지 타입 2. 보내는 사람 이메일 3. 받는 사람 이메일 4. 등록된 카풀정보의 아이디(유일값) 5. 탑승자 출발 도착 좌표
                        *
                        *
                        * */
                        //핵심꼬임
                        sendMsgObj.put("kindOfmsg", "1"); //    kindOfmsg : 1 = carpool_applying
                        sendMsgObj.put("carpool_id", item_new_driver_info.getDriver_info_id()); // 등록된 카풀정보의 아이디(유일값)
                        sendMsgObj.put("sender", app.getUser_email()); // sender : 보내는 사람
                        sendMsgObj.put("receiver", item_new_driver_info.getUser_email()); // receiver : 받는 사람
                        sendMsgObj.put("rider_start_lat", item_new_driver_info.getRider_start_lat()); // 탑승자 출발 도착 좌표
                        sendMsgObj.put("rider_start_lng", item_new_driver_info.getRider_start_lng()); // 탑승자 출발 도착 좌표
                        sendMsgObj.put("rider_arrive_lat", item_new_driver_info.getRider_arrive_lat()); // 탑승자 출발 도착 좌표
                        sendMsgObj.put("rider_arrive_lng", item_new_driver_info.getRider_arrive_lng()); // 탑승자 출발 도착 좌표

                        service.sendMsg(sendMsgObj.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //Toast.makeText(context, app.getUser_email()+", "+item_new_driver_info.getUser_email(), Toast.LENGTH_SHORT).show();
                    //service.sendMsg("aaa"); // 보낼 메시지
                }else{
                    Toast.makeText(context, "로그인 후에 카풀신청이 가능합니다.", Toast.LENGTH_SHORT).show();
                }
       
               /* intent = new Intent(TestCarpoolItemView.this, NewUserPageActivity.class);
                startActivity(intent);
*/
                break;
            case R.id.btn_driver_info_carpool_info: // 확인버튼을 누르면 다시 메인페이지로 이동할 수 있도록 함.
                intent = new Intent(context,  NewDriverInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를없앤다.
                intent.putExtra("data", item_new_driver_info);
                Log.e("ddd driver_info", item_new_driver_info.getUser_email().toString());
                Log.e("ddd driver_info", item_new_driver_info.getUser_start_date().toString());
                context.startActivity(intent);
                break;
        }
    }
}

