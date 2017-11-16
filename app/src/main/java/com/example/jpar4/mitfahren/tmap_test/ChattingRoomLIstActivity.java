package com.example.jpar4.mitfahren.tmap_test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.jpar4.mitfahren.ItemView.ItemChattingListView;
import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.model.Item_ChattingRoomList;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChattingRoomLIstActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "ChattingRoomLIstActivity";
    Context context;
    Activity mActivity;
    Myapp app;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView listView_chattingroomlist; // 리스트뷰 선언
    RoomListAdpater roomListAdpater; //대화상대 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room_list);
        context=getApplicationContext();
        app = (Myapp)getApplicationContext();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setOnRefreshListener(this);

        listView_chattingroomlist = (ListView)findViewById(R.id.listView_chattingroomlist); // 현재 대화 상대

        //대화상대 어댑터 등록
        roomListAdpater = new RoomListAdpater();
        listView_chattingroomlist.setOnItemClickListener(this);
        listView_chattingroomlist.setAdapter(roomListAdpater);

    /*    Intent intent = getIntent();
        //카풀 신청 정보에서 올때
        String carpool_ID = intent.getStringExtra("carpool_ID");
        carpool_ID = "51";*/
        //String chatter_email = "z@z.com";
        String chatter_email = app.getUser_email();
        GetRoomList task = new GetRoomList();
        task.execute(chatter_email);
    }

    @Override
    public void onRefresh() {
        roomListAdpater.clearItems();
/*데이터 추가*/
        String chatter_email = app.getUser_email();
        GetRoomList task = new GetRoomList();
        task.execute(chatter_email);
/*데이터 추가*/

        // 새로고침 완료
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(context, ""+position+roomListAdpater.items.get(position).getCarpool_id(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ChattingRoomLIstActivity.this, ChattingRoomActivity.class);
        intent.putExtra("carpool_ID",roomListAdpater.items.get(position).getCarpool_id());
        startActivity(intent);
    }

    /*------------------------------------------------------------------채팅창 어댑터-------------*/
    class RoomListAdpater extends BaseAdapter {
        ArrayList<Item_ChattingRoomList> items = new ArrayList<>(); // 아이템 보관하기 위한 소스


        public void clearItems(){
            items.clear();

        }
        @Override
        public int getCount() { // 리스트뷰가 어댑터한테 리스트 몇 개가지고 있냐고 물어보면 실행하는 메소드
            return items.size();
        }

        public void addItem(Item_ChattingRoomList item) { // 데이터를 추가해주는 메서드
            items.add(item);
            //  notifyDataSetChanged();
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
            final int mPosition = position;
            ItemChattingListView view = null;
            if (convertView == null) { // 뷰가 없으면 생성
                view = new ItemChattingListView(context);
            } else {// 재사용
                view = (ItemChattingListView) convertView;
            }

            Item_ChattingRoomList curItem = items.get(position);// 현재 아이템
            view.setItem_ChattingRoomList(curItem);
         /*   view.setItem_Invitable(curItem);
            if(isThereCheckBox){// true면 checkBox보이고 아니면 안보이게
                view.invitable_user_check.setVisibility(View.VISIBLE);
            }else{
                view.invitable_user_check.setVisibility(View.GONE);
            }*/


            return view;
        }
        /*-----------------------------------------------54321 내림차순정렬--------------------------------------------------------------------------*/
        public void idDescSort(){
            Comparator<Item_ChattingRoomList> idDesc = new Comparator<Item_ChattingRoomList>() {
                @Override
                public int compare(Item_ChattingRoomList item1, Item_ChattingRoomList item2) {
                    int ret = 0 ;

                    if (Integer.parseInt(item1.getCarpool_id()) < Integer.parseInt(item2.getCarpool_id()))
                        ret = 1 ;
                    else if (Integer.parseInt(item1.getCarpool_id()) ==  Integer.parseInt(item2.getCarpool_id()))
                        ret = 0 ;
                    else
                        ret = -1 ;

                    return ret ;
                    //-1이랑 1이랑 바꾸면 내림차순으로 바뀜
                }
            };
            Collections.sort(items, idDesc) ;
        }
    }
    /*------------------------------------------------------------------채팅창 어댑터-------------*/
              /*-----------------------------------------------------------------------디비에서 채팅방 정보를 가져옮-----------------------------------------------------------------------*/

    class GetRoomList extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
/*
            progressDialog = ProgressDialog.show(context,
                    "잠시만 기다려 주세요.", null, true, true);*/
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

         /*   progressDialog.dismiss();*/
            /*result로 JsonArray를 받아와서 Json으로 하나씪 분리해서 값을 뽑아야됨*/
            try{
                //JSONObject obj = new JSONObject(result);
                JSONArray arr = new JSONArray(result);
                ArrayList<Item_ChattingRoomList> itemList = new ArrayList<>();

                for (int i = 0; i < arr.length(); i++)
                {
                    Item_ChattingRoomList item = new Item_ChattingRoomList();
                    item.setCarpool_id(arr.getJSONObject(i).getString("carpool_id")); // carpool_id
                    item.setMaster_email(arr.getJSONObject(i).getString("master_email")); // item_id
                    item.setRoom_title(arr.getJSONObject(i).getString("room_title")); //
                    item.setCar_pic(arr.getJSONObject(i).getString("car_pic"));
                    item.setRoom_num(arr.getJSONObject(i).getString("poeple_num"));

                    roomListAdpater.addItem(item);
                }
                Log.e("ddd","???");
                roomListAdpater.idDescSort();
                roomListAdpater.notifyDataSetChanged();


            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String chatter_email = (String) params[0];
            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/getChattingRoomList.php";
            String postParameters = "chatter_email=" + chatter_email;


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

              //  Log.d(TAG, "EmailCheck: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
    /*-----------------------------------------------------------------------디비에서 채팅방 정보를 가져옮-----------------------------------------------------------------------*/
}
