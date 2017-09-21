package com.example.jpar4.mitfahren.tmap_test;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.model.Item_New_Driver_Info;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewDriverInfoActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener{
    private static final String TAG = "NewDriverInfo";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    /*app객체 (로그인에 정보 이용에사용)*/
    Myapp app;
    /*app객체 (로그인에 정보 이용에 사용)*/
    /**
     * Context Variables
     */
    Context mContext;

    /*화면 View 객체 세팅*/
    ImageView iv_driver_info_pic;
    TextView tv_driver_info_date, tv_driver_info_time, tv_driver_info_people, tv_driver_info_start, tv_driver_info_arrive;
    Button btn_driver_info_confirm, iv_driver_info_see_user_info, btn_see_apply_list, btn_carpool_apply;
    Item_New_Driver_Info item_new_driver_info;
    /*화면 View 객체 세팅*/

    /*--------------------------------구글맵 내 현재 위치 찍기----------------------------------------*/
    /*구글맵*/
    private GoogleMap mGoogleMap = null;
    /*구글맵 Line과 라인여부 추가*/
    private Polyline line;
    private boolean isLineOnMap = false;
    /*출발지 마커*/
    private Marker StartMarker = null;
    /*도착지 마커*/
    private Marker ArriveMarker = null;
    //디폴트 위치, Seoul
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private AppCompatActivity mActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("카풀 정보");
        setContentView(R.layout.new_driver_info_activity);
        /*app객체*/
        app = (Myapp)getApplicationContext();
        mContext = getApplicationContext();

        Intent intent = getIntent();
        /*에드드라이버2에서 날라올때*/
        String  user_start_date = intent.getStringExtra("user_start_date");
        /*리스트뷰에서 날라올때*/
        item_new_driver_info  = (Item_New_Driver_Info) intent.getSerializableExtra("data");


        iv_driver_info_pic = (ImageView) findViewById(R.id.iv_driver_info_pic);
        tv_driver_info_date = (TextView) findViewById(R.id.tv_driver_info_date);
        tv_driver_info_time = (TextView) findViewById(R.id.tv_driver_info_time);
        tv_driver_info_people = (TextView) findViewById(R.id.tv_driver_info_people);
        tv_driver_info_start = (TextView) findViewById(R.id.tv_driver_info_start);
        tv_driver_info_arrive = (TextView) findViewById(R.id.tv_driver_info_arrive);

        iv_driver_info_see_user_info = (Button)findViewById(R.id.iv_driver_info_see_user_info); //운전자 정보 보기
        iv_driver_info_see_user_info.setOnClickListener(this);

        btn_driver_info_confirm = (Button)findViewById(R.id.btn_driver_info_confirm); //운전자 등록 후 확인
        btn_driver_info_confirm.setOnClickListener(this);

        btn_see_apply_list = (Button)findViewById(R.id.btn_see_apply_list); //신청자 목록 보기
        btn_see_apply_list.setOnClickListener(this);

        btn_carpool_apply = (Button)findViewById(R.id.btn_carpool_apply); //카풀 신청하기
        btn_carpool_apply.setOnClickListener(this);


       /*
        * 1. 인텐트로 이메일을 전달받아 넘겨줄것.
        *
        */
       if(app.isLoginOK()){// 로그인이고 앱 아이디와
           if(item_new_driver_info==null){ // 등록후 확인페이지면 null
               GetDriverInfo task = new GetDriverInfo();
               //task.execute("qlql@qlql.com");
               Log.e("ddd ", app.getUser_email()+","+item_new_driver_info.getUser_start_date());
               task.execute(app.getUser_email(), user_start_date);
           }
       /*    else if(!app.getUser_email().equals(item_new_driver_info.getUser_email())){
               GetDriverInfo task = new GetDriverInfo();
               //task.execute("qlql@qlql.com");\
               Log.e("ddd 2", item_new_driver_info.getUser_email()+","+item_new_driver_info.getUser_start_date());
               task.execute(item_new_driver_info.getUser_email(), item_new_driver_info.getUser_start_date());
           }*/else{//검색페이지에서 온거면 not null

               if(!app.getUser_email().equals(item_new_driver_info.getUser_email())){// 다르면 카풀 신청하기 나오고
                   btn_driver_info_confirm.setVisibility(View.GONE);
                   btn_carpool_apply.setVisibility(View.VISIBLE);
               }else{// 같으면 신청 목록 보기 나오고
                   btn_driver_info_confirm.setVisibility(View.GONE);
                   btn_see_apply_list.setVisibility(View.VISIBLE);
               }
               GetDriverInfo task = new GetDriverInfo();
               //task.execute("qlql@qlql.com");\
               Log.e("ddd 2", item_new_driver_info.getUser_email()+","+item_new_driver_info.getUser_start_date());
               task.execute(item_new_driver_info.getUser_email(), item_new_driver_info.getUser_start_date());
           }

       }else{// 운전자 검색을 통해 운전자 아이디를 전달 받는 경우, 이메일을 전달받는 경우 이를 운전자 정보 페이지에도 전달할 수 있도록 구현해야 함.
           GetDriverInfo task = new GetDriverInfo();
           //task.execute("qlql@qlql.com");
           Log.e("ddd 3", item_new_driver_info.getUser_email()+","+item_new_driver_info.getUser_start_date());
           task.execute(item_new_driver_info.getUser_email(), item_new_driver_info.getUser_start_date());
           Toast.makeText(app, "로그인 후에 카풀신청이 가능합니다.", Toast.LENGTH_SHORT).show();
       }





       /*구글맵셋팅. 뭔지모름사실 화면 안꺼지게 셋팅하는건가?*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mActivity = this;
        /*구글맵셋팅. 뭔지모름사실 화면 안꺼지게 셋팅하는건가?*/

        /*맵불러오기*/
         /*프레그먼트매니저 불러옴*/
        FragmentManager fragmentManager = getFragmentManager();
        /*프레그먼트에 레이아웃에 있는 map 연결*/
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.driver_info_map);
        /*이거 this= maybe MapTestActivity? 이거 지금 보고 있는거 연동시켜주는건가? 잘 모름..*/
        mapFragment.getMapAsync(this);
    }
    /*-----------------------------------------------------------------------디비에서 운전자 정보를 가져옮-----------------------------------------------------------------------*/
        /*로그인 확인하는 쓰레드*/
    class GetDriverInfo extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(NewDriverInfoActivity.this,
                    "잠시만 기다려 주세요.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            //  Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();

            String[] arr_result = result.split("#");
            for(int i=0;i<arr_result.length;i++){
                Log.e("result check", arr_result[i]);
            }
/*에이싱크결과값 받아옴*/
            String user_email = arr_result[0];
            String user_start_date = arr_result[1];
            String user_start_time = arr_result[2];
            String user_with_poeple = arr_result[3];
            String user_start_lat = arr_result[4];
            String user_start_lng = arr_result[5];
            String user_arrive_lat = arr_result[6];
            String user_arrive_lng = arr_result[7];
            String user_having_rider = arr_result[8];
            String user_car_photo = arr_result[9];

            tv_driver_info_date.setText(user_start_date);

            SimpleDateFormat old_time_format = new SimpleDateFormat("hh:mm:ss");
            SimpleDateFormat new_time_format = new SimpleDateFormat("hh:mm aa");
            String new_time="";
            try{
                Date start_time = old_time_format.parse(user_start_time);
                new_time = new_time_format.format(start_time);

            }catch(ParseException e){
                e.printStackTrace();
            }


            tv_driver_info_time.setText(new_time);
            tv_driver_info_people.setText(user_with_poeple + " 명");
            Picasso.with(mContext).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/"+user_car_photo).into(iv_driver_info_pic);

            //getCurrentAddress2(user_start_lat, user_start_lng); // 출발지 주소
            //getCurrentAddress2(user_arrive_lat, user_arrive_lng); // 도착지 주소
            tv_driver_info_start.setText( getCurrentAddress2(user_start_lat, user_start_lng));
            tv_driver_info_arrive.setText(getCurrentAddress2(user_arrive_lat, user_arrive_lng));

            //TextView tv_driver_info_date, tv_driver_info_time, tv_driver_info_people, tv_driver_info_start, tv_driver_info_arrive;iv_driver_info_pic

            /*마커찍기*/
            setStartLocation(Double.parseDouble(user_start_lat), Double.parseDouble(user_start_lng));
            setArriveLocation(Double.parseDouble(user_arrive_lat), Double.parseDouble(user_arrive_lng));

            /*카메라 포커스*/
            // 카메라로 두 마커가 보이게 조절거리에 따른 줌레벨 조절 필요
            Double cLat =  (Double)((Double.parseDouble(user_start_lat)+Double.parseDouble(user_arrive_lat))/2);
            Double cLon =  (Double)((Double.parseDouble(user_start_lng)+Double.parseDouble(user_arrive_lng))/2);
            LatLng currentLocation = new LatLng( cLat, cLon);

            /*줌레벨 설정*/
            float distanceInMeters= getDistanceInMeter((Double.parseDouble(user_start_lat)), (Double.parseDouble(user_start_lng)), (Double.parseDouble(user_arrive_lat)), (Double.parseDouble(user_arrive_lng))); // 거리계산
            float properZoomLevel=getZoomForMetersWide(distanceInMeters); //줌레벨 계산
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, properZoomLevel-1));
            Toast.makeText(mActivity, Float.toString(distanceInMeters)+" / "+ Float.toString(properZoomLevel) , Toast.LENGTH_SHORT).show();  //테스트

            /*경로 보여주기(해야됨)*/
           GetPathJson getJsonPath = new GetPathJson();
            getJsonPath.execute(""+(Double.parseDouble(user_start_lat)), ""+(Double.parseDouble(user_start_lng)), ""+(Double.parseDouble(user_arrive_lat)), ""+(Double.parseDouble(user_arrive_lng)));

            /*경로 표시*/

           Log.d(TAG, "POST response  - " + result);

        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String user_email = (String)params[0];
            String user_start_date = (String)params[1];

            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/get_driver_info.php";
            String postParameters = "user_email=" + user_email + "&user_start_date=" + user_start_date;


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
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_driver_info_see_user_info: //사용자 정보보기
                intent = new Intent(NewDriverInfoActivity.this, NewUserPageActivity.class);
                if(app.isLoginOK()){ // 로그인 되어 있을 때
                    if(item_new_driver_info==null){ // 등록후 확인페이지면 null
                        intent.putExtra("user_email", app.getUser_email());
                    }else {
                        if(!app.getUser_email().equals(item_new_driver_info.getUser_email())){// 다르면 카풀 신청하기 나오고
                            intent.putExtra("user_email", item_new_driver_info.getUser_email());
                        }else{// 같으면 신청 목록 보기 나오고
                            intent.putExtra("user_email", item_new_driver_info.getUser_email());
                        }
                    }
               }
                else{
                    intent.putExtra("user_email", item_new_driver_info.getUser_email());
                }


                startActivity(intent);

                break;
            case R.id.btn_driver_info_confirm: // 확인버튼을 누르면 다시 메인페이지로 이동할 수 있도록 함.
                intent = new Intent(NewDriverInfoActivity.this,  NewSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMove() {

    }
    /*OnMapReadyCallback*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mGoogleMap = googleMap;
        // camera 좌쵸를 서울역 근처로 옮겨 봅니다.
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(37.555744, 126.970431)   // 위도, 경도
        ));


        mGoogleMap.setOnCameraIdleListener(this);
        mGoogleMap.setOnCameraMoveListener(this);

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
/*        setCurrentLocation(null, "위치정보 가져올 수 없음",
                "위치 퍼미션과 GPS 활성 요부 확인하세요");*/


        mGoogleMap.getUiSettings().setCompassEnabled(true);
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //API 23 이상이면 런타임 퍼미션 처리 필요

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions( mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            } else {

              /*  if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }*/

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mGoogleMap.setMyLocationEnabled(true);
                }

            }
        } else {

           /* if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }*/


            mGoogleMap.setMyLocationEnabled(true);

        }


       /* 서울 수도 맵에 마커로 찍기 (연습1)
       LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));*/
    }
    /*OnMapReadyCallback*/

    public void setStartLocation(Double Latitude, Double Longitude) {

        if (StartMarker != null) StartMarker.remove();



        LatLng startLocation = new LatLng(Latitude, Longitude);

        //마커를 원하는 이미지로 변경해줘야함
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(startLocation);
       /* markerOptions.title("출발지");*/
        // markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_start)));
               /* .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));*/
        StartMarker = mGoogleMap.addMarker(markerOptions); //최초마커 지우기


        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));

        return;


            /*MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(DEFAULT_LOCATION);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            // StartMarker = mGoogleMap.addMarker(markerOptions);

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));*/

    }

    public void setArriveLocation(Double Latitude, Double Longitude) {

        if (ArriveMarker != null) ArriveMarker.remove();



        LatLng arriveLocation = new LatLng( Latitude, Longitude);

        //마커를 원하는 이미지로 변경해줘야함
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(arriveLocation);
     /*   markerOptions.title("도착지");*/
        //markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_arrive)));
                /*.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));*/
        ArriveMarker = mGoogleMap.addMarker(markerOptions); //최초마커 지우기


        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(arriveLocation));

        return;


       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        // ArriveMarker = mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
*/
    }

    public String getCurrentAddress2(String Latitude, String Longitude){

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
    /*줌줌줌++++++++++++++++++++++++++++++++++Calculate distance between two locations+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public float getDistanceInMeter(double startLat, double startLong, double endLat, double endLong) {
        Location sourceLoc = new Location("");
        sourceLoc.setLatitude(startLat);
        sourceLoc.setLongitude(startLong);
        Location destLoc = new Location("");
        destLoc.setLatitude(endLat);
        destLoc.setLongitude(endLong);
        float distanceInMeters = sourceLoc.distanceTo(destLoc);
        return distanceInMeters;
    }
    /*줌줌줌++++++++++++++++++++++++++++++++++Calculate zoom level from distance+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public float getZoomForMetersWide(double desiredMeters) {
        float properZoomLevel;
        if(desiredMeters > 20088000){
            properZoomLevel = 0;
        }else if(desiredMeters > 10044000){
            properZoomLevel = 1;
        }else if(desiredMeters > 5022000.14151925){
            properZoomLevel = 2;
        }else if(desiredMeters > 2511000.07075963){
            properZoomLevel = 3;
        }else if(desiredMeters > 1255500.03537981){
            properZoomLevel = 4;
        }else if(desiredMeters > 627750.01768991){
            properZoomLevel = 5;
        }else if(desiredMeters > 313875){
            properZoomLevel = 6;
        }else if(desiredMeters > 156937.50442248){
            properZoomLevel = 7;
        }else if(desiredMeters > 78468){
            properZoomLevel = 8;
        }else if(desiredMeters > 39234.37610562){
            properZoomLevel = 9;
        }else if(desiredMeters > 19617.18805281){
            properZoomLevel = 10;
        }else if(desiredMeters > 9808.59402640){
            properZoomLevel = 11;
        }else if(desiredMeters > 4909.29701320){
            properZoomLevel = 12;
        }else if(desiredMeters > 2452.14850660){
            properZoomLevel = 13;
        }else if(desiredMeters > 1226.07425330){
            properZoomLevel = 14;
        }else if(desiredMeters > 613.03712665){
            properZoomLevel = 15;
        }else if(desiredMeters > 306.51856332){
            properZoomLevel = 16;
        }else if(desiredMeters > 153.25928166){
            properZoomLevel = 17;
        }else if(desiredMeters > 76){
            properZoomLevel = 18;
        }else if(desiredMeters > 38.31482042) {
            properZoomLevel = 19;
        }else{
            properZoomLevel = 19;
        }

        return properZoomLevel;
    }
    /*-----------------------------------------GetPathJson---------------------------------------------------------------------------------------------------*/
    public class GetPathJson extends AsyncTask<String, Void, String> {
        // 자동차 안내 tmap
        String startLat;
        String startLng;
        String endLat;
        String endLng;


        @Override
        protected String doInBackground(String... params) {
            /*시작 위도, 경도, 도착 위도, 경도*/
            startLat = params[0];
            startLng = params[1];
            endLat = params[2];
            endLng = params[3];
            String result="";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://apis.skplanetx.com/tmap/routes?version=1&appKey=eb2b21b5-65d8-3686-9664-1442352bab42&startX=" + startLng + "&startY=" + startLat + "&endX=" + endLng + "&endY=" + endLat + "&reqCoordType=WGS84GEO&resCoordType=WGS84GEO")
                    .build(); // 이 url에 좌표를 전송하여 json구조를 전송받는다. 구글의 좌표(위도, 경도)와 다음의 좌표 (경도, 위도) 순서가 다르다
            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("ddd[GetPathJson]출도json", result);
            // 결과값을 파싱해주는 스레드에 넘김
            ParsePathJson aParsePathJson = new ParsePathJson();
            aParsePathJson.execute(result);
        }
    }

  /*-----------------------------------------GetPathJson---------------------------------------------------------------------------------------------------*/


    /*-----------------------------------------ParsePathJson---------------------------------------------------------------------------------------------------*/
    public class ParsePathJson extends AsyncTask<String, Integer, ArrayList<LatLng>> {
        // Parsing the data in non-ui thread

        int totalDistance;
        ArrayList<com.google.android.gms.maps.model.LatLng> mapPoints; //LatLng 변수

        @Override
        protected ArrayList<com.google.android.gms.maps.model.LatLng> doInBackground(String... jsonData) {
            try {
                JSONObject jsonObject = new JSONObject(jsonData[0]); //통제이슨을 받아옴
                JSONArray features = jsonObject.getJSONArray("features"); // json 이름 형상 정보 입니다. (geojson 표준 규격) //키값 : Type과 features중 features가져옴

                mapPoints = new ArrayList<>(); //지도에 찍을 맵 좌표값을 저장할 어레이리스트를 만듬

                for (int i = 0; i < features.length(); i++) { // 블로그 참고 하였다.
                    JSONObject test2 = features.getJSONObject(i);
                    if (i == 0) {
                        JSONObject properties = test2.getJSONObject("properties"); // 프로퍼티에는 전체 거리 전체 시간 택시요금 네비안내 다음에올길이름등 정보가 들어있다.
                        totalDistance += properties.getInt("totalDistance"); // 경로 총 길이(단위: m)입니다. - pointType=S 일때 응답되는 정보입니다.

                    }

                    JSONObject geometry = test2.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates"); //좌표 정보입니다.
                    String geoType = geometry.getString("type"); // 구간의 정보입니다.

                    if (geoType.equals("Point")) { // 꼭지점에 해당하는 Point 객체
                        double lonJson = coordinates.getDouble(0);
                        double latJson = coordinates.getDouble(1);

//                        Log.d("TAG", "-");
//                        Log.d("TAG", lonJson + "," + latJson + "\n");
                        com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);
                        mapPoints.add(point);

                    }

                    if (geoType.equals("LineString")) { // 선에 해당하는 LineString 객체
                        for (int j = 0; j < coordinates.length(); j++) {
                            JSONArray JLinePoint = coordinates.getJSONArray(j);
                            double lonJson = JLinePoint.getDouble(0);
                            double latJson = JLinePoint.getDouble(1);

//                            Log.d("TAG", "-");
//                            Log.d("TAG", lonJson + "," + latJson + "\n");
                            com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);
                            mapPoints.add(point);

                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapPoints;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(ArrayList<com.google.android.gms.maps.model.LatLng> result) {
            PolylineOptions lineOptions = null;
            lineOptions = new PolylineOptions();

            for (int i = 0; i < result.size(); i++) {
                com.google.android.gms.maps.model.LatLng point = result.get(i);

                LatLng position = new LatLng(point.latitude, point.longitude);

//                Log.e("position", position + "");
//                points.add(position);
//                Log.e("points", points + "");

/*                // 출발지 마커
                if (i == result.size() - 1) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(position);
                    markerOptions.title("출발지");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    marker = mGoogleMap.addMarker(markerOptions);


                    // 출발지로 맵이동
                    CameraPosition cp = new CameraPosition.Builder().target((position)).zoom(15).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                }*/

                // Adding all the points in the route to LineOptions
                lineOptions.add(position);
                lineOptions.width(20);
                //  lineOptions.color(Color.argb(164,57,17,64)); //rgba(57, 17, 64, 0.62) // 보라색
                lineOptions.color(Color.argb(170,0,89,44));
            }

            // 폴리라인을 그려준다.
            line = mGoogleMap.addPolyline(lineOptions);
            // 처음 맵이 그려질 때는 line변수가 없기 때문에 line변수가 있는지 없는 지 구별해줄 조건이 필요했음
            isLineOnMap = true;
        }
    }
/*-----------------------------------------ParsePathJson---------------------------------------------------------------------------------------------------*/
}
