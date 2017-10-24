package com.example.jpar4.mitfahren.tmap_test;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.ItemView.TestCarpoolItemView;
import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.func.StringDateCompare;
import com.example.jpar4.mitfahren.model.Item_New_Driver_Info;
import com.example.jpar4.mitfahren.service.MyService;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;



/*운전자 검색결과 화면*/
public class TestLIstView extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TestListView";
    ListView listView;
    TestAdapter adapter;
    Intent intent;
    String startArrivePoint="";

    /*시간 날짜*/
    EditText from_date;
    EditText to_date;
    int year_x, month_x, day_x, year_x1, month_x1, day_x1;
    static final int DIALOG_ID1 = 0;
    static final int DIALOG_ID2 = 1;
    String date1 = "";
    String date2 = "";
    /*시간 날짜*/
    /*범위 성별 정렬 스피너*/
    Spinner option_spinnner_distance, option_spinnner_sex, option_spinnner_howtoshow;
    LinearLayout search_option_layout;
    ImageButton btn_search_option;
    Button btn_search;
    EditText et_option_distance; // 검색옵션 거리
    TextView tv_no_output;//검색결과 없습니다.

    boolean BTN_UP_DOWN = true;
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    MyService mService; //서비스객체
    boolean mBound;
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/


    /*범위 성별 정렬 스피너*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_test_list_view);

        tv_no_output = (TextView)findViewById(R.id.tv_no_output);
        et_option_distance = (EditText)findViewById(R.id.et_option_distance);
        et_option_distance.setText("10000");

//
//        출발지 좌표 도착지 좌표 받아옴
        intent = getIntent();
        startArrivePoint = intent.getStringExtra("startArrivePoint");

        search_option_layout = (LinearLayout)findViewById(R.id.search_option_layout);
        btn_search_option = (ImageButton) findViewById(R.id.btn_search_option);
        btn_search_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BTN_UP_DOWN){
                    btn_search_option.setImageResource(R.drawable.uparrow);
                    search_option_layout.setVisibility(View.VISIBLE);
                    BTN_UP_DOWN=false;
                }else{
                    btn_search_option.setImageResource(R.drawable.downarrow);
                    search_option_layout.setVisibility(View.GONE);
                    BTN_UP_DOWN=true;
                }

            }
        });

        btn_search = (Button)findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);

        /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/
        from_date = (EditText) findViewById(R.id.from_date);
        to_date = (EditText) findViewById(R.id.to_date);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
        showDate();

    /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/
     /*------------------------------------------------------------------------------------------범위 성별 정렬 스피너------------------------------------------------------------------------------------------*/
        /*범위*/
        option_spinnner_distance = (Spinner)findViewById(R.id.option_spinnner_distance);
        ArrayAdapter distanceAdapter = ArrayAdapter.createFromResource(this, R.array.option_distance, android.R.layout.simple_spinner_item);
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        option_spinnner_distance.setAdapter(distanceAdapter);

        /*성별*/
        option_spinnner_sex = (Spinner)findViewById(R.id.option_spinnner_sex);
        ArrayAdapter sexAdapter = ArrayAdapter.createFromResource(this, R.array.option_sex, android.R.layout.simple_spinner_item);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        option_spinnner_sex.setAdapter(sexAdapter);

        /*정렬*/
        option_spinnner_howtoshow = (Spinner)findViewById(R.id.option_spinnner_howtoshow);
        ArrayAdapter howtoshowAdapter = ArrayAdapter.createFromResource(this, R.array.option_howtoshow, android.R.layout.simple_spinner_item);
        howtoshowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        option_spinnner_howtoshow.setAdapter(howtoshowAdapter);

/*------------------------------------------------------------------------------------------범위 성별 정렬 스피너------------------------------------------------------------------------------------------*/

        /*운전자 정보 가져옴*/
        GetDriverInfo2 task = new GetDriverInfo2();
        task.execute();

        listView = (ListView) findViewById(R.id.listView);
        adapter = new TestAdapter();

        listView.setAdapter(adapter); // 리스트에 어댑터를 추가

        /*오늘 날짜로 설정*/
        Calendar calendar = Calendar.getInstance();
        calendar.set(year_x, month_x, day_x);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = format.format(calendar.getTime());
        date1 = strDate;
        from_date.setText(strDate); // 오늘 날짜로 설정
        to_date.setText(strDate); // 오늘날짜로 설정
    }
    /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/

    public void showDate() {
        from_date.setFocusable(false);
        from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID1);
            }
        });

        to_date.setFocusable(false);
        to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID2);
            }
        });
    }
    /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/
    @Override
    protected Dialog onCreateDialog(int id) {
        DatePickerDialog datePickerDialog;
        Calendar calendar;
        Date newDate;
        switch (id) {
            case DIALOG_ID1://시작날짜
                //return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);

                datePickerDialog = new DatePickerDialog(this, PickerListener1, year_x, month_x, day_x);
                calendar = Calendar.getInstance();

                calendar.add(Calendar.DATE, 0); // Add 0 days to Calendar
                newDate = calendar.getTime();
                datePickerDialog.getDatePicker().setMinDate(newDate.getTime()-(newDate.getTime()%(24*60*60*1000)));
                return datePickerDialog;

            case DIALOG_ID2://끝날짜
//                앞에서 선택한 날짜가 있으면 뒤에 선택할 수 없도록 설정해줘야함 /////////////////////////////////////////////////////////////////////////////
                datePickerDialog = new DatePickerDialog(this, PickerListener2, year_x, month_x, day_x);
                calendar = Calendar.getInstance();

                calendar.add(Calendar.DATE, 0); // Add 0 days to Calendar
                newDate = calendar.getTime();
                datePickerDialog.getDatePicker().setMinDate(newDate.getTime()-(newDate.getTime()%(24*60*60*1000)));
                return datePickerDialog;

            default:
                return null;

        }
    }
    /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------------날짜------------------------------------------------------------------------------------------*/
    private DatePickerDialog.OnDateSetListener PickerListener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year_x, month_x, day_x);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = format.format(calendar.getTime());
            date1 = strDate;
            from_date.setText(strDate);
            //Toast.makeText(booking.this, year_x + "-" + month_x + "-" + day_x, Toast.LENGTH_SHORT).show();
        }
    };
    private DatePickerDialog.OnDateSetListener PickerListener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year_x, month_x, day_x);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = format.format(calendar.getTime());
            date2 = strDate;
            to_date.setText(strDate);
            //Toast.makeText(booking.this, year_x + "-" + month_x + "-" + day_x, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                /*버튼 사라지기*/
                btn_search_option.setImageResource(R.drawable.downarrow);
                search_option_layout.setVisibility(View.GONE);
                BTN_UP_DOWN=true;
                /*버튼 사라지기*/


                String option_date1 = from_date.getText().toString();
                String option_date2 = to_date.getText().toString();
                String option_distance, option_distance_meters, option_sex, option_howtoshow;

         /*       if(option_spinnner_distance.isSelected()){
                     a3 = option_spinnner_distance.getSelectedItem().toString();
                }else{
                     a3 = "거리없어";
                }*/

                if(et_option_distance==null){
                    option_distance_meters="입력한거리없음";
                }else{
                    option_distance_meters = et_option_distance.getText().toString();
                }

/*                if(option_spinnner_distance.isSelected()){
                    a5 = option_spinnner_sex.getSelectedItem().toString();
                }else{
                     a5= "성별없어";
                }

                if(option_spinnner_distance.isSelected()){
                     a6 = option_spinnner_howtoshow.getSelectedItem().toString();
                }else{
                     a6 = "정렬없어";
                }*/
                option_distance = option_spinnner_distance.getSelectedItem().toString();
                option_sex = option_spinnner_sex.getSelectedItem().toString();
                option_howtoshow = option_spinnner_howtoshow.getSelectedItem().toString();

                Toast.makeText(this, option_date1+option_date2+option_distance+option_distance_meters+option_sex+option_howtoshow, Toast.LENGTH_SHORT).show();

                /*순서대로 해야됨 아마도 ...*/
                adapter.applySearchOptionDate(option_date1, option_date2);
                adapter.applySearchOptionDistance(option_distance, option_distance_meters);
                adapter.applySexOption(option_sex);
                adapter.applyHowToShow(option_howtoshow);
                adapter.checkNoneOfItem();

                adapter.notifyDataSetChanged();
                break;
        }
    }


    class TestAdapter extends BaseAdapter {
            ArrayList<Item_New_Driver_Info> items = new ArrayList<>(); // 아이템 보관하기 위한 소스
            ArrayList<Item_New_Driver_Info> items2 = new ArrayList<>();


            @Override
            public int getCount() { // 리스트뷰가 어댑터한테 리스트 몇 개가지고 있냐고 물어보면 실행하는 메소드
                return items.size();
            }

            public void addItem(Item_New_Driver_Info item) { // 데이터를 추가해주는 메서드
                items.add(item);
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

                TestCarpoolItemView view = null;
                if (convertView == null) { // 뷰가 없으면 생성
                    view = new TestCarpoolItemView(getApplicationContext());
                } else {// 재사용
                    view = (TestCarpoolItemView) convertView;
                }

                Item_New_Driver_Info curItem = items.get(position);// 현재 아이템
                view.setStart(curItem.getUser_start());
                view.setArrive(curItem.getUser_arrive());
                view.setItem_new_driver_info(curItem);
                view.deliveryService(mService);
                return view;
            }
            boolean searchFirstOrNot=false; //첫 검색결과를 보존
            public void applySearchOptionDate(String from_date, String to_date){

                if(!searchFirstOrNot) {
                    items2.addAll(items); //검색후 처음에만 복사해놓고 나중에는 그냥 있는 거 씀.
                    searchFirstOrNot = true;
                }
                    items.clear();


                    StringDateCompare stringDateCompare = new StringDateCompare();
                    ArrayList<Item_New_Driver_Info> tempItems = new ArrayList<>();
                    for(int i =0; i<items2.size();i++){
                        /*-------------------------------------------날짜 옵션--------------------------------------------------------------------*/
                        Log.e("ddd 시작날짜",items2.get(i).getUser_start_date() + from_date + to_date);
                        int date_result = stringDateCompare.DateCompare(items2.get(i).getUser_start_date(), from_date);
                        if(date_result<0){
                            continue;
                        }

                        int date_result2 = stringDateCompare.DateCompare(items2.get(i).getUser_start_date(), to_date);
                        if(date_result2 != 404 && date_result2>0){
                            continue;
                        }

                        Log.e(" ddd" ,""+date_result+", "+date_result2);
                        /*-------------------------------------------날짜 옵션--------------------------------------------------------------------*/


                        tempItems.add(items2.get(i));

                    }
                    items.addAll(tempItems);
/*
                }else{
                    items.addAll(items2);
                    items2.clear();
                    temapp=false;
                }*/

            }
            public void applySearchOptionDistance(String option_name, String option_distance){
                ArrayList<Item_New_Driver_Info> tempItems = new ArrayList<>();
                tempItems.addAll(items);
                items.clear();


                for(int i =0; i<tempItems.size();i++){
                    if(!option_distance.equals("")){
                        if(Integer.parseInt(option_distance) < Integer.parseInt(tempItems.get(i).getDistance_meters())){ //거리가 옵션거리보다 크면
                            continue;
                        }
                    }
                    Log.e("ddd 거리", ""+tempItems.get(i).getDistance_meters()+", "+option_name+option_distance);
                                    /*---------------------------------------------------------------거리계산및 세팅부분--------------------------------------------------------------------------------------------------------------*/
                    //거리구하기 테스트
                    String[] arr_startArrivePoint = startArrivePoint.split("#");
                    Float distanceFromStart = getDistanceInMeter(arr_startArrivePoint[0],arr_startArrivePoint[1],tempItems.get(i).getUser_start_lat(),tempItems.get(i).getUser_start_lng());
                    Float distanceFromArrive = getDistanceInMeter(arr_startArrivePoint[2],arr_startArrivePoint[3], tempItems.get(i).getUser_arrive_lat(),tempItems.get(i).getUser_arrive_lng());
                    if(option_spinnner_distance.getSelectedItem().toString().equals("지정 출발지로부터 카풀 출발지까지")){
                        tempItems.get(i).setDistance_option("지정 출발지로부터 카풀 출발지까지 : ");
                        tempItems.get(i).setDistance_meters(""+distanceFromStart.intValue());
                    }else{
                        tempItems.get(i).setDistance_option("지정 도착지로부터 카풀 도착지까지 : ");
                        tempItems.get(i).setDistance_meters(""+distanceFromArrive.intValue());
                    }
/*---------------------------------------------------------------거리계산및 세팅부분--------------------------------------------------------------------------------------------------------------*/
                    //items.get(i).setDistance_option(option_name);
                    items.add(tempItems.get(i));
                }
            }
            public void applySexOption(String sex_option){
                Log.e("ddd 성별", sex_option);
                ArrayList<Item_New_Driver_Info> tempItems = new ArrayList<>();
                tempItems.addAll(items);
                items.clear();
                for(int i =0; i<tempItems.size();i++){
                    if(sex_option.equals("남자만")){
                        if(tempItems.get(i).getUser_sex().equals("남성")){
                            items.add(tempItems.get(i));
                        }
                    }else if(sex_option.equals("여자만")){
                        if(tempItems.get(i).getUser_sex().equals("여성")){
                            items.add(tempItems.get(i));
                        }
                    }else{
                        items.add(tempItems.get(i));
                    }
                }
            }

            public void applyHowToShow(String option_howtoshow) {
                Log.e("ddd 정렬", option_howtoshow);
                Comparator<Item_New_Driver_Info> meterASC = new Comparator<Item_New_Driver_Info>() {
                    @Override
                    public int compare(Item_New_Driver_Info item1, Item_New_Driver_Info item2) {
                        int ret = 0 ;

                        if (Integer.parseInt(item1.getDistance_meters()) < Integer.parseInt(item2.getDistance_meters()))
                            ret = -1 ;
                        else if (Integer.parseInt(item1.getDistance_meters()) == Integer.parseInt(item2.getDistance_meters()))
                            ret = 0 ;
                        else
                            ret = 1 ;

                        return ret ;
                        //-1이랑 1이랑 바꾸면 내림차순으로 바뀜
                    }
                };
                Comparator<Item_New_Driver_Info> dateASC = new Comparator<Item_New_Driver_Info>() {
                    @Override
                    public int compare(Item_New_Driver_Info item1, Item_New_Driver_Info item2) {

                        StringDateCompare dateCompare = new StringDateCompare();


                        return dateCompare.DateCompare(item1.getUser_start_date(),item2.getUser_start_date());

                    }
                };
                if(option_howtoshow.equals("빠른 날짜순")){
                    Collections.sort(items, dateASC) ;
                }else{//가까운 거리순
                    Collections.sort(items, meterASC) ;
                }
                adapter.notifyDataSetChanged() ;
            }

            public void checkNoneOfItem(){
                if(items.size()==0){
                    tv_no_output.setVisibility(View.VISIBLE);
                }else{
                    tv_no_output.setVisibility(View.GONE);
                }
            }
        }

    /*-----------------------------------------------------------------------디비에서 운전자 정보를 가져옮-----------------------------------------------------------------------*/

    class GetDriverInfo2 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TestLIstView.this,
                    "잠시만 기다려 주세요.", null, true, true);
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            /*result로 JsonArray를 받아와서 Json으로 하나씪 분리해서 값을 뽑아야됨*/
            try{
                //JSONObject obj = new JSONObject(result);
                JSONArray arr = new JSONArray(result);
                ArrayList<Item_New_Driver_Info> itemList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++)
                {



//                    Log.e("ddd", arr_startArrivePoint[0]+" "+arr_startArrivePoint[1]);
//                    Log.e("ddd", distanceFromStart.toString());
//                    Log.e("ddd", ""+distanceFromStart.intValue());
//                    Log.e("ddd", arr_startArrivePoint[2]+" "+arr_startArrivePoint[3]);
//                    Log.e("ddd", distanceFromArrive.toString());
//                    Log.e("ddd", ""+distanceFromArrive.intValue());

                    Item_New_Driver_Info item = new Item_New_Driver_Info();
                    item.setDriver_info_id(arr.getJSONObject(i).getString("id")); // 카풀정보
                    item.setCarpool_id(arr.getJSONObject(i).getString("id"));

                    item.setUser_email(arr.getJSONObject(i).getString("user_email"));
                    item.setUser_start_date(arr.getJSONObject(i).getString("user_start_date"));
                    item.setUser_start_time(arr.getJSONObject(i).getString("user_start_time"));
                    //명수 대신 삽입해서 확인해보기
                    //item.setUser_with_poeple(출발지로부터거리.toString());

                    item.setUser_with_poeple(arr.getJSONObject(i).getString("user_with_poeple"));
                    item.setUser_start_lat(arr.getJSONObject(i).getString("user_start_lat"));
                    item.setUser_start_lng(arr.getJSONObject(i).getString("user_start_lng"));
                    item.setUser_arrive_lat(arr.getJSONObject(i).getString("user_arrive_lat"));
                    item.setUser_arrive_lng(arr.getJSONObject(i).getString("user_arrive_lng"));
                    item.setUser_car_photo(arr.getJSONObject(i).getString("user_car_photo"));
                    item.setUser_having_rider(arr.getJSONObject(i).getString("user_having_rider"));
                    item.setUser_carpool_complete(arr.getJSONObject(i).getString("user_carpool_complete"));

                    item.setUser_start(getAddress(arr.getJSONObject(i).getString("user_start_lat"),arr.getJSONObject(i).getString("user_start_lng")));
                    item.setUser_arrive(getAddress(arr.getJSONObject(i).getString("user_arrive_lat"),arr.getJSONObject(i).getString("user_arrive_lng")));

                    item.setUser_sex(arr.getJSONObject(i).getString("user_sex"));
/*---------------------------------------------------------------거리계산및 세팅부분--------------------------------------------------------------------------------------------------------------*/
                    //거리구하기 테스트
                    String[] arr_startArrivePoint = startArrivePoint.split("#");
                    //탑승자 위치정보저장~
                    //핵심꼬임
                    item.setRider_start_lat(arr_startArrivePoint[0]);
                    item.setRider_start_lng(arr_startArrivePoint[1]);
                    item.setRider_arrive_lat(arr_startArrivePoint[2]);
                    item.setRider_arrive_lng(arr_startArrivePoint[3]);

                    Float distanceFromStart = getDistanceInMeter(arr_startArrivePoint[0],arr_startArrivePoint[1], arr.getJSONObject(i).getString("user_start_lat"),arr.getJSONObject(i).getString("user_start_lng"));
                    Float distanceFromArrive = getDistanceInMeter(arr_startArrivePoint[2],arr_startArrivePoint[3], arr.getJSONObject(i).getString("user_arrive_lat"),arr.getJSONObject(i).getString("user_arrive_lng"));
                    if(option_spinnner_distance.getSelectedItem().toString().equals("지정 출발지로부터 카풀 출발지까지")){
                        //tv_distanceinM.setText("지정 출발지로부터 카풀 출발지까지  : "+distanceFromStart+"M");
                        //tv_distanceinM.setText("지정 출발지로부터 카풀 출발지까지  : "+"M");
                       // item.setDistance("지정 출발지로부터 카풀 출발지까지 : "+distanceFromStart.intValue()+"M");
                        item.setDistance_option("지정 출발지로부터 카풀 출발지까지 : ");
                        item.setDistance_meters(""+distanceFromStart.intValue());
                    }else{
                        //tv_distanceinM.setText("지정 도착지로부터 카풀 도착지까지  : "+distanceFromArrive+"M");
                        //tv_distanceinM.setText("지정 도착지로부터 카풀 도착지까지  : "+"M");
                        //item.setDistance("지정 도착지로부터 카풀 도착지까지 : "+distanceFromArrive.intValue()+"M");
                        item.setDistance_option("지정 도착지로부터 카풀 도착지까지 : ");
                        item.setDistance_meters(""+distanceFromArrive.intValue());
                    }
/*---------------------------------------------------------------거리계산및 세팅부분--------------------------------------------------------------------------------------------------------------*/
                   // itemList.add(item);
                    adapter.addItem(item);

                    //JSONObject obj = new JSONObject(arr.get(i).toString());
                  //  Log.e("json"+i, arr.get(i).toString());


                    Log.e("ddd json"+i, arr.getJSONObject(i).toString());
                  Log.e("ddd user_email", arr.getJSONObject(i).getString("user_email")) ;
                }
                /*--------------------------------------------------------------------------------------------------------------------정렬---------------------------------------------------------------------------------------------------------------------------*/
                String option_date1 = from_date.getText().toString();
                String option_date2 = to_date.getText().toString();
                String option_distance, option_distance_meters, option_sex, option_howtoshow;

                if(et_option_distance==null){
                    option_distance_meters="입력한거리없음";
                }else{
                    option_distance_meters = et_option_distance.getText().toString();
                }

                option_distance = option_spinnner_distance.getSelectedItem().toString();
                option_sex = option_spinnner_sex.getSelectedItem().toString();
                option_howtoshow = option_spinnner_howtoshow.getSelectedItem().toString();

                /*순서대로 해야됨 아마도 ...*/
                adapter.applySearchOptionDate(option_date1, option_date2);
                adapter.applySearchOptionDistance(option_distance, option_distance_meters);
                adapter.applySexOption(option_sex);
                adapter.applyHowToShow(option_howtoshow);
                adapter.checkNoneOfItem();
                /*--------------------------------------------------------------------------------------------------------------------정렬------------------------------------------------------------------------------------------------------------------------------*/
                adapter.notifyDataSetChanged();

                Log.e("ddd",arr.get(0).toString());
            }catch (Exception e){
                e.printStackTrace();
            }

            //  Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
/*            String[] arr_result = result.split("#");
            for (int i = 0; i < arr_result.length; i++) {
               Log.e("result check", arr_result[i]);
            }*/

       // Log.e("ddd",result);

        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
          //  String user_email = (String) params[0];

            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/testgetalldriverinfo.php";
          ///  String postParameters = "user_email=" + user_email;


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
               // outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

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

                Log.d(TAG, "EmailCheck: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
/*-------------------------------------------------주소변환---------------------------------------------------------------*/
public String getAddress(String Latitude, String Longitude){

    //지오코더... GPS를 주소로 변환
    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    List<Address> addresses;

    try {

        addresses = geocoder.getFromLocation(
                Double.parseDouble(Latitude),
                Double.parseDouble(Longitude),
                1);
    } catch (IOException ioException) {
        //네트워크 문제
        Toast.makeText(this, "지오코더 서비스2 사용불가", Toast.LENGTH_LONG).show();
        return "지오코더 서비스2 사용불가";
    } catch (IllegalArgumentException illegalArgumentException) {
        Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
        return "잘못된 GPS 좌표";

    }


    if (addresses == null || addresses.size() == 0) {
        Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
        return "주소 미발견";

    } else {
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();
    }

}
/*-------------------------------------------------거리 구하기---------------------------------------------------------------*/
public float getDistanceInMeter(String startLat_s, String startLong_s, String endLat_s, String endLong_s) {
    Double startLat = Double.parseDouble(startLat_s);
    Double startLong = Double.parseDouble(startLong_s);
    Double endLat = Double.parseDouble(endLat_s);
    Double endLong = Double.parseDouble(endLong_s);

    Location sourceLoc = new Location("");
    sourceLoc.setLatitude(startLat);
    sourceLoc.setLongitude(startLong);
    Location destLoc = new Location("");
    destLoc.setLatitude(endLat);
    destLoc.setLongitude(endLong);
    float distanceInMeters = sourceLoc.distanceTo(destLoc);
    return distanceInMeters;
}
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound){
            unbindService(mConnection);
            mBound=false;
        }
    }
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
}




