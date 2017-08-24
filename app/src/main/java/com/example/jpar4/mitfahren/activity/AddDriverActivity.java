package com.example.jpar4.mitfahren.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.func.HttpAssoci_Func;
import com.example.jpar4.mitfahren.test_activity.TestSearchActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class AddDriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    EditText add_driver_from;
    TextView mTextMessage;
    /* 임시 나중에 response 잘라서 처리해야함*/
    float lat ;
    float lng ;

    /*주소받아오기 위한 REQUESTCODE*/
    private static final int GET_ADDRESS_REQUEST_CODE = 3001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        add_driver_from = (EditText) findViewById(R.id.add_driver_from);

        //출발지 에딧 텍스트
        add_driver_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDriverActivity.this, TestSearchActivity.class);
                //startActivity(intent);

                startActivityForResult(intent, GET_ADDRESS_REQUEST_CODE); //
            }
        });

        //http연결됬는지 텍스트뷰에 표시
        mTextMessage = (TextView)findViewById(R.id.textMessage);
        if(HttpAssoci_Func.isNetConnect(this.getApplicationContext()) )
            mTextMessage.setText("Network is connected");
        else
            mTextMessage.setText("Network is disconnected");




        /*프레그먼트매니저 불러옴*/
        FragmentManager fragmentManager = getFragmentManager();
        /*프레그먼트에 레이아웃에 있는 map 연결*/
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        /*이거 this= maybe MapTestActivity? 이거 지금 보고 있는거 연동시켜주는건가? 잘 모름..*/
        mapFragment.getMapAsync(this);

        // 과거에는 GoogleMap 객체를 얻어오기 위해
        // mapFragment.getMap() 메소드를 사용했으나
        // 현재 getMap() 은 deprecate 되었습니다.
        // 지금은 위와 같이 getMapAsync() 를 하고
        // 아래의 OnMapReady() 에서 처리를 GoogleMap 객체관련
        // 처리를 합니다.

    }

    /**
     * OnMapReady 는 map이 사용가능하면 호출되는 콜백 메소드입니다
     * 여기서 marker 나 line, listener, camera 이동 등을 설정해두면 됩니다.
     * 이번 예제에서는 서울역 근처에 marker를 더하고 적절한 title과, zoom을 설정해둡니다
     * 이 시점에서, 만약 사용자 기기게 Google Play service가 설치되지 않으면
     * 설치하라고 메세지가 뜨게 되고,  설치후에 다시 이 앱으로 제어권이 넘어옵니다
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // ↑매개변수로 GoogleMap 객체가 넘어옵니다.
        mMap = googleMap;
        // camera 좌쵸를 서울역 근처로 옮겨 봅니다.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(37.555744, 126.970431)   // 위도, 경도
        ));

        // 구글지도(지구) 에서의 zoom 레벨은 1~23 까지 가능합니다.
        // 여러가지 zoom 레벨은 직접 테스트해보세요
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        googleMap.animateCamera(zoom);   // moveCamera 는 바로 변경하지만,
        // animateCamera() 는 근거리에선 부드럽게 변경합니다

        // marker 표시
        // market 의 위치, 타이틀, 짧은설명 추가 가능.
        MarkerOptions marker = new MarkerOptions();
        marker .position(new LatLng(37.555744, 126.970431))
                .title("서울역")
                .snippet("Seoul Station");
        googleMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력

        // 마커클릭 이벤트 처리
        // GoogleMap 에 마커클릭 이벤트 설정 가능.
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 마커 클릭시 호출되는 콜백 메서드
                Toast.makeText(getApplicationContext(),
                        marker.getTitle() + " 클릭했음"
                        , Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /*출발지 도착지 값 받아오는곳*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*출발지 값 받아오는곳*/
        if(requestCode==GET_ADDRESS_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            String result_msg = data.getStringExtra("result_msg");
            add_driver_from.setText(result_msg);

            String juso = result_msg;
            String addr = "http://maps.googleapis.com/maps/api/geocode/json?address="
                    + juso.replaceAll(" ", "%20") + "&sensor=false";

             new HttpReqTask().execute("", "", addr);
            //    mTextMessage.setText(addr);
        }
    }

    private class HttpReqTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... arg) {
            String response = "";
            if( arg.length == 1 ) {
                return (String)HttpAssoci_Func.getHttpConnResult(arg[0]);
            }

            response = HttpAssoci_Func.getHttpClientResult(arg[ arg.length-1 ]);
            if( arg.length == 3 ) {
                try {
                    JSONObject jOb = new JSONObject(response).getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
               /*     String lat = jOb.getString("lat");
                    String lng = jOb.getString("lng");*/
                    /*임시용*/
                    lat = Float.parseFloat(jOb.getString("lat"));
                    lng = Float.parseFloat(jOb.getString("lng"));
                    response = lat + " / " + lng;
                }
                catch(JSONException e) {
                    Log.d("tag", "Parse Error");
                }
            }

            return response;
        }

        protected void onPostExecute(String result) {
            mTextMessage.setText(result);
            // marker 표시
            // market 의 위치, 타이틀, 짧은설명 추가 가능.
            MarkerOptions marker = new MarkerOptions();
            marker .position(new LatLng(lat, lng))
                    .title("위치이름")
                    .snippet("출발지");
            mMap.addMarker(marker).showInfoWindow(); // 마커추가,화면에출력
            mMap.moveCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(lat, lng)
            ));

        }
    }
}
