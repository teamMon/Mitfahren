package com.example.jpar4.mitfahren.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.func.MRRoundedImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class JoinActivity extends AppCompatActivity {
    final int PICK_FROM_ALBUM = 2001;
    final int AFTER_FROM_ALBUM = 2002;


    EditText join_et_input_name;
    EditText join_et_input_age;
    EditText join_et_input_pw;
    EditText join_et_input_pwchk;
    Button join_btn_confirm;
    ImageButton join_ibtn_uploadprofile;
    ImageView join_iv_person;
    private Uri mImageCaptureUri;
    Bitmap roundimage_bitmap;

boolean picOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        join_et_input_name = (EditText) findViewById(R.id.join_et_input_name);
        join_et_input_age = (EditText) findViewById(R.id.join_et_input_age);
        join_et_input_pw = (EditText) findViewById(R.id.join_et_input_pw);
        join_et_input_pwchk = (EditText) findViewById(R.id.join_et_input_pwchk);
        join_btn_confirm = (Button) findViewById(R.id.join_btn_confirm);
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


        join_btn_confirm.setOnClickListener(new View.OnClickListener() {
            Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
            @Override
            public void onClick(View v) {
                if(join_et_input_name.getText().toString().equals("")){//유저네임이 공백이면
                    Toast.makeText(JoinActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();

                    findViewById(R.id.join_et_input_name).startAnimation(shake);
                    join_et_input_name.requestFocus();
                }
                else if(join_et_input_age.getText().toString().equals("") ){
                    Toast.makeText(JoinActivity.this, "주민등록번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.join_et_input_age).startAnimation(shake);
                    join_et_input_age.requestFocus();

                }
                else if(join_et_input_age.getText().toString().length()<7){
                    Toast.makeText(JoinActivity.this, "조건에 맞게 입력해주시기 바랍니다..", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.join_et_input_age).startAnimation(shake);
                    join_et_input_age.requestFocus();

                }
                else if(join_et_input_pw.getText().toString().equals("")){
                    Toast.makeText(JoinActivity.this, "Password를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    join_et_input_pw.requestFocus();
                }
                else if(join_et_input_pwchk.getText().toString().equals("")){
                    Toast.makeText(JoinActivity.this, "Password Confirm을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    join_et_input_pwchk.requestFocus();
                }
                else if(!(join_et_input_pw.getText().toString().equals(join_et_input_pwchk.getText().toString()))){//비밀번호가 다르면
                    Toast.makeText(JoinActivity.this, "두 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    join_et_input_pw.setText("");
                    join_et_input_pwchk.setText("");
                    join_et_input_pw.requestFocus();
                }
                //비밀번호 체크
                else if(join_et_input_pw.getText().toString().equals(join_et_input_pwchk.getText().toString())){

                }
                else{//그밖의 에러

                }
            }
        });

    }

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
    public void saveBitmaptoJpeg(Bitmap bitmap,String folder, String name){
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
}
