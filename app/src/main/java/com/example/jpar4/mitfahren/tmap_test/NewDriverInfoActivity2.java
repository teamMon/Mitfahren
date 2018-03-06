package com.example.jpar4.mitfahren.tmap_test;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewDriverInfoActivity2 extends AppCompatActivity implements
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

        GetDriverInfo task = new GetDriverInfo();
        task.execute("qlql@qlql.com");

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

            progressDialog = ProgressDialog.show(NewDriverInfoActivity2.this,
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


            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String user_email = (String)params[0];

            String serverURL = "http://ec2-13-124-251-123.ap-northeast-2.compute.amazonaws.com/db/get_driver_info.php";
            String postParameters = "user_email=" + user_email;


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
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));

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


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(arriveLocation));

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
}
