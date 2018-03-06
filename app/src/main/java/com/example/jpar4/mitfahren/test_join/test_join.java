package com.example.jpar4.mitfahren.test_join;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.activity.LoginActivity;
import com.example.jpar4.mitfahren.func.ImageUploader;
import com.example.jpar4.mitfahren.func.InternetConnection;
import com.example.jpar4.mitfahren.func.MRRoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
/*
*
*
* 회원등록하는 화면
*
* */
public class test_join extends AppCompatActivity{
    private static String TAG = "test_join.java";

    /**
     * Context Variables
     */
    Context mContext;
    /**
     * Image path to send
     */
    String imagePath;
    String foloer_name;

    final int PICK_FROM_ALBUM = 2001;
    final int AFTER_FROM_ALBUM = 2002;
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

    /*프로필 이미지 등록*/
    ImageButton join_ibtn_uploadprofile;
    ImageView join_iv_person;
    private Uri mImageCaptureUri;
    Bitmap roundimage_bitmap;
    boolean picOk = false;
    /*프로필 이미지 등록*/


    Boolean EMAIL_OK = false, PW_OK= false, PWCHK_OK= false, NAME_OK= false, AGE_OK= false, SEX_OK= false,PIC_OK=false;//이메일 형식에 맞을때 True;EMAIL_OK,PW_OK,PWCHK_OK



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("회원 가입");

        setContentView(R.layout.test_join_activity);
        mContext = getApplicationContext();


        /*프로필 사진 등록*/
        join_ibtn_uploadprofile = (ImageButton) findViewById(R.id.join_ibtn_uploadprofile);
        join_iv_person = (ImageView) findViewById(R.id.join_iv_person);
        join_ibtn_uploadprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

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
        /*--------------------------------------------------------------버튼 온클릭 ------------------------------------------------------------------------------------------*/
        join_btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(test_join.this, EMAIL_OK.toString()+PW_OK+PWCHK_OK+"", Toast.LENGTH_SHORT).show();
                /*이메일, 이름, 비밀번호, 비밀번호확인, 나이, 성별 값 가져옴*/
                /*1. 이메일 형식 예외처리 해야됨 2. 이메일 중복 예외처리 해야됨 (20170905 jpar424) */
                /*3. 비밀번호 복잡도 예외처리 해야됨 4, 나이 텍스트뷰 마이너스 안되게, 그리고 최대 3자리로 예외처리 해야됨, 숫자만 입력할 수 있도록 함*/
                /*5. 비밀번호와 비밀번호 확인의 값이 같은지 체크해야됨*/
    /*            if(join_et_input_age.getText().length()==0) {
                    AGE_OK=false;
                }else{
                    AGE_OK=true;
                }*/
                if(NAME_OK && EMAIL_OK && PW_OK && PWCHK_OK && AGE_OK && PIC_OK){ //이메일 패스워드 패스워트 확인 통과해야지 인서트문 날림
                 /*이메일, 이름, 비밀번호, 비밀번호확인, 나이, 성별 값 가져옴*/
                    String user_email = join_et_input_id.getText().toString();
                    String user_name = join_et_input_name.getText().toString();
                    String user_pwd = join_et_input_pw.getText().toString();
                    String user_pwdchk = join_et_input_pwchk.getText().toString();
                    String user_age = join_et_input_age.getText().toString();
                    String user_sex = join_spn_sex.getSelectedItem().toString();
                    //String user_photo = join_et_input_name.getText().toString();


                /*파일 업로드*/
                    //if(!TextUtils.isEmpty(imagePath) ){
                        if (InternetConnection.checkConnection(mContext)) {
                            foloer_name = user_email.substring(0, user_email.lastIndexOf(".") ) + user_email.substring(user_email.lastIndexOf(".") + 1);
                            UploadAsync upload_task = new UploadAsync();
                            upload_task.execute();
                        } else {
                            Snackbar.make(findViewById(R.id.parentView), R.string.string_internet_connection_warning, Snackbar.LENGTH_INDEFINITE).show();
                        }
                   // }else {
                    //    Snackbar.make(findViewById(R.id.parentView), R.string.string_message_to_attach_file, Snackbar.LENGTH_INDEFINITE).show();
                  //  }

                    /*데이터 php로 전송*/
                    InsertData task = new InsertData();
                    task.execute(user_email,user_name,user_pwd,user_age,user_sex);

                /*입력 필드 초기화 초기화 해줘야되나??*/
                  /*  join_et_input_id.setText("");
                    join_et_input_name.setText("");
                    join_et_input_pw.setText("");
                    join_et_input_pwchk.setText("");
                    join_et_input_age.setText("");*/
                    //join_spn_sex -> 이거 여성일 때, 남성으로 바뀌게 초기화해야됨.

                /*현재 이미지를 선택하지 않았을 때, nobody.jpg로 값이 저장 안됨. 이미지 삽입시 되게 해라!!!! */
                }else{
                    if(!NAME_OK){
                        join_et_input_name.requestFocus();
                        Toast.makeText(test_join.this, "이름을 바르게 입력해주세요.", Toast.LENGTH_SHORT).show();

                    }else if(!AGE_OK){
                        Toast.makeText(test_join.this, "나이를 바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
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
                    }else if(!PIC_OK){
                        Snackbar.make(findViewById(R.id.parentView), R.string.string_message_to_attach_file, Snackbar.LENGTH_LONG).show();
                        //Snackbar.LENGTH_INDEFINITE하면 안없어짐.
                    }
                }



            }
        });
        /*나이 19~110까지 제한*/
        join_et_input_age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user_name = ((EditText) findViewById(R.id.join_et_input_age)).getText().toString();
                TextView tv_age_noti = (TextView) findViewById(R.id.tv_age_noti);

                if (user_name.length() == 0) {
                    tv_age_noti.setText("");
                    tv_age_noti.setTextColor(Color.RED);
                    AGE_OK = false;
                } else if (Integer.parseInt(user_name) < 19 || Integer.parseInt(user_name)> 110) {// 길이가 짧을 때
                    tv_age_noti.setText("19세이상 110세이하 성인남녀만 가입할 수 있습니다.");
                    /*tv_age_noti.setBackgroundColor(Color.argb(0, 0, 0, 0));*/
                    tv_age_noti.setTextColor(Color.RED);
                    AGE_OK = false;
                } else{
                    tv_age_noti.setText("");
                    /*tv_pw_noti.setBackgroundColor(Color.GREEN);*/
                    tv_age_noti.setTextColor(Color.GREEN);
                    tv_age_noti.setText("사용 가능합니다.");
                    AGE_OK = true;
                }
        }

            @Override
            public void afterTextChanged(Editable s) {

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
                        tv_email_noti.setBackgroundColor(Color.argb(0,0,0,0));
                        tv_email_noti.setTextColor(Color.RED);
                        EMAIL_OK=false;
                    } else {
                        tv_email_noti.setText("");
                        /*tv_email_noti.setBackgroundColor(Color.GREEN);*/
                        tv_email_noti.setTextColor(Color.GREEN);
                        tv_email_noti.setText("사용 가능합니다.");

                        EMAIL_OK=true;
                    }
                    if(count ==0){
                        tv_email_noti.setText("");
                        tv_email_noti.setBackgroundColor(Color.argb(0,0,0,0));
                        tv_email_noti.setTextColor(Color.RED);
                        EMAIL_OK=false;
                    }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String user_email = ((EditText)findViewById(R.id.join_et_input_id)).getText().toString();
                TextView tv_email_noti = (TextView)findViewById(R.id.tv_email_noti);

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {

                    tv_email_noti.setText("이메일 형식이 아닙니다.");
                    tv_email_noti.setBackgroundColor(Color.argb(0,0,0,0));
                    tv_email_noti.setTextColor(Color.RED);
                    EMAIL_OK=false;
                } else {
                    tv_email_noti.setText("");
                    /*tv_email_noti.setBackgroundColor(Color.GREEN);*/
                    tv_email_noti.setTextColor(Color.GREEN);
                    tv_email_noti.setText("사용 가능합니다.");
                    EMAIL_OK=true;
                }
                if(user_email.length()==0){
                    tv_email_noti.setText("이메일을 입력해 주세요");
                    tv_email_noti.setBackgroundColor(Color.argb(0,0,0,0));
                    tv_email_noti.setTextColor(Color.RED);
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
                        tv_pw_noti.setBackgroundColor(Color.argb(0,0,0,0));
                        tv_pw_noti.setTextColor(Color.RED);
                        PW_OK=false;
                    }
                    else if(user_pw.length()<8){// 길이가 짧을 때
                        tv_pw_noti.setText("비밀번호는 8~20자리로 입력해 주세요.");
                        tv_pw_noti.setBackgroundColor(Color.argb(0,0,0,0));
                        tv_pw_noti.setTextColor(Color.RED);
                        PW_OK=false;
                    }else{ //길이가 적당할 때
                        tv_pw_noti.setText("비밀번호는 영문, 숫자, 특수문자의 조합으로 만들어 주세요");
                        tv_pw_noti.setBackgroundColor(Color.argb(0,0,0,0));
                        tv_pw_noti.setTextColor(Color.RED);
                        PW_OK=false;
                    }

                    //Toast.makeText(test_join.this,"비밀번호 형식을 지켜주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    tv_pw_noti.setText("");
                    /*tv_pw_noti.setBackgroundColor(Color.GREEN);*/
                    tv_pw_noti.setTextColor(Color.GREEN);
                    tv_pw_noti.setText("사용 가능합니다.");
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
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", user_pwchk))
                {
                    if(user_pwchk.length()==0){
                        tv_pwchk_noti.setText("");
                       /* tv_pwchk_noti.setBackgroundColor(Color.WHITE);*/
                        tv_pwchk_noti.setBackgroundColor(Color.argb(0,0,0,0));
                        tv_pwchk_noti.setTextColor(Color.RED);
                        PWCHK_OK=false;
                    }
                    else if(user_pwchk.length()<8){// 길이가 짧을 때
                        tv_pwchk_noti.setText("비밀번호는 8~20자리로 입력해 주세요.");

                        tv_pwchk_noti.setBackgroundColor(Color.argb(0,0,0,0));
                        tv_pwchk_noti.setTextColor(Color.RED);
                        PWCHK_OK=false;
                    }else{ //길이가 적당할 때
                        tv_pwchk_noti.setText("비밀번호는 영문, 숫자, 특수문자의 조합으로 만들어 주세요");

                        tv_pwchk_noti.setBackgroundColor(Color.argb(0,0,0,0));
                        tv_pwchk_noti.setTextColor(Color.RED);
                        PWCHK_OK=false;
                    }

                    //Toast.makeText(test_join.this,"비밀번호 형식을 지켜주세요.",Toast.LENGTH_SHORT).show();
                }else if(!user_pwchk.equals(user_pw)){
                    tv_pwchk_noti.setText("두개의 비밀번호가 일치하지 않습니다.");

                    tv_pwchk_noti.setBackgroundColor(Color.argb(0,0,0,0));
                    tv_pwchk_noti.setTextColor(Color.RED);
                    PWCHK_OK=false;
                }else{
                    tv_pwchk_noti.setText("");
                  /*  tv_pwchk_noti.setBackgroundColor(Color.GREEN);*/
                    tv_pwchk_noti.setTextColor(Color.GREEN);
                    tv_pwchk_noti.setText("사용 가능합니다.");
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
                    tv_name_noti.setBackgroundColor(Color.argb(0,0,0,0));
                    tv_name_noti.setTextColor(Color.RED);
                    NAME_OK=false;
                }else if(user_name.length()==0){
                    tv_name_noti.setText("");
                    tv_name_noti.setTextColor(Color.RED);
                    NAME_OK=false;
                }else if(user_name.length()==1){
                    tv_name_noti.setText("이름은 두글자 이상 적어주세요.");
                    tv_name_noti.setTextColor(Color.RED);
                    NAME_OK=false;
                }
                else{
            /*        tv_name_noti.setBackgroundColor(Color.GREEN);*/
                    tv_name_noti.setTextColor(Color.GREEN);
                    tv_name_noti.setText("사용 가능합니다.");
                    NAME_OK=true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    /*이미지 올리는 쓰레드*/
    class UploadAsync extends  AsyncTask<Void, Integer, Boolean>{
            ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(test_join.this);
            progressDialog.setMessage(getString(R.string.string_title_upload_progressbar_));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog != null)
                progressDialog.dismiss();

            /*파일 업로드 성공 토스트 */
/*            if (aBoolean)
                Toast.makeText(getApplicationContext(), R.string.string_upload_success, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), R.string.string_upload_fail, Toast.LENGTH_LONG).show();*/

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject jsonObject = ImageUploader.uploadImage(roundimage_bitmap, imagePath, foloer_name);
                if (jsonObject != null)
                    return jsonObject.getString("result").equals("success");

            } catch (JSONException e) {
                Log.i("TAG", "Error : " + e.getLocalizedMessage());
            }
            return false;
        }
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

            /*로그인 액티비티로 이동*/
            Intent intent = new Intent(test_join.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

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
            String user_photo = foloer_name+"/"+foloer_name.substring(0,foloer_name.lastIndexOf("@"))+imagePath.substring(imagePath.lastIndexOf(".")); // 폴더명+ "/" + 파일명+ 확장자

            String serverURL = "http://ec2-13-124-251-123.ap-northeast-2.compute.amazonaws.com/db/join_insert.php";
            String postParameters = "user_email=" + user_email + "&user_name=" + user_name + "&user_pwd=" + user_pwd + "&user_age=" + user_age + "&user_sex=" + user_sex + "&user_photo=" + user_photo;
            Log.e("ddd", user_photo);

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
                tv_email_noti.setBackgroundColor(Color.argb(0,0,0,0));
                tv_email_noti.setTextColor(Color.RED);
                EMAIL_OK=false;
                join_et_input_id.requestFocus();
            }else{// 아닐 때 사용 가능
                tv_email_noti.setText("");
                /*tv_email_noti.setBackgroundColor(Color.GREEN);*/
                tv_email_noti.setTextColor(Color.GREEN);
                tv_email_noti.setText("사용 가능합니다.");
                EMAIL_OK=true;
            }
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
            String user_email = (String)params[0];

            String serverURL = "http://ec2-13-124-251-123.ap-northeast-2.compute.amazonaws.com/db/join_email_check.php";
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

    /*----------------------------------------------프로필사진등록------------------------------------------*/
    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1052: {
                // If request is cancelled, the result
                // arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED ){

                    // permission was granted.

                } else {
                    // Permission denied - Show a message
                    // to inform the user that this app only works
                    // with these permissions granted
                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissions();
                }
                picOk = true;
                mImageCaptureUri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                /*추가추가*/
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(mImageCaptureUri, filePathColumn, null, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imagePath = cursor.getString(columnIndex);

            /*        Picasso.with(mContext).load(new File(imagePath))
                            .transform(PicassoTransformations.resizeTransformation)
                            .into(imageView);*/
                    cursor.close();

                }

                /*추가추가*/
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, AFTER_FROM_ALBUM);

       /*         try {
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView) findViewById(R.id.join_iv_person);
                    Bitmap image_bitmap2 = MRRoundedImageView.getCroppedBitmap(image_bitmap, 1024);
                    image.setImageBitmap(image_bitmap2);


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;
            }
            case AFTER_FROM_ALBUM :
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    ImageView image = (ImageView) findViewById(R.id.join_iv_person);
                    roundimage_bitmap = MRRoundedImageView.getCroppedBitmap(photo, 1024);
                    image.setImageBitmap(roundimage_bitmap);
                    PIC_OK=true;
                    //saveBitmaptoJpeg(image_bitmap2, "imagefolder","123");

                }

        /*        // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }
*/
                break;
            default: {
                Toast.makeText(getBaseContext(), "에러발생" , Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
    public void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            //   app.setProfilePath(file_name);///////////////////////////////////////////////////////////////////////////////////////////////////
        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
    /*----------------------------------------------프로필사진등록------------------------------------------*/
}
