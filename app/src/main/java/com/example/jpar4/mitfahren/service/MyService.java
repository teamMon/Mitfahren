package com.example.jpar4.mitfahren.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.tmap_test.CarpoolInfoActivity;
import com.example.jpar4.mitfahren.tmap_test.NewNotiActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

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


}
/*------------------------------------------------------------------------------------네크워크 스레드 복붙-------------------------------------------------------------------------------------------------------------------------*/


/**
 * Created by jpar4 on 2017-09-28.
 */

 class NetworkTask extends Thread {
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

    public NetworkTask(){

    }

    public NetworkTask(Service service, String email, MyService myService){
        this.service = service;
        this.email = email;
        this.myService = myService;

    }

    public NetworkTask(String addr, int port){
        this.dstAddress = addr;
        this.dstPort = port;
    }

    @Override
    public void run() {
        super.run();
        try {
            //socket = new Socket("127.0.0.1", 7777);
            socket = new Socket("52.78.6.238", 7777);
            //System.out.println("서버 연결됨.");
            Log.e("ddd msg", "서버 연결됨.");

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            out.writeUTF(email);
            // System.out.println("클라이언트 : 메시지 전송완료");
            Log.e("ddd msg", "클라이언트 : 메시지 전송완료"+socket.getInetAddress());
            while(isConnected) {
                msg=in.readUTF();
                Log.e("ddd msg", msg);
                try {
                    //     System.out.println("msg :"+msg);
                    //String msg1 = msg.split("#")[0];
                    if(msg.charAt(0)=='{') {// {로 시작하면 json
                        JSONObject jsonObj = new JSONObject(msg);
                        Log.e("ddd", jsonObj.get("kindOfmsg").toString()+jsonObj.get("receiver").toString()+jsonObj.get("sender").toString());
                        if(jsonObj.get("kindOfmsg").equals("1")){ //1번은 카풀신청
                            Log.e("ddd 노티알람", jsonObj.get("sender").toString()+"님이 카풀을 신청하셨습니다.");
                            sencNotification(msg);//노티피케이션---------------------------------------jsonObj.get("sender").toString()
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