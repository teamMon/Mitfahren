package com.example.jpar4.mitfahren.tmap_test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import com.example.jpar4.mitfahren.service.MyService;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
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

    /*메시지처리핸들러*/
    Handler chattinghandler;

    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    SQLiteDatabase sqliteDB;
    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
        /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    MyService mService; //서비스객체
    boolean mBound;
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room_list);
        context=getApplicationContext();
        app = (Myapp)getApplicationContext();
        chattinghandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1001) { //에코체팅
                    String carpool_id = msg.getData().getString("carpool_id");
                //    Log.e("ddd handleMessage","여기 오나요??");
                    for(int i=0; i< roomListAdpater.getCount();i++){
            //            Log.e("handleMessage conut", ""+i);
                       if(((Item_ChattingRoomList)roomListAdpater.getItem(i)).getCarpool_id().equals(carpool_id)){
               //            Log.e("handleMessage item", ""+((Item_ChattingRoomList)roomListAdpater.getItem(i)).getCarpool_id().equals(carpool_id));
                           ((Item_ChattingRoomList)roomListAdpater.getItem(i)).setNum_unread_msg(get_unread_num(carpool_id));
                       }
                    }
                    roomListAdpater.notifyDataSetChanged();
                }
            }
        };


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
        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
        sqliteDB = init_database();
        init_tables();
        //load_msg();

        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
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
        /*d없*/
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
                    item.setNum_unread_msg(get_unread_num(arr.getJSONObject(i).getString("carpool_id")));

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

    @Override
    protected void onPause() {
        super.onPause();
/*        for(int i=0; i< roomListAdpater.getCount();i++){
           // Log.e("ddd dddx", ""+((Item_ChattingRoomList)roomListAdpater.getItem(i)).getCarpool_id());
                ((Item_ChattingRoomList)roomListAdpater.getItem(i)).setNum_unread_msg(get_unread_num(((Item_ChattingRoomList)roomListAdpater.getItem(i)).getCarpool_id()));
        }
        roomListAdpater.notifyDataSetChanged();*/
    }

    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private SQLiteDatabase init_database() {
        Log.e("ddd", "데이터베이스세팅");
        SQLiteDatabase db = null ;
        // File file = getDatabasePath("chatting.db");
        File file = new File(getFilesDir(), "chatting.db");
        Log.e("ddd PATH : ", file.toString());

        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null) ;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (db == null) {
            Log.e("ddd DB creation failed", file.getAbsolutePath().toString()) ; }
        return db ;
    }

    private void init_tables() {
 /*       sendMsgObj.put("kindOfmsg", "2"); //    kindOfmsg : 1 = carpool_applying ; 2 : chatting
        sendMsgObj.put("carpool_id", carpool_ID); // 등록된 카풀정보의 아이디(유일값), 방번호 어떻게 할지 생각해보고
        //  sendMsgObj.put("sender", app.getUser_email()); // sender : 보내는 사람
        sendMsgObj.put("sender", app.getUser_email());
        sendMsgObj.put("sender_name", app.getUser_name());
        sendMsgObj.put("sender_pic", app.getUser_photo());
        //   sendMsgObj.put("receiver", item_new_driver_info.getUser_email()); // receiver : 받는 사람
        //   sendMsgObj.put("receiver", "z@z.com");
        sendMsgObj.put("contents", sent_text);*/

        if (sqliteDB != null) {
     /*       String sqlDelete = "DELETE FROM CHATTING_TABLE" ;
            sqliteDB.execSQL(sqlDelete) ;*/
/*            String sqlDropTbl = "DROP TABLE CHATTING_TABLE" ;
            sqliteDB.execSQL(sqlDropTbl) ;*/


            String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS CHATTING_TABLE (" +
                    "NO "           + "INTEGER PRIMARY KEY NOT NULL," +
                    "MSG_TYPE "         + "TEXT," +
                    "CARPOOL_ID "         + "TEXT," +
                    "SENDER "         + "TEXT," +
                    "SENDER_NAME "         + "TEXT," +
                    "SENDER_PIC "        + "TEXT," +
                    "TIME "        + "TEXT," +
                    "IS_READ "        + "TEXT," +
                    "CONTENTS "       + "TEXT," +
                    "IMG_MSG "        + "TEXT" +")" ;
            //    "OVER20 "       + "INTEGER" + ")" ;

            System.out.println(sqlCreateTbl) ;
            Log.e("ddd sqlCreateTbl", sqlCreateTbl);
            sqliteDB.execSQL(sqlCreateTbl) ;
        }
    }
    private String get_unread_num(String carpool_id) {

        // String sqlInsert = "INSERT INTO CONTACT_T " + "(NO, NAME, PHONE, OVER20) VALUES (" + Integer.toString(no) + "," + "'" + name + "'," + "'" + phone + "'," + ((isOver20 == true) ? "1" : "0") + ")" ; System.out.println(sqlInsert) ; sqliteDB.execSQL(sqlInsert) ;
        //  String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS) VALUES ('2', '63', 'z@z.com', '유아인', 'z@zcom/z.gif', '12:11', 'Y', '하...')" ;
        // Log.e("ddd", sqlInsert);
        //  sqliteDB.execSQL(sqlInsert) ;

String unread_num="0";

        if (sqliteDB != null) {
            String sqlQueryTbl = "SELECT count(*) FROM CHATTING_TABLE where IS_READ='Y' and CARPOOL_ID="+"'"+carpool_id+"'" ;
            Cursor cursor = null ;

            // 쿼리 실행
            cursor = sqliteDB.rawQuery(sqlQueryTbl, null);

            while (cursor.moveToNext()) {
                    // Log.e("ddd select문", cursor.getString(0)+cursor.getString(1)+cursor.getString(2)+cursor.getString(3)+cursor.getString(4)+cursor.getString(5)+cursor.getString(7)+cursor.getString(8)+cursor.getString(9));
                Log.e("ddd count문", cursor.getString(0));
                unread_num = cursor.getString(0);
/*                if(carpool_ID.equals(cursor.getString(2))){ // 방번호가 같은때만 채팅 표시해줌
                    Item_Chatting item = new Item_Chatting();
                    if(app.getUser_email().equals(cursor.getString(3))) {// 보낸분자
                        if(cursor.getString(9).equals("NO_IMG")){ // 문자메시지 일때
                            item.setSent_time(cursor.getString(6));
                            item.setSent_content(cursor.getString(8));
                            item.setSent(true);
                            chatting_adapter.addItem(item);
                        }else{// 이미지 메시지 일때, 띠용
                            item.setSent_time(cursor.getString(6));
                            item.setImg_file_name(cursor.getString(9));
                            item.setSent(true);
                            //chatting_display.setTranscriptMode( ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL );
                            chatting_adapter.addItem(item);
                        }
                        // item.setMsg_date(sent_date);

                    }else{//받은문자
                        if(cursor.getString(9).equals("NO_IMG")) { // 받은 문자메시지 일때
                            //                item.setMsg_date(received_date);
                            item.setCarpool_id(cursor.getString(2));
                            item.setSender_name(cursor.getString(4));
                            item.setSender_pic(cursor.getString(5));
                            item.setReceived_time(cursor.getString(6));
                            item.setReceived_content(cursor.getString(8));
                            item.setSent(false);
                            chatting_adapter.addItem(item);
                        }
                        else{ //받은 이미지 메시지 일때
                            item.setCarpool_id(cursor.getString(2));
                            item.setSender_name(cursor.getString(4));
                            item.setSender_pic(cursor.getString(5));
                            item.setReceived_time(cursor.getString(6));
                            item.setReceived_content(cursor.getString(8));
                            item.setImg_file_name(cursor.getString(9)); // 서버의 이미지 주소 입력
                            item.setSent(false);
                            chatting_adapter.addItem(item);
                        }

                    }
                }*/
            }
        }
        return unread_num;
    }
    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            mService.registerCallback(mCallback); //콜백 등록
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    //서비스에서 아래의 콜백 함수를 호출하며, 콜백 함수에서는 액티비티에서 처리할 내용 입력
    private MyService.ICallback mCallback = new MyService.ICallback() {
        @Override
        public String getCarpool_ID() {
            return null;
        }

        @Override
        public void recvRejectCall(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents) {

        }

        @Override
        public void recvRoomID(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents) {

        }

        @Override
        public void changeUnreadMsgNum(String carpool_id) {
            Log.e("ddd changeUnreadMsgNum", " "+carpool_id);
            Message message= Message.obtain();
            message.what = 1001; // 안읽은 메시지수 표시
            Bundle bundle = new Bundle();
            //  bundle.putString("friendId",friendId);
            bundle.putString("carpool_id",carpool_id);
            //    bundle.putLong("time",time);
            message.setData(bundle);
            chattinghandler.sendMessage(message);
        }

        public void recvData(String txt) {
            Log.e("ddd", "ddd"+" "+txt);
            Message message= Message.obtain();
            message.what = 1; //에코채팅
            Bundle bundle = new Bundle();
            //  bundle.putString("friendId",friendId);
            bundle.putString("content",txt);
            //    bundle.putLong("time",time);
            message.setData(bundle);
           // chattinghandler.sendMessage(message);

        }
        //띠용
        public void recvChattingMsg(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents){ // 채팅 메시지 처리

            Log.e("ddd", "ddd"+" recvChattingMsg");
            Message message= Message.obtain();
            message.what = 2; //채팅
            Bundle bundle = new Bundle();
            //  bundle.putString("friendId",friendId);
            bundle.putString("carpool_id",carpool_id);
            bundle.putString("sender_email",sender_email);
            bundle.putString("sender_name",sender_name);
            bundle.putString("sender_pic",sender_pic);
            bundle.putString("contents",contents);

            //    bundle.putLong("time",time);
            message.setData(bundle);
            //chattinghandler.sendMessage(message);
        }

        @Override
        public void recvChattingImgMsg(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents, String img_file_name) { // 채팅 이미지 메시지 처리
            Log.e("ddd", "ddd"+" recvChattingImgMsg");
            Message message= Message.obtain();
            message.what = 3; //채팅이미지 메시지
            Bundle bundle = new Bundle();
            //  bundle.putString("friendId",friendId);
            bundle.putString("carpool_id",carpool_id);
            bundle.putString("sender_email",sender_email);
            bundle.putString("sender_name",sender_name);
            bundle.putString("sender_pic",sender_pic);
            bundle.putString("contents",contents);
            bundle.putString("img_file_name",img_file_name);

            //    bundle.putLong("time",time);
            message.setData(bundle);
           // chattinghandler.sendMessage(message);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
                for(int i=0; i< roomListAdpater.getCount();i++){
           // Log.e("ddd dddx", ""+((Item_ChattingRoomList)roomListAdpater.getItem(i)).getCarpool_id());
                ((Item_ChattingRoomList)roomListAdpater.getItem(i)).setNum_unread_msg(get_unread_num(((Item_ChattingRoomList)roomListAdpater.getItem(i)).getCarpool_id()));
        }
        roomListAdpater.notifyDataSetChanged();

        Intent intent = new Intent(this, MyService.class);
        // startService(intent);
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
