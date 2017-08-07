package com.example.jpar4.mitfahren.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.activity.CarpoolPageActivity;
import com.example.jpar4.mitfahren.model.Item_driver_info;

import java.util.ArrayList;

/**
 * Created by jpar4 on 2017-08-07.
 */

public class DriverSearchAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<Item_driver_info> arr_Item_driver_info;
    int layout_driver_info;

    public DriverSearchAdapter(Context context, int layout_driver_info, ArrayList<Item_driver_info> arr_Item_driver_info){
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arr_Item_driver_info = arr_Item_driver_info;
        this.layout_driver_info = layout_driver_info;
    }
    @Override
    public int getCount() {
        return arr_Item_driver_info.size();
    }

    @Override
    public Object getItem(int position) {
        return arr_Item_driver_info.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout_driver_info, parent, false);
        }

        TextView driver_name = (TextView) convertView.findViewById(R.id.tv_driver_name);
        driver_name.setText(arr_Item_driver_info.get(position).getDriver_name());
        TextView driver_from = (TextView) convertView.findViewById(R.id.tv_driver_from);
        driver_from.setText(arr_Item_driver_info.get(position).getDriver_from());
        TextView driver_to = (TextView) convertView.findViewById(R.id.tv_driver_to);
        driver_to.setText(arr_Item_driver_info.get(position).getDriver_to());
        TextView driver_start_time = (TextView) convertView.findViewById(R.id.tv_depart_time_value);
        driver_start_time.setText(arr_Item_driver_info.get(position).getDriver_start_time());
        TextView driver_carry_people_num = (TextView) convertView.findViewById(R.id.tv_driver_carry_people_num_value);
        driver_carry_people_num.setText(Integer.toString(arr_Item_driver_info.get(position).getDriver_carry_people_num()));

        /*
        * 버튼을 눌렀을 때 카풀요청을 누르면 카풀요청 쪽지보내는 창이 뜨고, 카풀 페이지로 이동을 누르면 카풀페이지로 이동할 수 있도록 함
        * 카풀페이지에서 백버튼 눌러서 돌아올 수 있도록함.
        *
        */
        ImageButton btn_driver_search = (ImageButton) convertView.findViewById(R.id.btn_driver_search);
        btn_driver_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*버튼눌렀을때 다이얼로그 뜨는 부분*/
                final CharSequence[] items = {"카풀 요청 쪽지보내기", "카풀 페이지로 이동"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);     // 여기서 this는 Activity의 this
                // 여기서 부터는 알림창의 속성 설정
                builder      // 제목 설정setTitle("색상을 선택하세요")
                        .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index){
                                if(index==0){//쪽지보내기

                                }
                                else{//페이지 이동
                                    Intent intent = new Intent(context, CarpoolPageActivity.class);
                                    context.startActivity(intent);
                                }
                                //Toast.makeText(context, items[index], Toast.LENGTH_SHORT).show();
                            }
                        });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
                /*버튼눌렀을때 다이얼로그 뜨는 부분*/
            }
        });

        return convertView;
    }


}
