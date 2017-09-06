package com.example.jpar4.mitfahren.test_join;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class test_join extends AppCompatActivity{
    private static String TAG = "test_join.java";
/*    join_et_input_name
      join_et_input_age
      join_spn_sex
      join_et_input_id
      join_et_input_pw
      join_et_input_pwchk
      join_btn_confirm*/
    EditText join_et_input_name, join_et_input_age, join_et_input_id,
             join_et_input_pw, join_et_input_pwchk;
    Spinner join_spn_sex;
    Button join_btn_confirm;

    Boolean EMAIL_OK = false, PW_OK= false, PWCHK_OK= false, NAME_OK= false, AGE_OK= false, SEX_OK= false;//이메일 형식에 맞을때 True;EMAIL_OK,PW_OK,PWCHK_OK



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_join_activity);

        join_et_input_name = (EditText) findViewById(R.id.join_et_input_name);
        join_et_input_age = (EditText) findViewById(R.id.join_et_input_age);
        join_et_input_id = (EditText) findViewById(R.id.join_et_input_id);  // 이메일




        join_et_input_pw = (EditText) findViewById(R.id.join_et_input_pw);
        join_et_input_pwchk = (EditText) findViewById(R.id.join_et_input_pwchk);

        /*성별*/
        join_spn_sex = (Spinner)findViewById(R.id.join_spn_sex);
        ArrayAdapter sexAdapter = ArrayAdapter.createFromResource(this, R.array.join_sex, android.R.layout.simple_spinner_item);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        join_spn_sex.setAdapter(sexAdapter);

        /*버튼*/
        join_btn_confirm = (Button) findViewById(R.id.join_btn_confirm);
        join_btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(test_join.this, EMAIL_OK.toString()+PW_OK+PWCHK_OK+"", Toast.LENGTH_SHORT).show();
                /*이메일, 이름, 비밀번호, 비밀번호확인, 나이, 성별 값 가져옴*/
                /*1. 이메일 형식 예외처리 해야됨 2. 이메일 중복 예외처리 해야됨 (20170905 jpar424) */
                /*3. 비밀번호 복잡도 예외처리 해야됨 4, 나이 텍스트뷰 마이너스 안되게, 그리고 최대 3자리로 예외처리 해야됨, 숫자만 입력할 수 있도록 함*/
                /*5. 비밀번호와 비밀번호 확인의 값이 같은지 체크해야됨*/
                if(join_et_input_age.getText().length()==0) {
                    AGE_OK=false;
                }else{
                    AGE_OK=true;
                }
                if(NAME_OK && EMAIL_OK && PW_OK && PWCHK_OK && AGE_OK){ //이메일 패스워드 패스워트 확인 통과해야지 인서트문 날림
                 /*이메일, 이름, 비밀번호, 비밀번호확인, 나이, 성별 값 가져옴*/
                    String user_email = join_et_input_id.getText().toString();
                    String user_name = join_et_input_name.getText().toString();
                    String user_pwd = join_et_input_pw.getText().toString();
                    String user_pwdchk = join_et_input_pwchk.getText().toString();
                    String user_age = join_et_input_age.getText().toString();
                    String user_sex = join_spn_sex.getSelectedItem().toString();
                    //String user_photo = join_et_input_name.getText().toString();

                /*데이터 php로 전송*/
                    InsertData task = new InsertData();
                    task.execute(user_email,user_name,user_pwd,user_age,user_sex);

                /*입력 필드 초기화*/
                    join_et_input_id.setText("");
                    join_et_input_name.setText("");
                    join_et_input_pw.setText("");
                    join_et_input_pwchk.setText("");
                    join_et_input_age.setText("");
                    //join_spn_sex -> 이거 여성일 때, 남성으로 바뀌게 초기화해야됨.

                /*현재 이미지를 선택하지 않았을 때, nobody.jpg로 값이 저장 안됨. 이미지 삽입시 되게 해라!!!! */
                }else{
                    if(!NAME_OK){
                        join_et_input_name.requestFocus();
                        Toast.makeText(test_join.this, "이름을 바르게 입력해주세요.", Toast.LENGTH_SHORT).show();

                    }else if(!AGE_OK){
                        Toast.makeText(test_join.this, "나이를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        join_et_input_age.requestFocus();
                    }/*else if(!SEX_OK){

                    }*/else if(!EMAIL_OK){
                        join_et_input_id.requestFocus();
                        Toast.makeText(test_join.this, "이메일을 바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }else if(!PW_OK){
                        join_et_input_pw.requestFocus();
                        Toast.makeText(test_join.this, "패스워드를 바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }else if(!PWCHK_OK){
                        join_et_input_pwchk.requestFocus();
                        Toast.makeText(test_join.this, "패스워드 확인을 바르게 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });

        /*1.이메일 형식 예외처리*/
        join_et_input_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user_email = ((EditText)findViewById(R.id.join_et_input_id)).getText().toString();
                TextView tv_email_noti = (TextView)findViewById(R.id.tv_email_noti);

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {

                        tv_email_noti.setText("이메일 형식이 아닙니다.");
                        tv_email_noti.setBackgroundColor(Color.WHITE);
                        EMAIL_OK=false;
                    } else {
                        tv_email_noti.setText("");
                        tv_email_noti.setBackgroundColor(Color.GREEN);
                        EMAIL_OK=true;
                    }
                    if(count ==0){
                        tv_email_noti.setText("");
                        tv_email_noti.setBackgroundColor(Color.WHITE);
                        EMAIL_OK=false;
                    }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String user_email = ((EditText)findViewById(R.id.join_et_input_id)).getText().toString();
                TextView tv_email_noti = (TextView)findViewById(R.id.tv_email_noti);

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {

                    tv_email_noti.setText("이메일 형식이 아닙니다.");
                    tv_email_noti.setBackgroundColor(Color.WHITE);
                    EMAIL_OK=false;
                } else {
                    tv_email_noti.setText("");
                    tv_email_noti.setBackgroundColor(Color.GREEN);
                    EMAIL_OK=true;
                }
                if(user_email.length()==0){
                    tv_email_noti.setText("이메일을 입력해 주세요");
                    tv_email_noti.setBackgroundColor(Color.WHITE);
                    EMAIL_OK=false;
                }
            }
        });

        join_et_input_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                   // Toast.makeText(test_join.this, "포커스오프", Toast.LENGTH_SHORT).show();
                    if(EMAIL_OK){// 이메일 형식이 맞을 때만 중복체크

                         /*이메일 중복체크*/
                        CheckEmail task = new CheckEmail();
                        String user_email = ((EditText)findViewById(R.id.join_et_input_id)).getText().toString();
                        task.execute(user_email);
                    }


                }else{
                    //Toast.makeText(test_join.this, "포커스온", Toast.LENGTH_SHORT).show();
                }
            }
        });

            /*3. 비밀번호 복잡도 예외처리 해야됨*/
        join_et_input_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user_pw = ((EditText)findViewById(R.id.join_et_input_pw)).getText().toString();
                TextView tv_pw_noti =(TextView)findViewById(R.id.tv_pw_noti);
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", user_pw))
                {
                    if(user_pw.length()==0){
                        tv_pw_noti.setText("");
                        tv_pw_noti.setBackgroundColor(Color.WHITE);
                        PW_OK=false;
                    }
                    else if(user_pw.length()<8){// 길이가 짧을 때
                        tv_pw_noti.setText("비밀번호는 8~20자리로 입력해 주세요.");
                        tv_pw_noti.setBackgroundColor(Color.WHITE);
                        PW_OK=false;
                    }else{ //길이가 적당할 때
                        tv_pw_noti.setText("비밀번호는 영문, 숫자, 특수문자의 조합으로 만들어 주세요");
                        tv_pw_noti.setBackgroundColor(Color.WHITE);
                        PW_OK=false;
                    }

                    //Toast.makeText(test_join.this,"비밀번호 형식을 지켜주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    tv_pw_noti.setText("");
                    tv_pw_noti.setBackgroundColor(Color.GREEN);
                    PW_OK=true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        join_et_input_pwchk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user_pwchk = ((EditText)findViewById(R.id.join_et_input_pwchk)).getText().toString();
                String user_pw = ((EditText)findViewById(R.id.join_et_input_pw)).getText().toString();
                TextView tv_pwchk_noti =(TextView)findViewById(R.id.tv_pwchk_noti);
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", user_pw))
                {
                    if(user_pw.length()==0){
                        tv_pwchk_noti.setText("");
                        tv_pwchk_noti.setBackgroundColor(Color.WHITE);
                        PWCHK_OK=false;
                    }
                    else if(user_pw.length()<8){// 길이가 짧을 때
                        tv_pwchk_noti.setText("비밀번호는 8~20자리로 입력해 주세요.");
                        tv_pwchk_noti.setBackgroundColor(Color.WHITE);
                        PWCHK_OK=false;
                    }else{ //길이가 적당할 때
                        tv_pwchk_noti.setText("비밀번호는 영문, 숫자, 특수문자의 조합으로 만들어 주세요");
                        tv_pwchk_noti.setBackgroundColor(Color.WHITE);
                        PWCHK_OK=false;
                    }

                    //Toast.makeText(test_join.this,"비밀번호 형식을 지켜주세요.",Toast.LENGTH_SHORT).show();
                }else if(!user_pwchk.equals(user_pw)){
                    tv_pwchk_noti.setText("두개의 비밀번호가 일치하지 않습니다.");
                    tv_pwchk_noti.setBackgroundColor(Color.WHITE);
                    PWCHK_OK=false;
                }else{
                    tv_pwchk_noti.setText("");
                    tv_pwchk_noti.setBackgroundColor(Color.GREEN);
                    PWCHK_OK=true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*이름 한글 또는 영어만 입력 가능하게 ㄱ*/
        join_et_input_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user_name = ((EditText)findViewById(R.id.join_et_input_name)).getText().toString();
                TextView tv_name_noti =(TextView)findViewById(R.id.tv_name_noti);
                if(!Pattern.matches("^[a-zA-Z가-힣]*$", user_name)){
                    tv_name_noti.setText("영문 또는 한글만 입력 가능합니다.");
                    tv_name_noti.setBackgroundColor(Color.WHITE);
                    NAME_OK=false;
                }else{
                    tv_name_noti.setText("");
                    tv_name_noti.setBackgroundColor(Color.GREEN);
                    NAME_OK=true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }




    /*회원테이블에 회원정보 넣는 스레드*/
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(test_join.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Toast.makeText(test_join.this, result, Toast.LENGTH_SHORT).show();

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String user_email = (String)params[0];
            String user_name = (String)params[1];
            String user_pwd = (String)params[2];
            String user_age = (String)params[3];
            String user_sex = (String)params[4];

            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/join_insert.php";
            String postParameters = "user_email=" + user_email + "&user_name=" + user_name + "&user_pwd=" + user_pwd + "&user_age=" + user_age + "&user_sex=" + user_sex;


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

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


    /*이메일 중복 체크하는 스레드*/
    class CheckEmail extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        EditText join_et_input_id = (EditText)findViewById(R.id.join_et_input_id);
        TextView tv_email_noti = (TextView)findViewById(R.id.tv_email_noti);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(test_join.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            //Toast.makeText(test_join.this, result, Toast.LENGTH_SHORT).show();
            if(result.equals("o")){ // 아이디 중복일 때 o 아닐 때 x가 날라옴
                //Toast.makeText(test_join.this, "아이디 중복", Toast.LENGTH_SHORT).show();
                tv_email_noti.setText("중복된 이메일 입니다.");
                tv_email_noti.setBackgroundColor(Color.WHITE);
                EMAIL_OK=false;
                join_et_input_id.requestFocus();
            }else{// 아닐 때 사용 가능
                tv_email_noti.setText("");
                tv_email_noti.setBackgroundColor(Color.GREEN);
                EMAIL_OK=true;
            }
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String user_email = (String)params[0];

            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/join_email_check.php";
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
