package com.example.jpar4.mitfahren.tmap_test;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.func.HttpAssoci_Func;
import com.example.jpar4.mitfahren.model.NewMapLocaInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class NewAddDriverActivity2 extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener
{
    /*좌표 핀셋찍기에 사용  private String mLatitude,mLongitude;*/
    private String mLatitude,mLongitude;
    private Button btn_select_start; // 출발지 검색 버튼
    private Button btn_select_arrive; // 도착지 검색 버튼
    private Button btn_search_driver; // 운전자 검색 버튼
    // AutoCompleteTextView actv_search_start; // 출발지검색
    // AutoCompleteTextView actv_search_arrive; // 도착지검색
    TextView actv_search_start; // 출발지검색
    TextView actv_search_arrive; // 도착지검색
    TextView tv_once_explain; // 1회 출발지 검색을 할 수 있게 도와주는 문구를 보여줌
    TextView tv_helpingText; //위치를 찾는 중 입니다.
    boolean firstCamera;// 내 위치로 맵카매라 계속 포커스 오는 것 방지용 플래그
    Double lat; // 주소->좌표용
    Double lng;
    Boolean start_arrive = true; // true : start / false : arrive
    Boolean start_arrive_done = false; // true : 출발지, 도착지 선택 완료
    NewMapLocaInfo Start_Marker_InFo; // 출발지 정보 가지고 있는 클래스
    NewMapLocaInfo Arrive_Marker_InFo; // 도착지 정보 가지고 있는 클래스
/*    Boolean once_explain = true; // true : 딱한번 위의 현재 위치를 나타내는 주소 텍스트를 누르면 주소를 검색할 수 있음을 알려줌*/
    /*좌표 핀셋찍기에 사용  mLatitude, mLongitude, showposition*/


    /*--------------------------------구글맵 내 현재 위치 찍기----------------------------------------*/
     /*구글 ApiClient??*/
    private GoogleApiClient mGoogleApiClient = null;
    /*구글맵*/
    private GoogleMap mGoogleMap = null;
    /*현재찍는 곳*/
    private Marker currentMarker = null;
    /*출발지 마커*/
    private Marker StartMarker = null;
    /*도착지 마커*/
    private Marker ArriveMarker = null;
    //디폴트 위치, Seoul
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 10000;  // 10초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 10000; // 10초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    /*--------------------------------구글맵 내 현재 위치 찍기----------------------------------------*/

    /*주소받아오기 위한 REQUESTCODE*/
    private static final int GET_ADDRESS_REQUEST_CODE = 3001;
     /*주소받아오기 위한 REQUESTCODE*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_search);*/
 /*       android.support.v7.app.ActionBar ab = getSupportActionBar();
        if(once_explain){
            ab.setTitle("대한민국 카풀의 정석, TaTaTa");
            once_explain = false;
        }else  ab.setTitle("TaTaTa");-> 노 액션바라 안되는듯..*/
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("카풀 정보 등록");


        setContentView(R.layout.test_activity_new_add_driver2);

         /*좌표 핀셋찍기에 사용  mLatitude, mLongitude, showposition*/
        btn_select_start = (Button)findViewById(R.id.btn_select_start); // 출발지 버튼
        btn_select_start.setOnClickListener(this);
        btn_select_arrive = (Button)findViewById(R.id.btn_select_arrive); //도착지 버튼 -> 누를때 마커가 찍히게 하기
        btn_select_arrive.setOnClickListener(this);
        btn_search_driver = (Button)findViewById(R.id.btn_search_driver); //운전자 검색 버튼 -> 눌렀을 때, 운전자 리스트 뷰 나오게 하기
        btn_search_driver.setOnClickListener(this);

        actv_search_start = (TextView) findViewById(R.id.actv_search_start);
        actv_search_start.setOnClickListener(this);

        actv_search_arrive = (TextView) findViewById(R.id.actv_search_arrive);
        actv_search_arrive.setOnClickListener(this);

        tv_once_explain = (TextView) findViewById(R.id.tv_once_explain);
        tv_helpingText = (TextView) findViewById(R.id.tv_helpingText);

        /*좌표 핀셋찍기에 사용  mLatitude, mLongitude, showposition*/

         /*--------------------------------구글맵 내 현재 위치 찍기----------------------------------------*/
         /*현재위치 표시하기 위한 구글맵*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mActivity = this;
        /*현재위치 표시하기 위한 구글맵*/

        /*프레그먼트매니저 불러옴*/
        FragmentManager fragmentManager = getFragmentManager();
        /*프레그먼트에 레이아웃에 있는 map 연결*/
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        /*이거 this= maybe MapTestActivity? 이거 지금 보고 있는거 연동시켜주는건가? 잘 모름..*/
        mapFragment.getMapAsync(this);


        /*--------------------------------구글맵 내 현재 위치 찍기----------------------------------------*/
        
        /* ****************************************인텐트 받기******************************************************** */
        Intent intent = getIntent();
        String start_d = intent.getStringExtra("date");
        String start_t = intent.getStringExtra("time");
        String start_p = intent.getStringExtra("pNum");

        Toast.makeText(mActivity, start_d+" "+start_t+" "+start_p, Toast.LENGTH_SHORT).show();

    }


    /*-------------------------------------------맵에 내 현재 위치 찍기---------------------------------------------------------------------------------------*/
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstCamera = true;
        if (mGoogleApiClient!=null)
            mGoogleApiClient.connect();

        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }

    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        //위치 업데이트 중지
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);

            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi
                        .removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }

        }
        super.onDestroy();
    }



    /*OnMapReadyCallback*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mGoogleMap = googleMap;
        // camera 좌쵸를 서울역 근처로 옮겨 봅니다.
/*        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(37.555744, 126.970431)   // 위도, 경도
        ));*/


        mGoogleMap.setOnCameraIdleListener(this);
        mGoogleMap.setOnCameraMoveListener(this);

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setCurrentLocation(null, "위치정보 가져올 수 없음",
                "위치 퍼미션과 GPS 활성 요부 확인하세요");


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

                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mGoogleMap.setMyLocationEnabled(true);
                }

            }
        } else {

            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }


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



    /*GoogleApiClient.ConnectionCallbacks*/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"onConnected");
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            }
        }
        else{

            Log.d(TAG,"onConnected : call FusedLocationApi");
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            mGoogleMap.getUiSettings().setCompassEnabled(true);
            //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if ( cause ==  CAUSE_NETWORK_LOST )
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED )
            Log.e(TAG,"onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }
    /*GoogleApiClient.ConnectionCallbacks*/
    /*GoogleApiClient.OnConnectionFailedListener*/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Location location = null;
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude(DEFAULT_LOCATION.longitude);

        setCurrentLocation(location, "위치정보 가져올 수 없음",
                "위치 퍼미션과 GPS 활성 요부 확인하세요");
    }
    /*GoogleApiClient.OnConnectionFailedListener*/
    /*LocationListener*/
    @Override
    public void onLocationChanged(Location location) {
        Log.d( TAG, "onLocationChanged");
        String markerTitle = getCurrentAddress(location);
        String markerSnippet = "위도:"+String.valueOf(location.getLatitude())
                + " 경도:"+String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성
        setCurrentLocation(location, markerTitle, markerSnippet );

    }

    /*
     * 퍼미션을 얻었을 때, mGoogleApiClient가 없다면 실행시켜줌.
     */
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        if (currentMarker != null) currentMarker.remove();


        if (location != null) {
            LatLng currentLocation = new LatLng( location.getLatitude(), location.getLongitude());

            //마커를 원하는 이미지로 변경해줘야함
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            //currentMarker = mGoogleMap.addMarker(markerOptions); //최초마커 지우기

            if(firstCamera){
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                firstCamera=false;
            }
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        // currentMarker = mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }
    public void setFocusStartLocation(Double Latitude, Double Longitude) {//검색결과 좌표에 포커스 ㄱㄱ
        LatLng searchLocation = new LatLng(Latitude, Longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(searchLocation));
    }

    public void setStartLocation(Double Latitude, Double Longitude) {

        if (StartMarker != null) StartMarker.remove();



        LatLng currentLocation = new LatLng(Latitude, Longitude);

        //마커를 원하는 이미지로 변경해줘야함
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
       /* markerOptions.title("출발지");*/
        // markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_start)));
               /* .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));*/
        StartMarker = mGoogleMap.addMarker(markerOptions); //최초마커 지우기


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

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



        LatLng currentLocation = new LatLng( Latitude, Longitude);

        //마커를 원하는 이미지로 변경해줘야함
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
     /*   markerOptions.title("도착지");*/
        //markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_arrive)));
                /*.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));*/
        ArriveMarker = mGoogleMap.addMarker(markerOptions); //최초마커 지우기


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

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

    public String getCurrentAddress(Location location){

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
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


    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    /*LocationListener*/
    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale )
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale ) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        }

        else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }

            mGoogleMap.setMyLocationEnabled(true);


        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(NewAddDriverActivity2.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions( mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(NewAddDriverActivity2.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(NewAddDriverActivity2.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?" );
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {

                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mGoogleMap.setMyLocationEnabled(true);
                }


            }
            else {

                checkPermissions();
            }
        }
    }
    /*-------------------------------------------맵에 내 현재 위치 찍기---------------------------------------------------------------------------------------*/
/*-------------------------------------------버튼 리스너---------------------------------------------------------------------------------------*/
    @Override
    public void onClick(View v) {
        Intent intent;
        String address_name;
        switch (v.getId()){
            case R.id.btn_select_start: //출발지 지정 버튼
                start_arrive_done= false; // 출발지 도착지 지정 완료 true로 바꿔줌

                mLatitude=mGoogleMap.getCameraPosition().target.latitude+"";
                mLongitude=mGoogleMap.getCameraPosition().target.longitude+"";
                address_name = getCurrentAddress2(mLatitude,mLongitude);
       /*        Toast.makeText(NewSearchActivity.this, "Latitude:"+mLatitude +"\n Longitude:"+mLongitude+"\n address: "+ address_name,Toast.LENGTH_LONG).show();*/
                btn_select_start.setVisibility(View.GONE); //출발지 지정을 눌렀을 때, 출발지 지정 버튼이 사라지고
                btn_select_arrive.setVisibility(View.VISIBLE); //도착지 지정 버튼이 나오게 함 -> 도착지 검색 텍스트 뷰를 눌렀을 때에도 똑같이 적용함
                btn_search_driver.setVisibility(View.GONE);
                tv_once_explain.setVisibility(View.GONE); // 한번 누르면 사라지게 함.
                actv_search_start.setTextColor(Color.parseColor("#857C7474"));
                actv_search_arrive.setTextColor(Color.parseColor("#d4e6213b"));

            /*마커찍기*/
                setStartLocation(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
            /*데이터 클래스에 값 저장*/
                Start_Marker_InFo = new NewMapLocaInfo("출발지", address_name, Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
            /*저장된 값 테스트*/
                //  Toast.makeText(NewSearchActivity.this, "Latitude:"+Start_Marker_InFo.getLat() +"\n Longitude:"+Start_Marker_InFo.getLng()+"\n address: "+ Start_Marker_InFo.getMarkertitle(),Toast.LENGTH_LONG).show();


                start_arrive = false;
                break;

            case R.id.btn_select_arrive://도착지 지정 버튼 누르면
                start_arrive_done= true; // 출발지 도착지 지정 완료 true로 바꿔줌
            /*도착지 출발지 지정버튼 없얘고 운전자 검색 버튼 나오게 하기*/
                btn_select_arrive.setVisibility(View.GONE);
                btn_select_start.setVisibility(View.GONE);
                btn_search_driver.setVisibility(View.VISIBLE);

                mLatitude=mGoogleMap.getCameraPosition().target.latitude+"";
                mLongitude=mGoogleMap.getCameraPosition().target.longitude+"";
                address_name = getCurrentAddress2(mLatitude,mLongitude);
            /*마커찍기*/
                setArriveLocation(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
            /*데이터 클래스에 값 저장*/
                Arrive_Marker_InFo = new NewMapLocaInfo("도착지", address_name, Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
            /*저장된 값 테스트*/
                //   Toast.makeText(NewSearchActivity.this, "Latitude:"+Arrive_Marker_InFo.getLat() +"\n Longitude:"+Arrive_Marker_InFo.getLng()+"\n address: "+ Arrive_Marker_InFo.getMarkertitle(),Toast.LENGTH_LONG).show();

                // 카메라로 두 마커가 보이게 조절거리에 따른 줌레벨 조절 필요
                Double cLat =  (Double)((Arrive_Marker_InFo.getLat()+Start_Marker_InFo.getLat())/2);
                Double cLon =  (Double)((Arrive_Marker_InFo.getLng()+Start_Marker_InFo.getLng())/2);
                LatLng currentLocation = new LatLng( cLat, cLon);

            /*줌레벨 설정*/
                float distanceInMeters= getDistanceInMeter(Start_Marker_InFo.getLat(), Start_Marker_InFo.getLng(), Arrive_Marker_InFo.getLat(), Arrive_Marker_InFo.getLng()); // 거리계산
                float properZoomLevel=getZoomForMetersWide(distanceInMeters); //줌레벨 계산
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, properZoomLevel));
                Toast.makeText(mActivity, Float.toString(distanceInMeters)+" / "+ Float.toString(properZoomLevel) , Toast.LENGTH_SHORT).show();  //테스트

            /*경로 보여주기(해야됨)*/




                break;

            case R.id.actv_search_start:
                start_arrive = true; // 출발지 텍스트뷰 누르면 true로
                start_arrive_done = false;

                btn_select_start.setVisibility(View.VISIBLE); //
                btn_select_arrive.setVisibility(View.GONE); //
                btn_search_driver.setVisibility(View.GONE);
                tv_once_explain.setVisibility(View.GONE); // 한번 누르면 사라지게 함.

                actv_search_start.setTextColor(Color.parseColor("#000000"));
                actv_search_arrive.setTextColor(Color.parseColor("#857C7474"));

                intent = new Intent(NewAddDriverActivity2.this, NewAddressSearchActivity.class);
                intent.putExtra("start_arrive", start_arrive);
                startActivityForResult(intent, GET_ADDRESS_REQUEST_CODE); //
                break;

            case R.id.actv_search_arrive:
                start_arrive = false; // 도착지 텍스트뷰 누르면 false로
                start_arrive_done = false;
                btn_select_start.setVisibility(View.GONE); //출발지 지정을 눌렀을 때, 출발지 지정 버튼이 사라지고
                btn_search_driver.setVisibility(View.GONE);
                btn_select_arrive.setVisibility(View.VISIBLE); //도착지 지정 버튼이 나오게 함 -> 도착지 검색 텍스트 뷰를 눌렀을 때에도 똑같이 적용함
                tv_once_explain.setVisibility(View.GONE); // 한번 누르면 사라지게 함.
                actv_search_start.setTextColor(Color.parseColor("#857C7474"));
                actv_search_arrive.setTextColor(Color.parseColor("#d4e6213b"));

                mLatitude=mGoogleMap.getCameraPosition().target.latitude+"";
                mLongitude=mGoogleMap.getCameraPosition().target.longitude+"";
                address_name = getCurrentAddress2(mLatitude,mLongitude);

            /*------------------------------------------------------------------------그냥 텍스트뷰를 클릭했을 때에도 출발지 마커를 찍어 줘야 함------------------------------------------------------------------------*/
        /*    *//*마커찍기*//*
                setStartLocation(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
            *//*데이터 클래스에 값 저장*//*
                Start_Marker_InFo = new NewMapLocaInfo("출발지", address_name, Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));*/
      /*------------------------------------------------------------------------그냥 텍스트뷰를 클릭했을 때에도 출발지 마커를 찍어 줘야 함------------------------------------------------------------------------*/

                intent = new Intent(NewAddDriverActivity2.this, NewAddressSearchActivity.class);
                //startActivity(intent);
                intent.putExtra("start_arrive", start_arrive);
                startActivityForResult(intent, GET_ADDRESS_REQUEST_CODE); //
                break;
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

    /*-------------------------------------------버튼 리스너---------------------------------------------------------------------------------------*/
     /*출발지 도착지 값 받아오는곳*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                                mGoogleMap.setMyLocationEnabled(true);
                            }
                        }
                        else  mGoogleMap.setMyLocationEnabled(true);

                        return;
                    }
                }
                else{
                    setCurrentLocation(null, "위치정보 가져올 수 없음",
                            "위치 퍼미션과 GPS 활성 요부 확인하세요");
                }

                break;
        }

        /*출발지 값 받아오는곳*/
        if(requestCode==GET_ADDRESS_REQUEST_CODE && resultCode == Activity.RESULT_OK){


            String result_msg = data.getStringExtra("result_msg");
            //et_search_depart.setText(result_msg);
            Toast.makeText(mActivity, result_msg, Toast.LENGTH_SHORT).show();

            String juso = result_msg;
            String addr = "http://maps.googleapis.com/maps/api/geocode/json?address="
                    + juso.replaceAll(" ", "%20") + "&sensor=false";

            new HttpReqTask().execute("", "", addr);
        }
    }

    /*-------------------------------------------주소->좌표---------------------------------------------------------------------------------------*/
    private class HttpReqTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... arg) {
            String response = "";
            if( arg.length == 1 ) {
                return (String) HttpAssoci_Func.getHttpConnResult(arg[0]);
            }

            response = HttpAssoci_Func.getHttpClientResult(arg[ arg.length-1 ]);
            if( arg.length == 3 ) {
                try {
                    JSONObject jOb = new JSONObject(response).getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                    lat = Double.parseDouble(jOb.getString("lat"));
                    lng = Double.parseDouble(jOb.getString("lng"));
                    response = lat + " / " + lng;
                }
                catch(JSONException e) {
                    response = "에러다";
                    Log.d("tag", "Parse Error");
                }
            }

            return response;
        }

        protected void onPostExecute(String result) {
            tv_helpingText.setText(result); // result 는 좌표
            setFocusStartLocation(lat, lng);
            // marker 표시
            // market 의 위치, 타이틀, 짧은설명 추가 가능.
    /*                String markerTitle="";
                    if(markername.equals("출발지")){
                        //출발지 이름 표시
                        markerTitle = add_driver_from.getText().toString();
                        startingPoint =  new MapLocaInfo(markername, markerTitle, lat, lng);
                    }
                    else{//도착지
                        markerTitle = add_driver_to.getText().toString();
                        arrivingPoint =  new MapLocaInfo(markername, markerTitle, lat, lng);
                    }

                    MarkerOptions marker = new MarkerOptions();
                    marker .position(new LatLng(lat, lng))
                            .title(markerTitle)
                            .snippet(markername);
                    mMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(
                            new LatLng(lat, lng)
                    ));*/

        }
    }
    /*-------------------------------------------주소->좌표---------------------------------------------------------------------------------------*/

    /*-------------------------------------------OnCameraMoveListener---------------------------------------------------------------------------------------*/
    @Override
    public void onCameraMove() {
        if(start_arrive_done){ // 출발지 도착지 지정 완료일 때
            actv_search_arrive.setTextColor(Color.parseColor("#d4e6213b"));
            actv_search_start.setTextColor(Color.parseColor("#000000"));
            tv_helpingText.setVisibility(View.GONE);
        }
        else{
            if(start_arrive){
                actv_search_start.setText("위치 검색 중"); // 출발지 검색일때
                btn_select_start.setVisibility(View.INVISIBLE);
            }
            else {
                actv_search_arrive.setText("위치 검색 중"); // 도착지 검색일때
                btn_select_arrive.setVisibility(View.INVISIBLE);
            }

            tv_helpingText.setText("위치를 찾고 있습니다..");
        }



    }
     /*-------------------------------------------OnCameraMoveListener---------------------------------------------------------------------------------------*/

    /*-------------------------------------------OnCameraIdleListener---------------------------------------------------------------------------------------*/
    @Override
    public void onCameraIdle() {
        mLatitude=mGoogleMap.getCameraPosition().target.latitude+"";
        mLongitude=mGoogleMap.getCameraPosition().target.longitude+"";
        String address_name = getCurrentAddress2(mLatitude,mLongitude);

        String[] arr_address = address_name.split(" ");
        int arr_length = arr_address.length;

        if(start_arrive_done){ // 출발지 도착지 지정 완료일 때
            actv_search_arrive.setTextColor(Color.parseColor("#d4e6213b"));
            actv_search_start.setTextColor(Color.parseColor("#000000"));
           /* tv_helpingText.setVisibility(View.VISIBLE);
            tv_helpingText.setText("운전자 검색 버튼을 누르면 연락가능한 운전자를 보여줍니다");*/
        }

        else{// 완료 아닐 때
            if(start_arrive){ // 출발지 검색일 때
                if(arr_length<6 && arr_length>4)    actv_search_start.setText(arr_address[arr_length-3]+" "+arr_address[arr_length-2]+" "+arr_address[arr_length-1]);
                else if(arr_length>=6) {
                    actv_search_start.setText(arr_address[arr_length-4]+" "+arr_address[arr_length-3]+" "+arr_address[arr_length-2]+" "+arr_address[arr_length-1]);
                }
                else  actv_search_start.setText(address_name);
                btn_select_start.setVisibility(View.VISIBLE);
            }else{// 도착지 검색일 때
                if(arr_length<6 && arr_length>4)    actv_search_arrive.setText(arr_address[arr_length-3]+" "+arr_address[arr_length-2]+" "+arr_address[arr_length-1]);
                else if(arr_length>=6) actv_search_arrive.setText(arr_address[arr_length-4]+" "+arr_address[arr_length-3]+" "+arr_address[arr_length-2]+" "+arr_address[arr_length-1]);
                else  actv_search_arrive.setText(address_name);
                btn_select_arrive.setVisibility(View.VISIBLE);
            }

            tv_helpingText.setText(address_name);

        }




    }
    /*-------------------------------------------OnCameraIdleListener---------------------------------------------------------------------------------------*/
}
