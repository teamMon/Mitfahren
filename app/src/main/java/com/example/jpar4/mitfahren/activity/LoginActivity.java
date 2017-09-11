package com.example.jpar4.mitfahren.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.test_join.test_join;
import com.example.jpar4.mitfahren.tmap_test.NewSearchActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    TextView tv_logo;
    EditText et_input_id;
    EditText et_input_pw;
    Button btn_login;
    Button btn_join;

    Myapp app; // app객체

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
            public void onClick(View v) {
                LoginCheck task = new LoginCheck();
                task.execute(et_input_id.getText().toString(),et_input_pw.getText().toString());


         /*       Intent intent = new Intent(LoginActivity.this,  MainActivity.class);
                startActivity(intent);*/
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,  test_join.class);
                startActivity(intent);
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
                app.setLoginOK(true);
            /*-------------------------------------------사용자 정보 세팅----------------------------------------------------------------*/


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
}
