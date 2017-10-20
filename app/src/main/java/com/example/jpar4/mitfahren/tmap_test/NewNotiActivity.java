package com.example.jpar4.mitfahren.tmap_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.jpar4.mitfahren.ItemView.ItemNotiView;
import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.model.Item_Notification;
import com.example.jpar4.mitfahren.service.MyService;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewNotiActivity extends AppCompatActivity {
    private static final String TAG = "Notification_Activity";
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    MyService mService; //서비스객체
    boolean mBound;
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    ListView listView;
    NotiAdapter adapter;
    Intent intent;
    Context mContext;

    /*app객체 (로그인에 사용)*/
    Myapp app;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_noti);
        /*app객체*/
        app = (Myapp)getApplicationContext();
        mContext=getApplicationContext();
        intent = getIntent();
        String msg = intent.getStringExtra("msg");

      /*-----------------------------------------------------------------------디비에서 노티 정보를 가져옮-----------------------------------------------------------------------*/
        GetNotiInfo task = new GetNotiInfo();
        task.execute();
       /*-----------------------------------------------------------------------디비에서 노티 정보를 가져옮-----------------------------------------------------------------------*/

      /*-----------------------------------------------------------------------액티비티 리스트뷰 기본 세팅-----------------------------------------------------------------------*/
        listView = (ListView) findViewById(R.id.listView);
        adapter = new NotiAdapter();
        listView.setOnItemClickListener(adapter);
        listView.setAdapter(adapter); // 리스트에 어댑터를 추가
      /*-----------------------------------------------------------------------액티비티 리스트뷰 기본 세팅-----------------------------------------------------------------------*/
    }

    class NotiAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
        ArrayList<Item_Notification> items = new ArrayList<>(); // 아이템 보관하기 위한 소스
        ArrayList<Item_Notification> items2 = new ArrayList<>();


        @Override
        public int getCount() { // 리스트뷰가 어댑터한테 리스트 몇 개가지고 있냐고 물어보면 실행하는 메소드
            return items.size();
        }

        public void addItem(Item_Notification item) { // 데이터를 추가해주는 메서드
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

            ItemNotiView view = null;
            if (convertView == null) { // 뷰가 없으면 생성
                view = new ItemNotiView(getApplicationContext());
            } else {// 재사용
                view = (ItemNotiView) convertView;
            }

            Item_Notification curItem = items.get(position);// 현재 아이템
            view.setItem_notitication(curItem);
            //view.deliveryService(mService);
            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e("ddd", "리스트포지션"+position+" 아이디 "+id);
            ItemNotiView view2 =(ItemNotiView)getView(position,view,parent);
            view2.setItemBackgrouncColorWhite();
            intent = new Intent(mContext,  NewDriverInfoActivity.class);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를없앤다.
            intent.putExtra("carpool_ID", items.get(position).getCarpool_ID());
          /*  intent.putExtra("data", item_new_driver_info);
            Log.e("ddd driver_info", item_new_driver_info.getUser_email().toString());
            Log.e("ddd driver_info", item_new_driver_info.getUser_start_date().toString());*/
            mContext.startActivity(intent);

        }
    }

    /*-----------------------------------------------------------------------디비에서 노티 정보를 가져옮-----------------------------------------------------------------------*/
    class GetNotiInfo extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(NewNotiActivity.this,
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
                ArrayList<Item_Notification> itemList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++)
                {
                    /*로그인 아이디랑 리시버 아이디가 같은 경우만 표시*/
                    if(arr.getJSONObject(i).getString("receiver_email").equals(app.getUser_email())){
                        Item_Notification item = new Item_Notification();
                        if(arr.getJSONObject(i).getInt("msg_type")==1) {
                            item.setCarpool_ID(arr.getJSONObject(i).getString("carpool_id"));
                            item.setNoti_title("카풀신청");
                            item.setNoti_content(arr.getJSONObject(i).getString("user_name")+"님이 카풀을 신청하셨습니다.");
                            item.setNoti_profile_pic(arr.getJSONObject(i).getString("user_photo"));
                        }
                        adapter.addItem(item);
                    }



/*                    item.setUser_email(arr.getJSONObject(i).getString("user_email"));
                    item.setUser_start_date(arr.getJSONObject(i).getString("user_start_date"));
                    item.setUser_start_time(arr.getJSONObject(i).getString("user_start_time"));*/
                    //명수 대신 삽입해서 확인해보기
                    //item.setUser_with_poeple(출발지로부터거리.toString());

                    // itemList.add(item);
                   // adapter.addItem(item);

                    //JSONObject obj = new JSONObject(arr.get(i).toString());
                    //  Log.e("json"+i, arr.get(i).toString());


                    //Log.e("ddd json"+i, arr.getJSONObject(i).toString());
                   // Log.e("ddd user_email", arr.getJSONObject(i).getString("user_email")) ;
                }
                /*--------------------------------------------------------------------------------------------------------------------정렬---------------------------------------------------------------------------------------------------------------------------*/
                /*--------------------------------------------------------------------------------------------------------------------정렬------------------------------------------------------------------------------------------------------------------------------*/
                adapter.notifyDataSetChanged();

                Log.e("ddd",arr.get(0).toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            //  String user_email = (String) params[0];

            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/carpool_apply_join_user_info_new.php";
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
    /*-----------------------------------------------------------------------디비에서 노티 정보를 가져옮-----------------------------------------------------------------------*/
}
