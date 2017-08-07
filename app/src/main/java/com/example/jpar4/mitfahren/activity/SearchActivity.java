package com.example.jpar4.mitfahren.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.adapter.DriverSearchAdapter;
import com.example.jpar4.mitfahren.model.Item_driver_info;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    PopupWindow mPopupWindow;
    ListView driver_info_list;
    ArrayList<Item_driver_info> arr_Item_driver_info;
    ImageButton button_driver_search_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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

}
