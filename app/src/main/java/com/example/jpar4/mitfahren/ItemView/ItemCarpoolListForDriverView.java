package com.example.jpar4.mitfahren.ItemView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.model.Item_New_Driver_Info;
import com.example.jpar4.mitfahren.tmap_test.ChattingRoomActivity;
import com.example.jpar4.mitfahren.tmap_test.NewDriverInfoActivity;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jpar4 on 2017-09-19.
 */

public class ItemCarpoolListForDriverView extends LinearLayout implements View.OnClickListener{
    private static final String TAG = "ItemCarpoolListForDriverView";
    //singer_item.xml의 짝으로 만들어진 소스코드 그렇기 때문에 xml 레이아웃의 최상위 레이아웃인 LinearLayout의 클래스를 상속받아야한다.
    Context context;
    Activity mActivity;
    ImageView iv_item_carpool_info_main, iv_item_carpool_info_status;
    TextView tv_item_carpool_info_start;
    TextView tv_item_carpool_info_arrive, tv_item_carpool_info_current_people, tv_item_carpool_info_date;
    Button btn_carpool_cancel, btn_carpool_recruit_complete, btn_item_carpool_info, tv_item_carpool_info_status, btn_chatting_driver;
    Item_New_Driver_Info item_new_driver_info;
    FrameLayout item_driver_info_top;

    public ItemCarpoolListForDriverView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public ItemCarpoolListForDriverView(Context context, Activity mActivity) {
        super(context);
        this.context = context;
        this.mActivity = mActivity;
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
        btn_chatting_driver = (Button) findViewById(R.id.btn_chatting_driver);//채팅버튼
        btn_chatting_driver.setOnClickListener(this);
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
       Picasso.with(context).load("http://ec2-13-124-251-123.ap-northeast-2.compute.amazonaws.com/upload/"+item_new_driver_info.getUser_car_photo()).into(iv_item_carpool_info_main);
        tv_item_carpool_info_current_people.setText("인원 : " + item_new_driver_info.getUser_having_rider()+ " / " +item_new_driver_info.getUser_with_poeple());
        //TEST CODE
        tv_item_carpool_info_date.setText(item_new_driver_info.getUser_start_date());
        if(item_new_driver_info.getUser_carpool_complete().equals("모집중")){
            item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_searching));
            iv_item_carpool_info_status.setImageResource(R.drawable.usersearch64);
            tv_item_carpool_info_status.setBackgroundResource(R.drawable.search_rounded_corners_bg);
            tv_item_carpool_info_status.setText(item_new_driver_info.getUser_carpool_complete());
            btn_carpool_cancel.setVisibility(VISIBLE);
            btn_chatting_driver.setVisibility(VISIBLE);
            btn_carpool_recruit_complete.setVisibility(VISIBLE);
        }else if(item_new_driver_info.getUser_carpool_complete().equals("모집완료됨")){
            item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_searchdone));
            iv_item_carpool_info_status.setImageResource(R.drawable.searchdone64);
            tv_item_carpool_info_status.setBackgroundResource(R.drawable.searchdone_rounded_corners_bg);
              /*버튼 숨김*/
            btn_carpool_cancel.setVisibility(VISIBLE);
            btn_carpool_recruit_complete.setVisibility(INVISIBLE);
            tv_item_carpool_info_status.setText(item_new_driver_info.getUser_carpool_complete());
            btn_chatting_driver.setVisibility(VISIBLE);
        }else if(item_new_driver_info.getUser_carpool_complete().equals("카풀취소됨")){
            item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_cancel));
            iv_item_carpool_info_status.setImageResource(R.drawable.cancelbuttonx64);
            tv_item_carpool_info_status.setBackgroundResource(R.drawable.cancel_rounded_corners_bg);
            tv_item_carpool_info_status.setText(item_new_driver_info.getUser_carpool_complete());
               /*버튼 숨김*/
            btn_carpool_cancel.setVisibility(INVISIBLE);
            btn_carpool_recruit_complete.setVisibility(INVISIBLE);
            btn_chatting_driver.setVisibility(INVISIBLE);
        }else if(item_new_driver_info.getUser_carpool_complete().equals("카풀완료됨")){
            item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_complete));
            iv_item_carpool_info_status.setImageResource(R.drawable.checked64);
            tv_item_carpool_info_status.setBackgroundResource(R.drawable.complete_rounded_corners_bg);
            tv_item_carpool_info_status.setText(item_new_driver_info.getUser_carpool_complete());
              /*버튼 숨김*/
            btn_carpool_cancel.setVisibility(INVISIBLE);
            btn_carpool_recruit_complete.setVisibility(INVISIBLE);
            btn_chatting_driver.setVisibility(INVISIBLE);
        }
    }



    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_carpool_cancel: //카풀 취소
               /* intent = new Intent(TestCarpoolItemView.this, NewUserPageActivity.class);
                startActivity(intent);*/
               DialogCancel();
                break;
            case R.id.btn_carpool_recruit_complete: // 카풀 완료 버튼
                DialogCheck();

                break;
            case R.id.btn_item_carpool_info: // 카풀 정보보기
                intent = new Intent(context,  NewDriverInfoActivity.class);
                //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를없앤다.
                intent.putExtra("carpool_ID", item_new_driver_info.getCarpool_id());
                intent.putExtra("INETENT_SENT_FROM", "ItemCarpoolListForDriverView");
                intent.putExtra("driver_email", item_new_driver_info.getDriver_email());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 누가에서는 괜찮은데 구형폰에서 안되서 추가함
                context.startActivity(intent);
                break;
            case R.id.btn_chatting_driver: // 채팅버튼 누르면 방생성 (chatting_room 테이블에 정보 insert)( id, carpool_id, poeple_num, master_email, car_pic)
               Log.e("ddd", item_new_driver_info.getCarpool_id()+item_new_driver_info.getUser_car_photo()+item_new_driver_info.getUser_email());
                InsertChattingRoom task = new InsertChattingRoom();
                // 카풀 넘버랑 마스터이메일( 카풀등록자 이메일) , 등록된 카풀 자동차 사진 넘겨줌.
                task.execute(item_new_driver_info.getCarpool_id(), item_new_driver_info.getUser_email(), item_new_driver_info.getUser_car_photo());
                intent = new Intent(context, ChattingRoomActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 누가에서는 괜찮은데 구형폰에서 안되서 추가함
                intent.putExtra("carpool_ID", item_new_driver_info.getCarpool_id());
                context.startActivity(intent);
                break;
        }
    }
   /*------------------------------------------------------------------------------인원 마감 ------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private void DialogCheck(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mActivity);
        alt_bld.setMessage("인원 마감 하면 탑승자를 변경할 수 없습니다. \n인원 마감 하시겠습니까?").setCancelable(
                false).setNegativeButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PushButton task = new PushButton();
                        task.execute(item_new_driver_info.getCarpool_id(),"","recruit_done");
                        /*위에 카풀 완료 버튼에 넣으면 바로 바뀜*/
                        item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_searchdone));
                        iv_item_carpool_info_status.setImageResource(R.drawable.searchdone64);
                        tv_item_carpool_info_status.setBackgroundResource(R.drawable.searchdone_rounded_corners_bg);
                        tv_item_carpool_info_status.setText("모집완료됨");
                        /*버튼 숨김*/
                        btn_carpool_cancel.setVisibility(INVISIBLE);
                        btn_carpool_recruit_complete.setVisibility(INVISIBLE);
                    }
                }).setPositiveButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
    /*------------------------------------------------------------------------------카풀 취소 ------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private void DialogCancel(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mActivity);
        alt_bld.setMessage("카풀을 취소 하시겠습니까?").setCancelable(
                false).setNegativeButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PushButton task = new PushButton();
                        task.execute(item_new_driver_info.getCarpool_id(),"","carpool_cancel");
                        /*위에 카풀 완료 버튼에 넣으면 바로 바뀜*/
                        item_driver_info_top.setBackgroundColor(getResources().getColor(R.color.carpool_cancel));
                        iv_item_carpool_info_status.setImageResource(R.drawable.cancelbuttonx64);
                        tv_item_carpool_info_status.setBackgroundResource(R.drawable.cancel_rounded_corners_bg);
                        tv_item_carpool_info_status.setText("카풀취소됨");
                        /*버튼 숨김*/
                        btn_carpool_cancel.setVisibility(INVISIBLE);
                        btn_carpool_recruit_complete.setVisibility(INVISIBLE);
                    }
                }).setPositiveButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
    /*-----------------------------------------------------------------------수락버튼 눌렀을때-----------------------------------------------------------------------*/
    class PushButton extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        /*--------------------------------------------------------왜 안되는지 모르겠는데 프로그래스이거 안되서 없애버림-------------------------------------------------------------------*/
/*        progressDialog = ProgressDialog.show(mContext,
                "잠시만 기다려 주세요.", null, true, true);*/
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
/*        progressDialog.dismiss();*/
            Log.e("ddd", result);

        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String carpool_id = (String) params[0];
            String sender_email = (String) params[1];
            String accept_cancel = (String) params[2];

            Log.e("ddd", carpool_id+sender_email+accept_cancel);
            String serverURL = "http://ec2-13-124-251-123.ap-northeast-2.compute.amazonaws.com/db/update_driver_info.php";
            String postParameters = "carpool_id="+carpool_id+"&sender_email="+sender_email+"&accept_cancel="+accept_cancel;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
               // Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                //Log.d(TAG, "EmailCheck: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
    /*-----------------------------------------------------------------------수락버튼 눌렀을때-----------------------------------------------------------------------*/
      /*-----------------------------------------------------------------------채팅버튼 눌렀을 -----------------------------------------------------------------------*/
    class InsertChattingRoom extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        /*--------------------------------------------------------왜 안되는지 모르겠는데 프로그래스이거 안되서 없애버림-------------------------------------------------------------------*/
/*        progressDialog = ProgressDialog.show(mContext,
                "잠시만 기다려 주세요.", null, true, true);*/
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
/*        progressDialog.dismiss();*/
            Log.e("ddd", result);

        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String carpool_id = (String) params[0];
            String master_email = (String) params[1];
            String car_pic = (String) params[2];

            Log.e("ddd", carpool_id+master_email+car_pic);
            String serverURL = "http://ec2-13-124-251-123.ap-northeast-2.compute.amazonaws.com/db/chatting_room_insert.php";
            String postParameters = "carpool_id="+carpool_id+"&master_email="+master_email+"&car_pic="+car_pic;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                // Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                //Log.d(TAG, "EmailCheck: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
    /*-----------------------------------------------------------------------채팅버튼 눌렀을 때-----------------------------------------------------------------------*/
}

