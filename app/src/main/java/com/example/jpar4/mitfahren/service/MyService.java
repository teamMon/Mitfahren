package com.example.jpar4.mitfahren.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.apprtc.ConnectActivity;
import com.example.jpar4.mitfahren.tmap_test.CarpoolInfoActivity;
import com.example.jpar4.mitfahren.tmap_test.ChattingRoomActivity;
import com.example.jpar4.mitfahren.tmap_test.NewNotiActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by jpar4 on 2017-09-29.
 */

public class MyService extends Service {

    /*TCP연결용*/
    NetworkTask networkTask;

    public IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder{
        public MyService getService(){
            return MyService.this;
        }
    }

   public int showTheNumber(){
       return new Random().nextInt(99);
   }

   public void setConnectionTCP(String email){
     //  Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
       networkTask = new NetworkTask(this , email, this);
       networkTask.start();
   }

   public void sendMsg(String msg){
       networkTask.sendMessage(msg);
   }

   /*이거 안됨... 하...*/
   public void sendToast(String msg){
       Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
       networkTask.sendToast(this, msg);
   }

    //콜백 인터페이스 선언
    public interface ICallback {
        public String getCarpool_ID();// 카풀아이디 가져옴
        public void recvData(String msg); //액티비티에서 선언한 콜백 함수.
        public void recvChattingMsg(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents);
        public void recvChattingImgMsg(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents, String img_file_name);
        public void changeUnreadMsgNum(String carpool_id); // 안읽음 메시지 숫자 바꾸기
        public void recvRoomID(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents);
        public void recvRejectCall(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents);
    }

    private ICallback mCallback;

    //액티비티에서 콜백 함수를 등록하기 위함.
    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    public String getCarpoolID(){
        return mCallback.getCarpool_ID();
    }

    //액티비티에서 서비스 함수를 호출하기 위한 함수 생성
    public void myServiceFunc(String msg){
        Log.e("ddd", "서비스 실행되나?");
        //서비스에서 처리할 내용
        mCallback.recvData(msg);
       // return msg;
    }

    //액티비티에서 서비스 함수를 호출하기 위한 함수 생성
    public void chattingMsgDeliver(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents){
        Log.e("ddd", "chattingMsgDeliver");
        //서비스에서 처리할 내용
        mCallback.recvChattingMsg(carpool_id, sender_email, sender_name, sender_pic, contents); // 채팅 메시지
        mCallback.changeUnreadMsgNum(carpool_id);// 채팅메시지가 와도 바꿔주고
        // return msg;
    }

    //액티비티에서 서비스 함수를 호출하기 위한 함수 생성
    public void chattingImgMsgDeliver(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents, String img_file_name){
        Log.e("ddd", "chattingImgMsgDeliver");
        //서비스에서 처리할 내용
        mCallback.recvChattingImgMsg(carpool_id, sender_email, sender_name, sender_pic, contents, img_file_name);// 채팅 이미지 메시지
        mCallback.changeUnreadMsgNum(carpool_id);// 채팅이미지메시지가 와도 바꿔줌
    }

    //액티비티에서 서비스 함수를 호출하기 위한 함수 생성
    public void roomIDDeliver(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents){
        Log.e("ddd", "roomIDDeliver");
        //서비스에서 처리할 내용
        mCallback.recvRoomID(carpool_id, sender_email, sender_name, sender_pic, contents); // 룸 ID
        // 서비스에서 바로 액티비티 열어줌.
        Intent intent = new Intent(this, ConnectActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                /*intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/
        intent.putExtra("profile_pic", sender_pic);
        intent.putExtra("prfile_name", sender_name);
        intent.putExtra("carpool_ID", carpool_id);
        intent.putExtra("room_ID", contents); // contents = roomID 메시지타입이 4번일때.
        intent.putExtra("callRecv","Recv");
        startActivity(intent);
        // return msg;
    }
    //액티비티에서 서비스 함수를 호출하기 위한 함수 생성
    public void rejectCall(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents){
        Log.e("ddd", "chattingMsgDeliver");
        //서비스에서 처리할 내용
        mCallback.recvRejectCall(carpool_id, sender_email, sender_name, sender_pic, contents); // 채팅 메시지

        // return msg;
    }

//서비스에서 액티비티 함수 호출은..
   // mCallback.recvData();
}
/*------------------------------------------------------------------------------------네크워크 스레드 복붙-------------------------------------------------------------------------------------------------------------------------*/


/**
 * Created by jpar4 on 2017-09-28.
 */

 class NetworkTask extends Thread {
    Myapp app;

    String dstAddress;
    int dstPort;
    String response;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String msg;
    private String email;
    boolean isConnected = true;
    Service service;
    MyService myService;
    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    SQLiteDatabase sqliteDB;
    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public NetworkTask(){

    }

    public NetworkTask(Service service, String email, MyService myService){
        this.service = service;
        this.email = email;
        this.myService = myService;
        this.app = (Myapp)service.getApplicationContext();

    }

    public NetworkTask(String addr, int port){
        this.dstAddress = addr;
        this.dstPort = port;
    }
    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private SQLiteDatabase init_database() {
        Log.e("ddd", "데이터베이스세팅 서비스");
        SQLiteDatabase db = null ;
        // File file = getDatabasePath("chatting.db");
        File file = new File(service.getFilesDir(), "chatting.db");
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

    private void receive_msg(String msg_type, String received_date, String carpool_id, String sender_email, String sender_name, String sender_pic, String received_time, String sent_text) {

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
/*        String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS, IMG_MSG) VALUES ("
                + "'" + msg_type + "'," + "'" + carpool_ID + "',"+ "'" + app.getUser_email() + "',"+ "'" + app.getUser_name() + "',"+ "'" + app.getUser_photo() + "',"+ "'" + sent_time + "'," +"'Y' ," +"'" + sent_text + "'," +"'NO_IMG'" + ")" ;*/
        String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS, IMG_MSG) VALUES ("
                + "'" + msg_type + "'," + "'" + carpool_id + "',"+ "'" + sender_email + "',"+ "'" + sender_name + "',"+ "'" + sender_pic + "',"+ "'" + received_time + "'," +"'Y' ," +"'" + sent_text + "'," +"'NO_IMG'" + ")" ;
        //String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS) VALUES ('2', '63', 'z@z.com', '유아인', 'z@zcom/z.gif', '12:11', 'Y', '하...')" ;
        Log.e("ddd receive", sqlInsert);
        sqliteDB.execSQL(sqlInsert) ; // 디비 닫아야 하나??
        sqliteDB.close();

    }
    private void receive_imgmsg(String msg_type, String received_date, String carpool_id, String sender_email, String sender_name, String sender_pic, String received_time, String received_text, String received_img) {

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
/*        String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS, IMG_MSG) VALUES ("
                + "'" + msg_type + "'," + "'" + carpool_ID + "',"+ "'" + app.getUser_email() + "',"+ "'" + app.getUser_name() + "',"+ "'" + app.getUser_photo() + "',"+ "'" + sent_time + "'," +"'Y' ," +"'" + sent_text + "'," +"'NO_IMG'" + ")" ;*/
        String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS, IMG_MSG) VALUES ("
                + "'" + msg_type + "'," + "'" + carpool_id + "',"+ "'" + sender_email + "',"+ "'" + sender_name + "',"+ "'" + sender_pic + "',"+ "'" + received_time + "'," +"'Y' ," +"'" + received_text + "',"  +"'" + received_img + "'" + ")" ;
        //String sqlInsert = "INSERT INTO CHATTING_TABLE " + "(MSG_TYPE, CARPOOL_ID, SENDER, SENDER_NAME, SENDER_PIC, TIME, IS_READ, CONTENTS) VALUES ('2', '63', 'z@z.com', '유아인', 'z@zcom/z.gif', '12:11', 'Y', '하...')" ;
        Log.e("ddd receive", sqlInsert);
        sqliteDB.execSQL(sqlInsert) ; // 디비 닫아야 하나??
        sqliteDB.close();

    }

    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    public void run() {
        super.run();
        try {
            //socket = new Socket("127.0.0.1", 7777);
            socket = new Socket("13.124.251.123", 7777);
            //System.out.println("서버 연결됨.");
            Log.e("ddd msg", "서버 연결됨.");

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            out.writeUTF(email);
            // System.out.println("클라이언트 : 메시지 전송완료");
            Log.e("ddd msg", "클라이언트 : 메시지 전송완료"+socket.getInetAddress());
            while(isConnected) {
                msg=in.readUTF();
                Log.e("ddd msg", msg +"!!!");
                try {
                    //     System.out.println("msg :"+msg);
                    //String msg1 = msg.split("#")[0];
                    if(msg.charAt(0)=='{') {// {로 시작하면 json
                        JSONObject jsonObj = new JSONObject(msg);
                  //      Log.e("ddd", jsonObj.get("kindOfmsg").toString()+jsonObj.get("receiver").toString()+jsonObj.get("sender").toString()); // 여기 리시버 없는데 로그찍어서 에러뜸
                        if(jsonObj.get("kindOfmsg").equals("1")){ //1번은 카풀신청
                            Log.e("ddd 노티알람", jsonObj.get("sender").toString()+"님이 카풀을 신청하셨습니다.");
                            sencNotification(msg);//노티피케이션---------------------------------------jsonObj.get("sender").toString()
                        }
                        if(jsonObj.get("kindOfmsg").equals("2")){ //2번은 메시지 전송
                            Log.e("ddd 챗팅왔다", jsonObj.get("sender").toString()+"님이 메시지 보냄 : " +jsonObj.get("contents").toString() );
                           // myService.myServiceFunc(jsonObj.get("contents").toString()); //에코체팅
                            //카풀아이디, 센더 이메일, 센더 이음, 센더 사진, 전달한 내용 넣어줌
        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
                        if(!app.getUser_email().equals( jsonObj.get("sender").toString())){ // 자기가 보낸 메시지면 저장 안해도됨
                            sqliteDB = init_database();
                            Calendar calendar = Calendar.getInstance();
                            java.util.Date date = calendar.getTime();
                            String received_time = (new SimpleDateFormat("aa hh:mm ").format(date));
                            String received_date = (new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREA).format(date));
                            receive_msg( "2",  received_date,  jsonObj.get("carpool_id").toString(),  jsonObj.get("sender").toString(),  jsonObj.get("sender_name").toString(),  jsonObj.get("sender_pic").toString(),  received_time,  jsonObj.get("contents").toString()) ;
                        }
        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

                            myService.chattingMsgDeliver(jsonObj.get("carpool_id").toString(), jsonObj.get("sender").toString(), jsonObj.get("sender_name").toString(), jsonObj.get("sender_pic").toString(), jsonObj.get("contents").toString());

         /*----------------------------------------현재 액티비티 확인하기 ----------------------------------------------------------------------------------------*/
                            Log.e("ddd ABC","ChattingRoomActivity 여기와?");
                            ActivityManager activityManager = (ActivityManager) service.getSystemService(service.ACTIVITY_SERVICE);
                            List<ActivityManager.RunningTaskInfo> info;
                            info = activityManager.getRunningTasks(7);
           /*                 List<ActivityManager.AppTask> info;
                            info = activityManager.getAppTasks();*/
                          //  for (Iterator iterator = info.iterator(); iterator.hasNext();)  {
                                Iterator iterator = info.iterator(); iterator.hasNext();
                                ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) iterator.next();

                                if(runningTaskInfo.topActivity.getClassName().equals("com.example.jpar4.mitfahren.tmap_test.ChattingRoomActivity")) { //채팅방일때
                                    Log.e("ddd ABC","ChattingRoomActivity");
                                    if(myService.getCarpoolID().equals(jsonObj.get("carpool_id").toString())){//같으면 보내지말고

                                    }else{// 다르면 보내야지
                                        NotificationManager notificationManager= (NotificationManager)service.getSystemService(service.NOTIFICATION_SERVICE);
                                        Intent intent1 = new Intent(service.getApplicationContext(),ChattingRoomActivity.class); //인텐트 생성.
                                        intent1.putExtra("carpool_ID",jsonObj.get("carpool_id").toString());
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        Notification.Builder builder = new Notification.Builder(service.getApplicationContext());
                                        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( service,0, intent1, FLAG_UPDATE_CURRENT);
                                        builder.setSmallIcon(R.drawable.searching).setTicker("알림").setWhen(System.currentTimeMillis())
                                                .setContentTitle("채팅메시지").setContentText(""+jsonObj.get("sender_name").toString()+": "+jsonObj.get("contents").toString())
                                                //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                                                .setContentIntent(pendingNotificationIntent).setAutoCancel(true)
                                                .setPriority(Notification.PRIORITY_MAX) //** MAX 나 HIGH로 줘야 가능함
                                                //.setFullScreenIntent(pendingNotificationIntent, true)
                                                .setContentIntent(pendingNotificationIntent);
                                        notificationManager.notify(0, builder.build()); // Notification send

                                    }

                                }else{// 채팅방 아닐때 노티 ㅋㅋㅋ
                                    NotificationManager notificationManager= (NotificationManager)service.getSystemService(service.NOTIFICATION_SERVICE);
                                        Intent intent1 = new Intent(service.getApplicationContext(),ChattingRoomActivity.class); //인텐트 생성.
                                        intent1.putExtra("carpool_ID",jsonObj.get("carpool_id").toString());
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        Notification.Builder builder = new Notification.Builder(service.getApplicationContext());
                                        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( service,0, intent1, FLAG_UPDATE_CURRENT);
                                        builder.setSmallIcon(R.drawable.searching).setTicker("알림").setWhen(System.currentTimeMillis())
                                                .setContentTitle("채팅메시지").setContentText(""+jsonObj.get("sender_name").toString()+": "+jsonObj.get("contents").toString())
                                                //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true)
                                                .setPriority(Notification.PRIORITY_MAX) //** MAX 나 HIGH로 줘야 가능함
                                                //.setFullScreenIntent(pendingNotificationIntent, true);
                                                .setContentIntent(pendingNotificationIntent);
                                        notificationManager.notify(0, builder.build()); // Notification send
                                }
                                Log.e("ABC ", runningTaskInfo.topActivity.getClassName().toString());

                    //        }
        /*----------------------------------------현재 액티비티 확인하기스 ----------------------------------------------------------------------------------------*/


                        }
                        if(jsonObj.get("kindOfmsg").equals("3")){ //3번은 이미지 메시지 전송
                            Log.e("ddd 이미지왔다", jsonObj.get("sender").toString()+"님이 이미지 보냄 : " +jsonObj.get("contents").toString() );
                            // myService.myServiceFunc(jsonObj.get("contents").toString()); //에코체팅
                            //카풀아이디, 센더 이메일, 센더 이음, 센더 사진, 전달한 내용 넣어줌
        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
                            if(!app.getUser_email().equals( jsonObj.get("sender").toString())) { // 자기가 보낸 메시지면 저장 안해도됨
                                sqliteDB = init_database();
                                Calendar calendar = Calendar.getInstance();
                                java.util.Date date = calendar.getTime();
                                String received_time = (new SimpleDateFormat("aa hh:mm ").format(date));
                                String received_date = (new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREA).format(date));
                                receive_imgmsg( "3",  received_date,  jsonObj.get("carpool_id").toString(),  jsonObj.get("sender").toString(),  jsonObj.get("sender_name").toString(),  jsonObj.get("sender_pic").toString(),  received_time,  jsonObj.get("contents").toString(), jsonObj.get("img_file_name").toString()) ;
                            }
        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

                            myService.chattingImgMsgDeliver(jsonObj.get("carpool_id").toString(), jsonObj.get("sender").toString(), jsonObj.get("sender_name").toString(), jsonObj.get("sender_pic").toString(), jsonObj.get("contents").toString(),  jsonObj.get("img_file_name").toString());
                                     /*----------------------------------------현재 액티비티 확인하기 ----------------------------------------------------------------------------------------*/
                            Log.e("ddd ABC","ChattingRoomActivity 여기와?");
                            ActivityManager activityManager = (ActivityManager) service.getSystemService(service.ACTIVITY_SERVICE);
                            List<ActivityManager.RunningTaskInfo> info;
                            info = activityManager.getRunningTasks(7);
           /*                 List<ActivityManager.AppTask> info;
                            info = activityManager.getAppTasks();*/
                            //  for (Iterator iterator = info.iterator(); iterator.hasNext();)  {
                            Iterator iterator = info.iterator(); iterator.hasNext();
                            ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) iterator.next();

                            if(runningTaskInfo.topActivity.getClassName().equals("com.example.jpar4.mitfahren.tmap_test.ChattingRoomActivity")) { //채팅방일때
                                Log.e("ddd ABC","ChattingRoomActivity");
                                if(myService.getCarpoolID().equals(jsonObj.get("carpool_id").toString())){//같으면 보내지말고

                                }else{// 다르면 보내야지
                                    NotificationManager notificationManager= (NotificationManager)service.getSystemService(service.NOTIFICATION_SERVICE);
                                    Intent intent1 = new Intent(service.getApplicationContext(),ChattingRoomActivity.class); //인텐트 생성.
                                    intent1.putExtra("carpool_ID",jsonObj.get("carpool_id").toString());
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    Notification.Builder builder = new Notification.Builder(service.getApplicationContext());
                                    PendingIntent pendingNotificationIntent = PendingIntent.getActivity( service,0, intent1, FLAG_UPDATE_CURRENT);
                                    builder.setSmallIcon(R.drawable.searching).setTicker("알림").setWhen(System.currentTimeMillis())
                                            .setContentTitle("채팅메시지").setContentText(""+jsonObj.get("sender_name").toString()+": "+jsonObj.get("contents").toString())
                                            .setContentIntent(pendingNotificationIntent).setAutoCancel(true)
                                            .setPriority(Notification.PRIORITY_MAX) //** MAX 나 HIGH로 줘야 가능함
                                            .setContentIntent(pendingNotificationIntent);
                                    //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true)
                                    //.setPriority(Notification.PRIORITY_MAX) //** MAX 나 HIGH로 줘야 가능함
                                    //.setFullScreenIntent(pendingNotificationIntent, true);
                                    //.setContentIntent(pendingNotificationIntent)
                                    //.setOngoing(true);
                                    notificationManager.notify(0, builder.build()); // Notification send
                                }

                            }else{// 채팅방 아닐때 노티 ㅋㅋㅋ
                                if(!app.getUser_email().equals( jsonObj.get("sender").toString())) { // 자기가 보낸 메시지면 노티 안띄워도 되는데 테스트폰에서 갑자기 띄워서 방어막침
                                    NotificationManager notificationManager= (NotificationManager)service.getSystemService(service.NOTIFICATION_SERVICE);
                                    Intent intent1 = new Intent(service.getApplicationContext(),ChattingRoomActivity.class); //인텐트 생성.
                                    intent1.putExtra("carpool_ID",jsonObj.get("carpool_id").toString());
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    Notification.Builder builder = new Notification.Builder(service.getApplicationContext());
                                    PendingIntent pendingNotificationIntent = PendingIntent.getActivity( service,0, intent1, FLAG_UPDATE_CURRENT);
                                    builder.setSmallIcon(R.drawable.searching).setTicker("알림").setWhen(System.currentTimeMillis())
                                            .setContentTitle("채팅메시지").setContentText(""+jsonObj.get("sender_name").toString()+": "+jsonObj.get("contents").toString())
                                            //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true)
                                            .setPriority(Notification.PRIORITY_MAX) //** MAX 나 HIGH로 줘야 가능함
                                            //.setFullScreenIntent(pendingNotificationIntent, true);
                                            .setContentIntent(pendingNotificationIntent);
                                            //.setOngoing(true);
                                    notificationManager.notify(0, builder.build()); // Notification send
                                }
                            }
                            Log.e("ABC ", runningTaskInfo.topActivity.getClassName().toString());

                            //        }
        /*----------------------------------------현재 액티비티 확인하기스 ----------------------------------------------------------------------------------------*/

                        }
                        if(jsonObj.get("kindOfmsg").equals("4")){ //4번은 화상채팅 여기선 contents가 roomid
                            Log.e("ddd 화상채팅", jsonObj.get("sender").toString()+"님이 화상채팅 룸아이디 보냄 : " +jsonObj.get("contents").toString() );
                            // myService.myServiceFunc(jsonObj.get("contents").toString()); //에코체팅
                            //카풀아이디, 센더 이메일, 센더 이음, 센더 사진, 전달한 내용 넣어줌
        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

                            if(jsonObj.get("contents").toString().equals("reject")){//contents는 RoomID또는 reject가 날라올수 있음
                                if(!app.getUser_email().equals( jsonObj.get("sender").toString())) { // 자기가 보낸 메시지면 실행 안해도됨
                                    myService.rejectCall(jsonObj.get("carpool_id").toString(), jsonObj.get("sender").toString(), jsonObj.get("sender_name").toString(), jsonObj.get("sender_pic").toString(), jsonObj.get("contents").toString());
                                }
                            }
                            else{// 룸 아이디가 날라온 경우
                                if(!app.getUser_email().equals( jsonObj.get("sender").toString()))  {// 자기가 보낸 메시지면 실행 안해도됨
                                    myService.roomIDDeliver(jsonObj.get("carpool_id").toString(), jsonObj.get("sender").toString(), jsonObj.get("sender_name").toString(), jsonObj.get("sender_pic").toString(), jsonObj.get("contents").toString());
                                }
                            }
                        }

                    }else {// json형식 아닐때
                        //clientsMap.get(key).writeUTF(msg); // 그냥 찍어보냄
                        if(msg.equals("success")){
                            Log.e("ddd", "ddd success");
                            //Toast.makeText(service, "카풀신청이 되었습니다.", Toast.LENGTH_SHORT).show();
                            sencNotification(msg);
                        }
                        else if(msg.equals("failure")){
                            Log.e("ddd", "ddd failure");
                            sencNotification(msg);
                            /*Toast.makeText(service, "이미 요청된 카풀입니다.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(service.getApplicationContext(), "이미 요청된 카풀입니다.", Toast.LENGTH_SHORT).show();
                            myService.sendToast("이미 요청된 카풀입니다.");
                            Toast.makeText(myService, "ddd", Toast.LENGTH_SHORT).show();
                            MyService m = new MyService();
                            m.sendToast("Ddd");
                            Toast.makeText(m, "xxxx", Toast.LENGTH_SHORT).show();*/

                        }
                    }

                }catch(Exception e) {
                    e.printStackTrace();
                }


            }



        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg2) {
        Sender sender = new Sender(msg2);
        sender.start();
      /*  try {
            out.writeUTF(msg2);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    class Sender extends Thread{
        String msg3;

        public Sender(String msg){
            this.msg3 = msg;
        }

        @Override
        public void run() {
            try {
                out.writeUTF(msg3);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class tosatSender extends Thread{
        String msg3;
        Context mContext;

        public tosatSender(Context mContext, String msg){
            this.msg3 = msg;
            this.mContext = mContext;
        }

        @Override
        public void run() {
            Toast.makeText(mContext, msg3, Toast.LENGTH_SHORT).show();
        }
    }
    public void sendToast(Context mContext, String msg2) {
        tosatSender toastsender = new tosatSender(mContext, msg2);
        toastsender.start();
      /*  try {
            out.writeUTF(msg2);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void sencNotification(String msg){
         /*-----------------------------------------------------노티피케이션---------------------------------------------------------------------------------------------------------------------------------------*/
        NotificationManager notificationManager= (NotificationManager)service.getSystemService(service.NOTIFICATION_SERVICE);

        if(msg.equals("success")){
            Intent intent1 = new Intent(service.getApplicationContext(),CarpoolInfoActivity.class); //인텐트 생성.
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Notification.Builder builder = new Notification.Builder(service.getApplicationContext());
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity( service,0, intent1, FLAG_UPDATE_CURRENT);
            builder.setSmallIcon(R.drawable.searching).setTicker("알림").setWhen(System.currentTimeMillis())
                    .setContentTitle("카풀신청").setContentText("카풀이 신청되었습니다.")
                    //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX) //** MAX 나 HIGH로 줘야 가능함
                    .setFullScreenIntent(pendingNotificationIntent, true);
            notificationManager.notify(1, builder.build()); // Notification send
        }
        else if(msg.equals("failure")){
            Intent intent1 = new Intent(service.getApplicationContext(),CarpoolInfoActivity.class); //인텐트 생성.
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Notification.Builder builder = new Notification.Builder(service.getApplicationContext());
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity( service,0, intent1, FLAG_UPDATE_CURRENT);
            builder.setSmallIcon(R.drawable.searching).setTicker("알림").setWhen(System.currentTimeMillis())
                    .setContentTitle("카풀신청").setContentText( "이미 요청된 카풀입니다.") // 일시적으보 바꿔놓자
                    //.setContentTitle("카풀신청").setContentText( "카풀이 신청되었습니다.")
                    //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true)
            .setPriority(Notification.PRIORITY_MAX) //** MAX 나 HIGH로 줘야 가능함
            .setFullScreenIntent(pendingNotificationIntent, true);
            notificationManager.notify(1, builder.build()); // Notification send
        }
        else{
            Intent intent1 = new Intent(service.getApplicationContext(),NewNotiActivity.class); //인텐트 생성.
            intent1.putExtra("msg", msg);



            Notification.Builder builder = new Notification.Builder(service.getApplicationContext());
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를없앤다.

            PendingIntent pendingNotificationIntent = PendingIntent.getActivity( service,0, intent1, FLAG_UPDATE_CURRENT);
            //PendingIntent는 일회용 인텐트 같은 개념입니다.
              /*  FLAG_UPDATE_CURRENT - > 만일 이미 생성된 PendingIntent가 존재 한다면, 해당 Intent의 내용을 변경함.
                        FLAG_CANCEL_CURRENT - .이전에 생성한 PendingIntent를 취소하고 새롭게 하나 만든다.
                        FLAG_NO_CREATE -> 현재 생성된 PendingIntent를 반환합니다.
                FLAG_ONE_SHOT - >이 플래그를 사용해 생성된 PendingIntent는 단 한번밖에 사용할 수 없습니다*/

            JSONObject jsonObj = null;
            String sender = null;
            try {
                jsonObj = new JSONObject(msg);
                sender = jsonObj.get("sender").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            builder.setSmallIcon(R.drawable.searching).setTicker("알림").setWhen(System.currentTimeMillis())
                    .setContentTitle("카풀신청").setContentText( sender+"님이 카풀을 신청하셨습니다.")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true)
                    //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
            .setPriority(Notification.PRIORITY_MAX) //** MAX 나 HIGH로 줘야 가능함
            .setFullScreenIntent(pendingNotificationIntent, true);
            notificationManager.notify(1, builder.build()); // Notification send
        }


        //해당 부분은 API 4.1버전부터 작동합니다.

//setSmallIcon - > 작은 아이콘 이미지

//setTicker - > 알람이 출력될 때 상단에 나오는 문구.

//setWhen -> 알림 출력 시간.

//setContentTitle-> 알림 제목

//setConentText->푸쉬내용


      /*-----------------------------------------------------노티피케이션---------------------------------------------------------------------------------------------------------------------------------------*/
    }

 /*   public String receiveMessage(String msgReceived) {
        Receiver receiver = new Receiver(msgReceived);
        receiver.start();
        return msgReceived;
      *//*  try {
            out.writeUTF(msg2);
        } catch (IOException e) {
            e.printStackTrace();
        }*//*
    }

    class Receiver extends Thread{
        String msgReceived;

        public Receiver(String msg){
            this.msgReceived = msg;
        }

        @Override
        public void run() {
            try {
                msg=in.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}
/*------------------------------------------------------------------------------------네크워크 스레드 복붙-------------------------------------------------------------------------------------------------------------------------*/