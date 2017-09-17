package com.example.jpar4.mitfahren.tmap_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewUserPageActivity extends AppCompatActivity {
    private static final String TAG = "NewUserPage";
    /*app객체 (로그인에 정보 이용에사용)*/
    Myapp app;
    /*app객체 (로그인에 정보 이용에 사용)*/
    /**
     * Context Variables
     */
    Context mContext;

    /*화면 View 객체 세팅*/
    ImageView iv_user_info_pic;
    TextView tv_user_info_email, tv_user_info_name, tv_user_info_age, tv_user_info_sex, tv_user_info_total_rider, tv_user_info_total_riding;
    /*화면 View 객체 세팅*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("사용자 정보");
        setContentView(R.layout.new_user_page_activity);
        app = (Myapp) getApplicationContext();
        mContext = getApplicationContext();

        iv_user_info_pic = (ImageView) findViewById(R.id.iv_user_info_pic);
        tv_user_info_email = (TextView) findViewById(R.id.tv_user_info_email);
        tv_user_info_name = (TextView) findViewById(R.id.tv_user_info_name);
        tv_user_info_age = (TextView) findViewById(R.id.tv_user_info_age);
        tv_user_info_sex = (TextView) findViewById(R.id.tv_user_info_sex);
        tv_user_info_total_rider = (TextView) findViewById(R.id.tv_user_info_total_rider);
        tv_user_info_total_riding = (TextView) findViewById(R.id.tv_user_info_total_riding);

        /*
        * 1. 로그인 했을 때, app객체 유무로 자기 정보를 표시할 것 ( http통신 필요 없음)
         * 2. app 객체가 없을 때 이메일을 인텐트로 넘겨받아 디비에서 데이터를 받아옴( http통신 필요)
        */
        GetDriverInfo task = new GetDriverInfo();
        task.execute(app.getUser_email());
    }
    /*-----------------------------------------------------------------------디비에서 운전자 정보를 가져옮-----------------------------------------------------------------------*/
        /*로그인 확인하는 쓰레드*/
    class GetDriverInfo extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(NewUserPageActivity.this,
                    "잠시만 기다려 주세요.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            //  Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
            String[] arr_result = result.split("#");
            for (int i = 0; i < arr_result.length; i++) {
                Log.e("result check", arr_result[i]);
            }
/*에이싱크결과값 받아옴*/
            String user_email =arr_result[0];
            String user_name =arr_result[1];
            String user_age =arr_result[2];
            String user_sex =arr_result[3];
            String user_drive_num =arr_result[4];
            String user_ride_num =arr_result[5];
            String user_photo =arr_result[6];


            tv_user_info_email.setText(user_email);
            tv_user_info_name.setText(user_name);
            tv_user_info_age.setText(user_age);
            tv_user_info_sex.setText(user_sex);
            tv_user_info_total_rider.setText(user_drive_num + " 회");
            tv_user_info_total_riding.setText(user_ride_num + " 회");
            Picasso.with(mContext).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + user_photo).into(iv_user_info_pic);


        /*    ImageView iv_user_info_pic;
            TextView tv_user_info_name, tv_user_info_age, tv_user_info_sex, tv_user_info_total_rider, tv_user_info_total_riding;*/


            Log.d(TAG, "POST response  - " + result);

        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String user_email = (String) params[0];

            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/get_user_info.php";
            String postParameters = "user_email=" + user_email;


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
}
