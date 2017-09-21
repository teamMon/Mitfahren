package com.example.jpar4.mitfahren.tmap_test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.func.ImageUploader;
import com.example.jpar4.mitfahren.func.InternetConnection;
import com.example.jpar4.mitfahren.func.MRRoundedImageView;
import com.squareup.picasso.Picasso;

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

public class NewUserPageActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "NewUserPage";
    final int PICK_FROM_ALBUM = 2001;
    final int AFTER_FROM_ALBUM = 2002;
    /*app객체 (로그인에 정보 이용에사용)*/
    Myapp app;
    /*app객체 (로그인에 정보 이용에 사용)*/
    /**
     * Context Variables
     */
    Context mContext;
    /**
     * Image path to send
     */
    String imagePath;
    String foloer_name;

    /*화면 View 객체 세팅*/
    ImageView iv_user_info_pic;
    TextView tv_user_info_email, tv_user_info_name, tv_user_info_age, tv_user_info_sex, tv_user_info_total_rider, tv_user_info_total_riding;
    Button btn_user_info_pic_edit, btn_user_info_confirm;
    /*화면 View 객체 세팅*/
     /*프로필 이미지 등록*/
    private Uri mImageCaptureUri;
    Bitmap roundimage_bitmap;
    boolean picOk = false, PIC_OK=false;
    /*프로필 이미지 등록*/
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

        btn_user_info_pic_edit = (Button)findViewById(R.id.btn_user_info_pic_edit); //프로필 사진 변경
        btn_user_info_pic_edit.setOnClickListener(this);
        /*만약 app이메일 정보와 전달받은 이메일이 다를 경우 버튼이 안보이게 구현해야됨----------------------------------------------------------------------------------------------!!!*/

        btn_user_info_confirm = (Button)findViewById(R.id.btn_user_info_confirm); //확인
        btn_user_info_confirm.setOnClickListener(this);

        /*
        * 1. 로그인 했을 때, app객체 유무로 자기 정보를 표시할 것 ( http통신 필요 없음)
         * 2. app 객체가 없을 때 이메일을 인텐트로 넘겨받아 디비에서 데이터를 받아옴( http통신 필요)
        */

        Intent intent = getIntent();
        /*에드드라이버2에서 날라올때*/
        String  user_email = intent.getStringExtra("user_email");
        GetDriverInfo task = new GetDriverInfo();
        task.execute(user_email);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user_info_pic_edit: //프로필 사진 변경
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_FROM_ALBUM);
                break;

            case R.id.btn_user_info_confirm: //프로필 사진 변경
              if(PIC_OK){//프로필 사진 변경
                  if (InternetConnection.checkConnection(mContext)) {
                      foloer_name = app.getUser_email().substring(0, app.getUser_email().lastIndexOf(".") ) + app.getUser_email().substring(app.getUser_email().lastIndexOf(".") + 1);
                      UploadAsync upload_task = new UploadAsync();
                      upload_task.execute();
                      finish();
                  } else {
                      Snackbar.make(findViewById(R.id.parentView), R.string.string_internet_connection_warning, Snackbar.LENGTH_INDEFINITE).show();//-> parentView이거 없어서 오류날수도 있음
                  }
              }else{// 아니면 그냥 finish()
                  finish();
              }
                break;

        }
    }

    /*-----------------------------------------------------------------------디비에서 운전자 정보를 가져옮-----------------------------------------------------------------------*/

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
                    ImageView image = (ImageView) findViewById(R.id.iv_user_info_pic);
                    roundimage_bitmap = MRRoundedImageView.getCroppedBitmap(photo, 1024);
                    image.setImageBitmap(roundimage_bitmap);
                    //saveBitmaptoJpeg(image_bitmap2, "imagefolder","123");
                    PIC_OK=true;

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
    /*이미지 올리는 쓰레드*/
    class UploadAsync extends  AsyncTask<Void, Integer, Boolean>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(NewUserPageActivity.this);
            progressDialog.setMessage(getString(R.string.string_title_upload_progressbar_));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog != null)
                progressDialog.dismiss();

            if (aBoolean){
                Toast.makeText(getApplicationContext(), R.string.string_upload_success, Toast.LENGTH_LONG).show();
                Picasso.with(mContext).invalidate("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/"+app.getUser_photo());
             //   PicassoClear.clearImageDiskCache(mContext);


/*                Cache cache = new LruCache();
// Save this 'cache' object somewhere.
                Picasso picasso = new Picasso.Builder()
                        .setCache(cache)
                        .build();
                Picasso.setSingletonInstance(picasso);

// Later...
                cache.clear();*/
            }
            else
                Toast.makeText(getApplicationContext(), R.string.string_upload_fail, Toast.LENGTH_LONG).show();

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

}
