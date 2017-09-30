package com.example.jpar4.mitfahren.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.network.ClientBackground;
import com.example.jpar4.mitfahren.network.NetworkTask;
import com.example.jpar4.mitfahren.service.MyService;
import com.example.jpar4.mitfahren.tmap_test.NewSearchActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    TextView tv_logo;
    EditText et_input_id;
    EditText et_input_pw;
    Button btn_login;
    Button btn_join;

    Myapp app; // app객체
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    MyService mService; //서비스객체
    boolean mBound;
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    /*채팅용*/
    NetworkTask networkTask;
    ClientBackground client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*App객체에 아이디 저장 로그인 성공시*/
        app = (Myapp)getApplicationContext();


        tv_logo = (TextView) findViewById(R.id.tv_logo);
        et_input_id = (EditText) findViewById(R.id.et_input_id); // 이메일
        et_input_pw = (EditText) findViewById(R.id.et_input_pw); // 패스워드
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_join = (Button) findViewById(R.id.btn_join);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//로그인
                LoginCheck task = new LoginCheck();
                task.execute(et_input_id.getText().toString(),et_input_pw.getText().toString());


         /*연습*/
            /*    networkTask = new NetworkTask(et_input_id.getText().toString(), getApplicationContext());
               networkTask.execute();*/
              /*  client= new ClientBackground(et_input_id.getText().toString());
               client.start();*/



            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//회원가입
  /*              Intent intent = new Intent(LoginActivity.this,  test_join.class);
                startActivity(intent);*/
          /*연습*/

                NotificationManager notificationManager= (NotificationManager)LoginActivity.this.getSystemService(LoginActivity.this.NOTIFICATION_SERVICE);
                Intent intent1 = new Intent(LoginActivity.this.getApplicationContext(),LoginActivity.class); //인텐트 생성.
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                //intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를없앤다.

                PendingIntent pendingNotificationIntent = PendingIntent.getActivity( LoginActivity.this,0, intent1, FLAG_UPDATE_CURRENT);
                //PendingIntent는 일회용 인텐트 같은 개념입니다.
              /*  FLAG_UPDATE_CURRENT - > 만일 이미 생성된 PendingIntent가 존재 한다면, 해당 Intent의 내용을 변경함.
                  FLAG_CANCEL_CURRENT - .이전에 생성한 PendingIntent를 취소하고 새롭게 하나 만든다.
                  FLAG_NO_CREATE -> 현재 생성된 PendingIntent를 반환합니다.
                  FLAG_ONE_SHOT - >이 플래그를 사용해 생성된 PendingIntent는 단 한번밖에 사용할 수 없습니다*/

                builder.setSmallIcon(R.drawable.searching).setTicker("HETT").setWhen(System.currentTimeMillis())
                        .setNumber(1).setContentTitle("푸쉬 제목").setContentText("푸쉬내용")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                //해당 부분은 API 4.1버전부터 작동합니다.
                //setSmallIcon - > 작은 아이콘 이미지
                //setTicker - > 알람이 출력될 때 상단에 나오는 문구.
                //setWhen -> 알림 출력 시간.
                //setContentTitle-> 알림 제목
                //setConentText->푸쉬내용

                notificationManager.notify(1, builder.build()); // Notification send
          /*연습*/
            }
        });
    }
    /*로그인 확인하는 쓰레드*/
    class LoginCheck extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

          //  Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
            String[] arr_result = result.split("#");
            for(int i=0;i<arr_result.length;i++){
                Log.e("result check", arr_result[i]);
            }

            if((arr_result[arr_result.length-1]).equals("o")){//로그인성공 // 성공시 마지막은 항상 "o"
                /*로그인 성공했을 때*/
            /*-------------------------------------------사용자 정보 세팅----------------------------------------------------------------*/
                app.setUser_email(arr_result[0]);
                app.setUser_name(arr_result[1]);
                app.setUser_age(arr_result[2]);
                app.setUser_sex(arr_result[3]);
                app.setUser_drive_num(arr_result[4]);
                app.setUser_ride_num(arr_result[5]);
                app.setUser_photo(arr_result[6]);
                app.setLoginOK(true);
            /*-------------------------------------------사용자 정보 세팅----------------------------------------------------------------*/
/*---------------------------------------------------------------로그인시 tcp통신 연결----------------------------------------------------------------------------------*/
                if(mBound) {
                  /*테스트용*/
               /*  String numberStr = String.valueOf(mService.showTheNumber());
                  Toast.makeText(app, numberStr, Toast.LENGTH_SHORT).show();*/
               /*TCP연결해보기*/
                    mService.setConnectionTCP(et_input_id.getText().toString());
                }
/*---------------------------------------------------------------로그인시 tcp통신 연결----------------------------------------------------------------------------------*/
                Intent intent = new Intent(LoginActivity.this,  NewSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "[로그인 실패] "+result, Toast.LENGTH_SHORT).show();
            }

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String user_email = (String)params[0];
            String user_pwd = (String)params[1];

            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/login_idpwd.php";
            String postParameters = "user_email=" + user_email + "&user_pwd=" + user_pwd;


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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
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
/*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyService.class);
        //startService(intent);
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
