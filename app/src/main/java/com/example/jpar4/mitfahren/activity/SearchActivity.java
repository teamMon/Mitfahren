package com.example.jpar4.mitfahren.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.adapter.DriverSearchAdapter;
import com.example.jpar4.mitfahren.model.Item_driver_info;
import com.example.jpar4.mitfahren.test_activity.DaumTestActivity;
import com.example.jpar4.mitfahren.test_activity.GoogleMapTestActivity;
import com.example.jpar4.mitfahren.test_activity.MapTestActivity;
import com.example.jpar4.mitfahren.test_activity.PlaceAutoActivity;
import com.example.jpar4.mitfahren.test_activity.TestHttpActivity;
import com.example.jpar4.mitfahren.test_activity.TestSearchActivity;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    PopupWindow mPopupWindow;
    ListView driver_info_list;
    ArrayList<Item_driver_info> arr_Item_driver_info;
    ImageButton button_driver_search_setting;
    EditText et_search_depart;

    /*주소받아오기 위한 REQUESTCODE*/
    private static final int GET_ADDRESS_REQUEST_CODE = 3001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_search);*/
        setContentView(R.layout.activity_drawer);

        /*에딧트 텍스트 누르면 주소 검색 액티비티로 넘어갈 수 있게 구성*/
        et_search_depart = (EditText) findViewById(R.id.et_search_depart);
        et_search_depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, TestSearchActivity.class);
                //startActivity(intent);

                startActivityForResult(intent, GET_ADDRESS_REQUEST_CODE); //
            }
        });
         /*에딧트 텍스트 누르면 주소 검색 액티비티로 넘어갈 수 있게 구성*/



        /*네비 드로워*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*네비 드로워*/

        button_driver_search_setting = (ImageButton) findViewById(R.id.btn_driver_search_setting);
        button_driver_search_setting.setOnClickListener(onClickListener);



        Spinner spinner = (Spinner)findViewById(R.id.spinner_search_option);

        arr_Item_driver_info = new ArrayList<>();
        Item_driver_info item_driver_info;

        for(int i =0; i<20;i++) {
            item_driver_info = new Item_driver_info("홍길동"+i, "인천", "강남", "9:00", 0);
            arr_Item_driver_info.add(item_driver_info);
        }

        DriverSearchAdapter driverSearchAdapter = new DriverSearchAdapter(this, R.layout.item_driver_info, arr_Item_driver_info);


        driver_info_list = (ListView) findViewById(R.id.driver_search_list);
        driver_info_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(SearchActivity.this, "!!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, CarpoolPageActivity.class);
                startActivity(intent);
            }
        });
        driver_info_list.setAdapter(driverSearchAdapter);


    }

    View.OnClickListener onClickListener = new Button.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_driver_search_setting:
              //      Toast.makeText(getApplicationContext(), "!!!!", Toast.LENGTH_SHORT).show();
                View popupView = getLayoutInflater().inflate(R.layout.popup_setting_menu, null);
                mPopupWindow = new PopupWindow(popupView);
                mPopupWindow.setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
//팝업 터치 가능
                mPopupWindow.setTouchable(true);
//팝업 외부 터치 가능(외부 터치시 나갈 수 있게)
                mPopupWindow.setOutsideTouchable(true);
//외부터치 인식을 위한 추가 설정 : 미 설정시 외부는 null로 생각하고 터치 인식 X
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//애니메이션 활성화
                mPopupWindow.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
//한가운데 팝업 생성
                //mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
//아니면
//버튼에서 (Xoffset,Yoffset)만큼 떨어진 데 생성

                mPopupWindow.showAsDropDown(button_driver_search_setting, 20, 20);

                    break;
            }
        }
    };


    /*네비 메뉴*/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.right_corner_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myinfo) {
            // Handle the camera action
        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_add_driver) {
            Intent intent = new Intent(SearchActivity.this, AddDriverActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_see_friend) {

        } else if (id == R.id.nav_talk) {

        } else if (id == R.id.nav_noti) {

        } else if (id == R.id.nav_logout) {

        }else if (id == R.id.nav_out_mem) {

        }else if (id == R.id.nav_login) {
            Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_join) {
            Intent intent = new Intent(SearchActivity.this, JoinActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_testmap1) {//맵현재위치 찍기
            Intent intent = new Intent(SearchActivity.this, MapTestActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_testmap2) {//다음 주소 검색
            Intent intent = new Intent(SearchActivity.this, TestSearchActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.maptest1) {//다음 맵뷰
            Intent intent = new Intent(SearchActivity.this, DaumTestActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.maptest2) {//구글 맵placeAPI
            Intent intent = new Intent(SearchActivity.this, PlaceAutoActivity.class); //자동완성
            startActivity(intent);
        }
        else if (id == R.id.maptest3) {//위도 경도 변환
            Intent intent = new Intent(SearchActivity.this, GoogleMapTestActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.maptest4) {//http통신연습
            Intent intent = new Intent(SearchActivity.this, TestHttpActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.maptest5) {

        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
     /*네비 메뉴*/

     /*출발지 도착지 값 받아오는곳*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*출발지 값 받아오는곳*/
        if(requestCode==GET_ADDRESS_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            String result_msg = data.getStringExtra("result_msg");
            et_search_depart.setText(result_msg);
        }
    }
}
