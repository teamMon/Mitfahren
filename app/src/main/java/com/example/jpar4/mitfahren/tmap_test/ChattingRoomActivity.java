package com.example.jpar4.mitfahren.tmap_test;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.ItemView.ItemChatterView;
import com.example.jpar4.mitfahren.ItemView.ItemChattingInvitableView;
import com.example.jpar4.mitfahren.ItemView.ItemChattingView;
import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.model.Item_Chatting;
import com.example.jpar4.mitfahren.model.Item_Invitable;
import com.example.jpar4.mitfahren.service.MyService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChattingRoomActivity extends FragmentActivity implements View.OnClickListener{//  액션바를 없애기 위해 FragmentActivity로 바꿈
    private static final String TAG = "ChattingRoomActivity";
    Context context;
    Activity mActivity;
    Myapp app;
    ChatterAdapter chatter_adapter; //대화상대 어댑터
    InvitableAdapter invitable_adapter; //초대할사람 추가하는 어댑터
    ChattingAdapter chatting_adapter;// 채팅 메시지 출력하는 어댑터

    private final String[] navItems = {"Brown", "Cadet Blue", "Dark Olive Green", "Dark Orange", "Golden Rod"};
    private ListView chatting_people_list, invitable_poeple_list, chatting_display; // 리스트뷰 선언
    private FrameLayout flContainer;
    private DrawerLayout drawerLayout;
    /*채팅방 제목*/
    TextView chatting_room_title;

    /*채팅버튼 채팅창*/
    Button btn_text_send;
    EditText input_text;

    /*나가기버튼*/
    ImageButton chatting_room_exit;
    /*사진 이미지 추가*/
    ImageButton send_pic;

    /*드로워*/
    private View drawerView;
    private ImageButton btn_menu;

    /*채팅사람추가*/
    ImageButton btn_addadd;
    Button btn_add_chatter;
    boolean isThereCheckBox = false;

    /*메시지처리핸들러*/
    Handler chattinghandler;

    String carpool_ID="";

    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    MyService mService; //서비스객체
    boolean mBound;
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    SQLiteDatabase sqliteDB;
    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    /*----------------------------------------사진 앨범 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    ImageView iv_view;

    String mCurrentPhotoPath;
    Uri imageUri;
    Uri photoURI, albumURI;
    /*----------------------------------------사진 앨범 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void onBackPressed() {
 /*       if (drawerLayout.isDrawerOpen(lvNavList)) {
            drawerLayout.closeDrawer(lvNavList);
        } else {
            super.onBackPressed();
        }*/
        if (drawerLayout.isDrawerOpen(drawerView)) {
            drawerLayout.closeDrawer(drawerView);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chatting_room_activity);

        context=getApplicationContext();
        app = (Myapp)getApplicationContext();



        chattinghandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1) { //에코체팅
                    String content = msg.getData().getString("content");
                    Calendar calendar = Calendar.getInstance();
                    java.util.Date date = calendar.getTime();
                    String received_time = (new SimpleDateFormat("aa hh:mm ").format(date));
                    String received_date = (new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREA).format(date));
                    Item_Chatting item = new Item_Chatting();
                    item.setMsg_date(received_date);

                    item.setReceived_time(received_time);
                    item.setReceived_content(content);
                    item.setSent(false);
                    chatting_adapter.addItem(item);
                    chatting_adapter.notifyDataSetChanged();
                }

                if(msg.what == 2) { //그냥 채팅 String carpool_id, String sender_email, String sender_name, String sender_pic, String contents
                    String carpool_id = msg.getData().getString("carpool_id");
                    String sender_email = msg.getData().getString("sender_email");
                    String sender_name = msg.getData().getString("sender_name");
                    String sender_pic = msg.getData().getString("sender_pic");
                    String contents = msg.getData().getString("contents");
//carpool_ID 이년이 carpool_id이년과 같을 떄만 보여주기. 이게 방개념이 되려나>
                    if(carpool_ID.equals(carpool_id)){ // 방나눠짐. ㅋㅋㅋ
                        if(!app.getUser_email().equals(sender_email)){ // 보낸사람이랑 유저랑 다를때만 받기
                            chatting_display.setTranscriptMode( ListView.TRANSCRIPT_MODE_DISABLED ); // 받을때는 안움직이게
                            Calendar calendar = Calendar.getInstance();
                            java.util.Date date = calendar.getTime();
                            String received_time = (new SimpleDateFormat("aa hh:mm ").format(date));
                            String received_date = (new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREA).format(date));
                            Item_Chatting item = new Item_Chatting();
                            item.setMsg_date(received_date);

                            item.setCarpool_id(carpool_id);
                            item.setSender_name(sender_name);
                            item.setSender_pic(sender_pic);
                            item.setReceived_time(received_time);
                            item.setReceived_content(contents);
                            item.setSent(false);
                            chatting_adapter.addItem(item);
                            chatting_adapter.notifyDataSetChanged();

                            //receive_msg(String msg_type, String received_date, String carpool_id, String sender_email, String sender_name, String sender_pic, String received_time, String sent_text) ;
                            /*여기서 저장하면 받을때, 이 액티비티 아니면 저장 안함 따라서 서비스에서 저장하는게 굳일듯*/
                            //receive_msg( "2",  received_date,  carpool_id,  sender_email,  sender_name,  sender_pic,  received_time,  contents) ;
                        }
                    }
                }
            }
        };

        /*채팅방 제목*/
        chatting_room_title = (TextView) findViewById(R.id.chatting_room_title) ;
        chatting_room_title.setText(app.getUser_name());

        chatting_people_list = (ListView)findViewById(R.id.chatting_people_list); // 현재 대화 상대
        invitable_poeple_list  = (ListView)findViewById(R.id.invitable_poeple_list); // 초대 가능한 사람
        chatting_display = (ListView)findViewById(R.id.chatting_display); // 채팅 화면
        /*초대버튼*/
        btn_addadd = (ImageButton) findViewById(R.id.btn_addadd);
        btn_addadd.setOnClickListener(this);
        btn_add_chatter = (Button) findViewById(R.id.btn_add_chatter);
        btn_add_chatter.setOnClickListener(this);
        /*사진 이미지 전송*/
        send_pic = (ImageButton) findViewById(R.id.send_pic);
        send_pic.setOnClickListener(this);

        iv_view = (ImageView) findViewById(R.id.iv_view);


        /*채팅버튼 채팅창*/
        btn_text_send = (Button)findViewById(R.id.btn_text_send);
        btn_text_send.setOnClickListener(this);
        input_text = (EditText)findViewById(R.id.input_text);

        /*나가기 버튼*/
        chatting_room_exit = (ImageButton) findViewById(R.id.chatting_room_exit);
        chatting_room_exit.setOnClickListener(this);


        flContainer = (FrameLayout)findViewById(R.id.fl_activity_main_container);

        drawerLayout = (DrawerLayout)findViewById(R.id.dl_activity_main_drawer);
        drawerLayout.setDrawerListener(myDrawerListener);
        drawerView = (View)findViewById(R.id.drawer);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        btn_menu = (ImageButton)findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
                //drawerLayout.openDrawer(lvNavList);
                drawerLayout.openDrawer(drawerView);
            }
        });
/*채팅방 색바뀌는 어댑터 접어두고*/
 /*       chatting_people_list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        chatting_people_list.setOnItemClickListener(new DrawerItemClickListener());  */

        //대화상대 어댑터 등록
        chatter_adapter = new ChatterAdapter();
        chatting_people_list.setAdapter(chatter_adapter);

        //초대할 사람 등록 어댑터 등록
        invitable_adapter = new InvitableAdapter();
        invitable_poeple_list.setAdapter(invitable_adapter);


        //메시지 출력 어댑터 등록
        chatting_adapter = new ChattingAdapter();
        chatting_display.setAdapter(chatting_adapter);

        Intent intent = getIntent();
        //카풀 신청 정보에서 올때
        carpool_ID = intent.getStringExtra("carpool_ID");

        /*현재 대화 상대 불러오기*/
        GetCurrentChatter task1 = new GetCurrentChatter();
        task1.execute(carpool_ID);

        /*초대 가능한 사람 추가*/
        GetInvitableInfo task = new GetInvitableInfo();
        task.execute(carpool_ID);

        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
        sqliteDB = init_database();
        init_tables();
        load_msg();
        chatting_adapter.notifyDataSetChanged();
        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
        checkPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_pic:
               // input_text.setEnabled(false);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);

                final CharSequence[] items = {"앨범선택", "사진촬영", "취소"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

// 여기서 부터는 알림창의 속성 설정
                builder.setTitle("이미지전송")        // 제목 설정
                        .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index){
                                switch (index){
                                    case 0:
                                       // Toast.makeText(getApplicationContext(), items[index], Toast.LENGTH_SHORT).show();
                                        getAlbum();
                                        break;
                                    case 1:
                                        //Toast.makeText(getApplicationContext(), items[index], Toast.LENGTH_SHORT).show();
                                        captureCamera();
                                        break;
                                    case 2:
                                        break;
                                }

                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기

                break;

            case R.id.btn_addadd:
               isThereCheckBox=true;
                invitable_adapter.notifyDataSetChanged();
                btn_add_chatter.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_add_chatter:
                btn_add_chatter.setVisibility(View.INVISIBLE);
                isThereCheckBox=false;
                invitable_adapter.getCheckedItem();
                invitable_adapter.notifyDataSetChanged();
                chatter_adapter.notifyDataSetChanged();
                /*---------------------------------------------------------------로그인시 tcp통신 연결----------------------------------------------------------------------------------*/
                if(mBound) {
                  /*테스트용*/
               /*  String numberStr = String.valueOf(mService.showTheNumber());
                  Toast.makeText(app, numberStr, Toast.LENGTH_SHORT).show();*/
               /*TCP연결해보기*/
                Log.e("ddd", ">>>???");
            //        mService.setConnectionTCP("a@a.com");
                }
                /*---------------------------------------------------------------로그인시 tcp통신 연결----------------------------------------------------------------------------------*/
                break;

            case R.id.btn_text_send:
                String sent_text = input_text.getText().toString(); // 텍스트 가져오고
                input_text.setText(""); // 텍스트 지우고
                Calendar calendar = Calendar.getInstance();
                java.util.Date date = calendar.getTime();
                String sent_time = (new SimpleDateFormat("aa hh:mm ").format(date));
                String sent_date = (new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREA).format(date));
                Item_Chatting item = new Item_Chatting();
                //
                item.setMsg_date(sent_date);
                item.setSent_time(sent_time);
                item.setSent_content(sent_text);
                item.setSent(true);
                chatting_display.setTranscriptMode( ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL );
                chatting_adapter.addItem(item);
                chatting_adapter.notifyDataSetChanged();
                send_msg( "2",  sent_date,  sent_time,  sent_text); // sqLite에 채팅 메시지 저장

/*                SimpleDateFormat old_time_format = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat new_time_format = new SimpleDateFormat("aa hh:mm ");
                String new_time="";
                try{
                    Date start_time = old_time_format.parse(item.getUser_start_time());
                    new_time = new_time_format.format(start_time);

                }catch(ParseException e){
                    e.printStackTrace();
                }*/
                //Toast.makeText(context, sent_time+" / "+sent_date, Toast.LENGTH_SHORT).show();

     /*---------------------------------------------------------------tcp통신으로 메시지 전송----------------------------------------------------------------------------------*/
                JSONObject sendMsgObj = new JSONObject();
                try{
                        /*
                        * 보내는 정보
                        * 1. 메시지 타입 2. 보내는 사람 이메일 3. 받는 사람 이메일 4. 등록된 카풀정보의 아이디(유일값) 5. 탑승자 출발 도착 좌표
                        *
                        *
                        * */
                    //핵심꼬임
                    sendMsgObj.put("kindOfmsg", "2"); //    kindOfmsg : 1 = carpool_applying ; 2 : chatting
                    sendMsgObj.put("carpool_id", carpool_ID); // 등록된 카풀정보의 아이디(유일값), 방번호 어떻게 할지 생각해보고
                  //  sendMsgObj.put("sender", app.getUser_email()); // sender : 보내는 사람
                    sendMsgObj.put("sender", app.getUser_email());
                    sendMsgObj.put("sender_name", app.getUser_name());
                    sendMsgObj.put("sender_pic", app.getUser_photo());
                 //   sendMsgObj.put("receiver", item_new_driver_info.getUser_email()); // receiver : 받는 사람
                 //   sendMsgObj.put("receiver", "z@z.com");
                    sendMsgObj.put("contents", sent_text);


                    mService.sendMsg(sendMsgObj.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
   /*---------------------------------------------------------------tcp통신으로 메시지 전송----------------------------------------------------------------------------------*/

                break;

            case R.id.chatting_room_exit:
                finish();
                break;
        }
    }

    /*리스트뷰 리스너*/
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            switch (position) {
                case 0:
                    flContainer.setBackgroundColor(Color.parseColor("#A52A2A"));
                    break;

                case 1:
                    flContainer.setBackgroundColor(Color.parseColor("#5F9EA0"));
                    break;

                case 2:
                    flContainer.setBackgroundColor(Color.parseColor("#556B2F"));
                    break;

                case 3:
                    flContainer.setBackgroundColor(Color.parseColor("#FF8C00"));
                    break;

                case 4:
                    flContainer.setBackgroundColor(Color.parseColor("#DAA520"));
                    break;
            }
           // drawerLayout.closeDrawer(lvNavList);
            drawerLayout.closeDrawer(drawerView);
        }
    }
    DrawerLayout.DrawerListener myDrawerListener = new DrawerLayout.DrawerListener(){
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {

        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };
    /*------------------------------------------------------------------대화상대 어댑터-------------*/
    class ChatterAdapter extends BaseAdapter {
        ArrayList<Item_Invitable> items = new ArrayList<>(); // 아이템 보관하기 위한 소스


        public void clearItems() {
            items.clear();

        }

        @Override
        public int getCount() { // 리스트뷰가 어댑터한테 리스트 몇 개가지고 있냐고 물어보면 실행하는 메소드
            return items.size();
        }

        public void addItem(Item_Invitable item) { // 데이터를 추가해주는 메서드
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
            final int mPosition = position;
            ItemChatterView view = null;
            if (convertView == null) { // 뷰가 없으면 생성
                view = new ItemChatterView(context);
            } else {// 재사용
                view = (ItemChatterView) convertView;
            }

            Item_Invitable curItem = items.get(position);// 현재 아이템
            view.setItem_Invitable(curItem);
            view.invitable_user_check.setVisibility(View.GONE);


            return view;
        }
    }

    /*------------------------------------------------------------------대화상대 어댑터-------------*/
    /*------------------------------------------------------------------초대 가능한 사람 어댑터-------------*/
    class InvitableAdapter extends BaseAdapter {
        ArrayList<Item_Invitable> items = new ArrayList<>(); // 아이템 보관하기 위한 소스


        public void clearItems(){
            items.clear();

        }
        @Override
        public int getCount() { // 리스트뷰가 어댑터한테 리스트 몇 개가지고 있냐고 물어보면 실행하는 메소드
            return items.size();
        }

        public void addItem(Item_Invitable item) { // 데이터를 추가해주는 메서드
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
            final int mPosition = position;
            ItemChattingInvitableView view = null;
            if (convertView == null) { // 뷰가 없으면 생성
                view = new ItemChattingInvitableView(context);
            } else {// 재사용
                view = (ItemChattingInvitableView) convertView;
            }

            Item_Invitable curItem = items.get(position);// 현재 아이템
            view.setItem_Invitable(curItem);
            if(isThereCheckBox){// true면 checkBox보이고 아니면 안보이게
                view.invitable_user_check.setVisibility(View.VISIBLE);
            }else{
                view.invitable_user_check.setVisibility(View.GONE);
            }


            return view;
        }
        public void getCheckedItem(){
            String aaa="";
            int count = items.size();
            ArrayList<Item_Invitable> items2 = new ArrayList<>(); // 아이템 보관하기 위한 소스
            items2.addAll(items);
            for(int i=0; i<count; i++){
                if(items2.get(i).isItem_checked()){
//                    aaa = aaa+ items.get(i).getUser_name()+items.get(i).getUser_email()+i+" / ";
                    items.get(i).setItem_checked(false);
                    //items.get(i).carpool_id, items.get(i).user_email, items.get(i),user_name, items.get(i).user_pic
                    String change_room_title =chatting_room_title.getText()+", "+items.get(i).getUser_name();
                    PushInvite task = new PushInvite();
                    task.execute(items.get(i).getCarpool_id(), items.get(i).getUser_name(), items.get(i).getUser_email(), items.get(i).getUser_pic(), change_room_title);

                    chatting_room_title.setText(change_room_title);
                    chatter_adapter.addItem(items.get(i)); //추가후
                    items.remove(i); //삭제

                }
            }
       //     Toast.makeText(context, aaa, Toast.LENGTH_SHORT).show();

        }

/*
        public void hideCheckBox(){
            int count = items.size();
            for(int i=0; i<count; i++){
              getView()
            }
        }
*/

       /* public void idDescSort(){
            Comparator<Item_New_Driver_Info> idDesc = new Comparator<Item_New_Driver_Info>() {
                @Override
                public int compare(Item_New_Driver_Info item1, Item_New_Driver_Info item2) {
                    int ret = 0 ;

                    if (Integer.parseInt(item1.getItem_id()) < Integer.parseInt(item2.getItem_id()))
                        ret = 1 ;
                    else if (Integer.parseInt(item1.getItem_id()) ==  Integer.parseInt(item2.getItem_id()))
                        ret = 0 ;
                    else
                        ret = -1 ;

                    return ret ;
                    //-1이랑 1이랑 바꾸면 내림차순으로 바뀜
                }
            };
            Collections.sort(items, idDesc) ;
        }*/

    }
    /*------------------------------------------------------------------초대 가능한 사람 어댑터-------------*/
    /*------------------------------------------------------------------채팅창 어댑터-------------*/
    class ChattingAdapter extends BaseAdapter {
        ArrayList<Item_Chatting> items = new ArrayList<>(); // 아이템 보관하기 위한 소스


        public void clearItems(){
            items.clear();

        }
        @Override
        public int getCount() { // 리스트뷰가 어댑터한테 리스트 몇 개가지고 있냐고 물어보면 실행하는 메소드
            return items.size();
        }

        public void addItem(Item_Chatting item) { // 데이터를 추가해주는 메서드
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
            ItemChattingView view = null;
            if (convertView == null) { // 뷰가 없으면 생성
                view = new ItemChattingView(context);
            } else {// 재사용
                view = (ItemChattingView) convertView;
            }

            Item_Chatting curItem = items.get(position);// 현재 아이템
            view.setItem_Chatting(curItem);
         /*   view.setItem_Invitable(curItem);
            if(isThereCheckBox){// true면 checkBox보이고 아니면 안보이게
                view.invitable_user_check.setVisibility(View.VISIBLE);
            }else{
                view.invitable_user_check.setVisibility(View.GONE);
            }*/


            return view;
        }

        public void updatelist(){
            this.notifyDataSetChanged();
        }
    }
    /*------------------------------------------------------------------채팅창 어댑터-------------*/
    /*------------------------------------------------------------------현재 대화 상대 어댑터-------------*/
    /*------------------------------------------------------------------현재 대화 상대 어댑터-------------*/
          /*-----------------------------------------------------------------------디비에서 초대 가능자 정보를 가져옮-----------------------------------------------------------------------*/

    class GetInvitableInfo extends AsyncTask<String, Void, String> {
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
                ArrayList<Item_Invitable> itemList = new ArrayList<>();

                for (int i = 0; i < arr.length(); i++)
                {
                    Item_Invitable item = new Item_Invitable();
                    item.setCarpool_id(arr.getJSONObject(i).getString("carpool_id")); // carpool_id
                    item.setUser_email(arr.getJSONObject(i).getString("user_email")); // item_id
                    item.setUser_name(arr.getJSONObject(i).getString("user_name")); //
                    item.setUser_pic(arr.getJSONObject(i).getString("user_photo"));
                    Log.e("ddd", "dd"+item.getUser_pic()+item.getUser_email()+item.getUser_name());
                    // itemList.add(item);
                    invitable_adapter.addItem(item);
                }
                Log.e("ddd","???");
                invitable_adapter.notifyDataSetChanged();


            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String carpool_id = (String) params[0];
            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/carpool_apply_join_user_info_new_for_chatting.php";
            String postParameters = "carpool_id=" + carpool_id;


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
    /*-----------------------------------------------------------------------디비에서 초대 가능자 정보를 가져옮-----------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------초대하기 버튼 눌렀을 때-----------------------------------------------------------------------*/
    class PushInvite extends AsyncTask<String, Void, String> {
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
                ArrayList<Item_Invitable> itemList = new ArrayList<>();

                for (int i = 0; i < arr.length(); i++)
                {
                    Item_Invitable item = new Item_Invitable();
                    item.setUser_email(arr.getJSONObject(i).getString("user_email")); // item_id
                    item.setUser_name(arr.getJSONObject(i).getString("user_name")); //
                    item.setUser_pic(arr.getJSONObject(i).getString("user_photo"));
                    Log.e("ddd", "dd"+item.getUser_pic()+item.getUser_email()+item.getUser_name());
                    // itemList.add(item);
                    invitable_adapter.addItem(item);
                }
                Log.e("ddd","???");
                invitable_adapter.notifyDataSetChanged();


            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            /*carpool_id,chatter_name,chatter_email,chatter_pic*/
            String carpool_id = (String) params[0];
            String chatter_name = (String) params[1];
            String chatter_email = (String) params[2];
            String chatter_pic = (String) params[3];
            String room_title = (String) params[4];

            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/invitechatter.php";
           // String postParameters = "carpool_id=" + carpool_id;
            String postParameters = "carpool_id="+carpool_id+"&chatter_name="+chatter_name+"&chatter_email="+chatter_email+"&chatter_pic="+chatter_pic+"&room_title="+room_title;


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
    /*-----------------------------------------------------------------------초대하기 버튼 눌렀을 때-----------------------------------------------------------------------*/
     /*-----------------------------------------------------------------------현재 대화 상대 정보 가져오기-----------------------------------------------------------------------*/
    class GetCurrentChatter extends AsyncTask<String, Void, String> {
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
                ArrayList<Item_Invitable> itemList = new ArrayList<>();

                for (int i = 0; i < arr.length(); i++)
                {
                    Item_Invitable item = new Item_Invitable();
                    item.setUser_email(arr.getJSONObject(i).getString("chatter_email")); // item_id
                    item.setUser_name(arr.getJSONObject(i).getString("chatter_name")); //
                    item.setUser_pic(arr.getJSONObject(i).getString("chatter_pic"));
                    item.setCarpool_id(arr.getJSONObject(i).getString("carpool_id"));
                    chatting_room_title.setText(arr.getJSONObject(i).getString("room_title"));

                    chatter_adapter.addItem(item);

                }
                chatter_adapter.notifyDataSetChanged();


            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String carpool_id = (String) params[0];
            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/getcurrentchatter.php";
            String postParameters = "carpool_id=" + carpool_id;


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
     /*-----------------------------------------------------------------------현재 대화 상대 정보 가져오기-----------------------------------------------------------------------*/


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
        public void recvData(String txt) {
            Log.e("ddd", "ddd"+" "+txt);
            Message message= Message.obtain();
            message.what = 1; //에코채팅
            Bundle bundle = new Bundle();
          //  bundle.putString("friendId",friendId);
            bundle.putString("content",txt);
        //    bundle.putLong("time",time);
            message.setData(bundle);
            chattinghandler.sendMessage(message);

        }

        public void recvChattingMsg(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents){

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
            chattinghandler.sendMessage(message);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
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
            String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS CHATTING_TABLE (" +
                    "NO "           + "INTEGER PRIMARY KEY NOT NULL," +
                    "MSG_TYPE "         + "TEXT," +
                    "CARPOOL_ID "         + "TEXT," +
                    "SENDER "         + "TEXT," +
                    "SENDER_NAME "         + "TEXT," +
                    "SENDER_PIC "        + "TEXT," +
                    "TIME "        + "TEXT," +
                    "IS_READ "        + "TEXT," +
                    "CONTENTS "       + "TEXT" + ")" ;
                //    "OVER20 "       + "INTEGER" + ")" ;

            System.out.println(sqlCreateTbl) ;
            Log.e("ddd sqlCreateTbl", sqlCreateTbl);
            sqliteDB.execSQL(sqlCreateTbl) ;
        }
    }


    private void load_msg() {

       // String sqlInsert = "INSERT INTO CONTACT_T " + "(NO, NAME, PHONE, OVER20) VALUES (" + Integer.toString(no) + "," + "'" + name + "'," + "'" + phone + "'," + ((isOver20 == true) ? "1" : "0") + ")" ; System.out.println(sqlInsert) ; sqliteDB.execSQL(sqlInsert) ;
      //  String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS) VALUES ('2', '63', 'z@z.com', '유아인', 'z@zcom/z.gif', '12:11', 'Y', '하...')" ;
      // Log.e("ddd", sqlInsert);
      //  sqliteDB.execSQL(sqlInsert) ;



        if (sqliteDB != null) {
            String sqlQueryTbl = "SELECT * FROM CHATTING_TABLE" ;
            Cursor cursor = null ;

            // 쿼리 실행
            cursor = sqliteDB.rawQuery(sqlQueryTbl, null);

            while (cursor.moveToNext()) {
                Log.e("ddd select문", cursor.getString(0)+cursor.getString(1)+cursor.getString(2)+cursor.getString(3)+cursor.getString(4)+cursor.getString(5)+cursor.getString(7)+cursor.getString(8) );

                if(carpool_ID.equals(cursor.getString(2))){ // 방번호가 같은때만 채팅 표시해줌
                    Item_Chatting item = new Item_Chatting();
                    if(app.getUser_email().equals(cursor.getString(3))) {// 보낸분자
                       // item.setMsg_date(sent_date);
                        item.setSent_time(cursor.getString(6));
                        item.setSent_content(cursor.getString(8));
                        item.setSent(true);
                        chatting_adapter.addItem(item);
                    }else{//받은문자
                        //                item.setMsg_date(received_date);
                        item.setCarpool_id(cursor.getString(2));
                        item.setSender_name(cursor.getString(4));
                        item.setSender_pic(cursor.getString(5));
                        item.setReceived_time(cursor.getString(6));
                        item.setReceived_content(cursor.getString(8));
                        item.setSent(false);
                        chatting_adapter.addItem(item);
                    }
                }
            }
         //  chatting_adapter.notifyDataSetChanged();



           /* Item_Chatting item = new Item_Chatting();
            item.setMsg_date(received_date);

            item.setCarpool_id(carpool_id);
            item.setSender_name(sender_name);
            item.setSender_pic(sender_pic);
            item.setReceived_time(received_time);
            item.setReceived_content(contents);
            item.setSent(false);
            chatting_adapter.addItem(item);
            chatting_adapter.notifyDataSetChanged();*/


/*            if (cursor.moveToNext()) { // 레코드가 존재한다면,
                // no (INTEGER) 값 가져오기.
                int no = cursor.getInt(0) ;
                EditText editTextNo = (EditText) findViewById(R.id.editTextNo) ;
                editTextNo.setText(Integer.toString(no)) ;

                // name (TEXT) 값 가져오기
                String name = cursor.getString(1) ;
                EditText editTextName = (EditText) findViewById(R.id.editTextName) ;
                editTextName.setText(name) ;

                // phone (TEXT) 값 가져오기
                String phone = cursor.getString(2) ;
                EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone) ;
                editTextPhone.setText(phone) ;

                // over20 (INTEGER) 값 가져오기.
                int over20 = cursor.getInt(3) ;
                CheckBox checkBoxOver20 = (CheckBox) findViewById(R.id.checkBoxOver20) ;
                if (over20 == 0) {
                    checkBoxOver20.setChecked(false) ;
                } else {
                    checkBoxOver20.setChecked(true) ;
                }
            }*/
        }
    }

    private void send_msg(String msg_type, String sent_date, String sent_time, String sent_text) {
   /*     item.setMsg_date(sent_date);
        item.setSent_time(sent_time);
        item.setSent_content(sent_text);
        item.setSent(true);*/
       //  String sqlInsert = "INSERT INTO CONTACT_T " + "(NO, NAME, PHONE, OVER20) VALUES (" + Integer.toString(no) + "," + "'" + name + "'," + "'" + phone + "'," + ((isOver20 == true) ? "1" : "0") + ")" ; System.out.println(sqlInsert) ; sqliteDB.execSQL(sqlInsert) ;
        String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS) VALUES ("
                + "'" + msg_type + "'," + "'" + carpool_ID + "',"+ "'" + app.getUser_email() + "',"+ "'" + app.getUser_name() + "',"+ "'" + app.getUser_photo() + "',"+ "'" + sent_time + "'," +"'Y' ," +"'" + sent_text + "'" + ")" ;
       //String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS) VALUES ('2', '63', 'z@z.com', '유아인', 'z@zcom/z.gif', '12:11', 'Y', '하...')" ;
         Log.e("ddd send", sqlInsert);
         sqliteDB.execSQL(sqlInsert) ;

        }

    private void receive_msg(String msg_type, String received_date, String carpool_id, String sender_email, String sender_name, String sender_pic, String received_time, String sent_text) {

        if (sqliteDB != null) {
            String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS CHATTING_TABLE (" +
                    "NO " + "INTEGER PRIMARY KEY NOT NULL," +
                    "MSG_TYPE " + "TEXT," +
                    "CARPOOL_ID " + "TEXT," +
                    "SENDER " + "TEXT," +
                    "SENDER_NAME " + "TEXT," +
                    "SENDER_PIC " + "TEXT," +
                    "TIME " + "TEXT," +
                    "IS_READ " + "TEXT," +
                    "CONTENTS " + "TEXT" + ")";
            //    "OVER20 "       + "INTEGER" + ")" ;

            System.out.println(sqlCreateTbl);
            Log.e("ddd sqlCreateTbl", sqlCreateTbl);
            sqliteDB.execSQL(sqlCreateTbl);
        }
   /*           item.setMsg_date(received_date);
                item.setCarpool_id(carpool_id);
                item.setSender_name(sender_name);
                item.setSender_pic(sender_pic);
                item.setReceived_time(received_time);
                item.setReceived_content(contents);*/
        //  String sqlInsert = "INSERT INTO CONTACT_T " + "(NO, NAME, PHONE, OVER20) VALUES (" + Integer.toString(no) + "," + "'" + name + "'," + "'" + phone + "'," + ((isOver20 == true) ? "1" : "0") + ")" ; System.out.println(sqlInsert) ; sqliteDB.execSQL(sqlInsert) ;
        String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS) VALUES ("
                + "'" + msg_type + "'," + "'" + carpool_id + "',"+ "'" + sender_email + "',"+ "'" + sender_name + "',"+ "'" + sender_pic + "',"+ "'" + received_time + "'," +"'Y' ," +"'" + sent_text + "'" + ")" ;
        //String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS) VALUES ('2', '63', 'z@z.com', '유아인', 'z@zcom/z.gif', '12:11', 'Y', '하...')" ;
        Log.e("ddd receive", sqlInsert);
        sqliteDB.execSQL(sqlInsert) ; // 디비 닫아야 하나??

    }

    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
        /*----------------------------------------사진 앨범 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private void captureCamera(){
        String state = Environment.getExternalStorageState();
        // 외장 메모리 검사
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {
                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함

                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;

                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "chatting_pic");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }


    private void getAlbum(){
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // 카메라 전용 크랍
    public void cropImage(){
        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기, 결과물의 크기
        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        galleryAddPic();
                        iv_view.setImageURI(imageUri);

                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(ChattingRoomActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {

                    if(data.getData() != null){
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        }catch (Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {

                    galleryAddPic();
                    iv_view.setImageURI(albumURI);
                }
                break;
        }
    }


    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    //아래는 권한 요청 Callback 함수입니다. PERMISSION_GRANTED로 권한을 획득했는지 확인할 수 있습니다. 아래에서는 !=를 사용했기에
//권한 사용에 동의를 안했을 경우를 if문으로 코딩되었습니다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    //권한 획득에 동의를 하지 않았을 경우 아래 Toast 메세지를 띄우며 해당 Activity를 종료시킵니다.
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

        /*----------------------------------------사진 앨범 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

}