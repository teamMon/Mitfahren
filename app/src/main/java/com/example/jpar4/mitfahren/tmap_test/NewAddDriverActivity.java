package com.example.jpar4.mitfahren.tmap_test;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.func.CarImageUploader;
import com.example.jpar4.mitfahren.func.InternetConnection;
import com.example.jpar4.mitfahren.func.MRRoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewAddDriverActivity extends AppCompatActivity {
    /*app객체 (로그인에 정보 이용에사용)*/
    Myapp app;
    /*app객체 (로그인에 정보 이용에 사용)*/
    /**
     * Context Variables
     */
    Context mContext;

    /*시간 날짜*/
    EditText btnpick, dp;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;
    String time = "";
    String date = "";

    EditText timepick, t;
    static final int DIALOG_TID = 1;
    int hour, min;
    /*시간 날짜*/

    /*자동차 사진 등록*/
    final int PICK_FROM_ALBUM = 2001;
    final int AFTER_FROM_ALBUM = 2002;
    ImageButton add_driver_btn_uploadpic;
    ImageView add_driver_iv_car;
    private Uri mImageCaptureUri;
    Bitmap roundimage_bitmap;
    boolean picOk = false;
    boolean PIC_OK=false;
    String imagePath;
    String foloer_name;
    String user_car_photo;
    /*자동차 사진 등록*/

    Spinner add_driver_spin_people_num;
    Button add_driver_btn_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*액션바 이름 변경*/
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("카풀 정보 등록");
        setContentView(R.layout.test_activity_new_add_driver);
         /*app객체*/
        app = (Myapp)getApplicationContext();
        mContext = getApplicationContext();

    /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/
        dp = (EditText) findViewById(R.id.date);
        t = (EditText) findViewById(R.id.time);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
        showDate();
        showTimePickerDialog();
    /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------------자동차 사진------------------------------------------------------------------------------------------*/
        add_driver_btn_uploadpic = (ImageButton) findViewById(R.id.add_driver_btn_uploadpic);
        add_driver_iv_car = (ImageView) findViewById(R.id.add_driver_iv_car);
        add_driver_btn_uploadpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

    /*------------------------------------------------------------------------------------------자동차 사진------------------------------------------------------------------------------------------*/
      /*  final BranchDownloader d = new BranchDownloader(this, url, dp, t, area, search);
        d.execute();*/

        /*성별*/
        add_driver_spin_people_num = (Spinner)findViewById(R.id.add_driver_spin_people_num);
        ArrayAdapter peopleAdapter = ArrayAdapter.createFromResource(this, R.array.add_driver_poeple, android.R.layout.simple_spinner_item);
        peopleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        add_driver_spin_people_num.setAdapter(peopleAdapter);

        /*------------------------------------------------------------------------------------------다음 버튼------------------------------------------------------------------------------------------*/
        add_driver_btn_next = (Button)findViewById(R.id.add_driver_btn_next);
        add_driver_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PIC_OK){//자동차 사진이 없으면
                    Snackbar.make(findViewById(R.id.parentView), "카메라를 눌러 자동차 사진을 등록해 주세요.", Snackbar.LENGTH_LONG).show();
                }else if(btnpick.getText().toString().equals("")){// 날짜가 없으면
                    Toast.makeText(NewAddDriverActivity.this, "날짜를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(timepick.getText().toString().equals("")){
                    Toast.makeText(NewAddDriverActivity.this, "시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(NewAddDriverActivity.this, NewAddDriverActivity2.class);
                    intent.putExtra("date", btnpick.getText().toString());
                    intent.putExtra("time", timepick.getText().toString());
                    intent.putExtra("pNum", add_driver_spin_people_num.getSelectedItem().toString());
                    /*자동차 파일 위치 만들기*/
                    foloer_name = app.getUser_email().substring(0, app.getUser_email().lastIndexOf(".") ) + app.getUser_email().substring(app.getUser_email().lastIndexOf(".") + 1);//폴더명 이미지 저장되어 있는
                    user_car_photo = foloer_name+"/car_"+foloer_name.substring(0,foloer_name.lastIndexOf("@"))+imagePath.substring(imagePath.lastIndexOf(".")); // 폴더명+ "/car_" + 파일명+ 확장자
                    Log.e("my ddd",user_car_photo);
                    intent.putExtra("user_car_photo", user_car_photo);
                    /*자동차 파일 위치 만들기*/
                       /*파일 업로드*/

                    if (InternetConnection.checkConnection(mContext)) {
                        UploadAsync upload_task = new UploadAsync();
                        upload_task.execute();
                    } else {
                        Snackbar.make(findViewById(R.id.parentView), R.string.string_internet_connection_warning, Snackbar.LENGTH_INDEFINITE).show();
                    }
                   // intent.putExtra("roundimage_bitmap", roundimage_bitmap); 쓰바 100kb넘으면 안되는 제약 있어서 그냥 그전에 올리기로함. 왜냐면 대부분 100kb이하지만 넘을수도 있기때문 근대 또 그냥은 안전달되는것 같고 바이트어레이로 바꿔서 전달해야되는 귀찮 그냥 여기서 올려야 겠음

                    startActivity(intent);
                }
            }
        });

        /*------------------------------------------------------------------------------------------다음 버튼------------------------------------------------------------------------------------------*/
    }
    /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/
    public void showTimePickerDialog() {
        timepick = (EditText) findViewById(R.id.time);
        timepick.setFocusable(false);
        timepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TID);
            }
        });
    }

    public void showDate() {
        btnpick = (EditText) findViewById(R.id.date);
        btnpick.setFocusable(false);
        btnpick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }
    /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ID:
                //return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);
                Calendar calendar = Calendar.getInstance();

                calendar.add(Calendar.DATE, 0); // Add 0 days to Calendar
                Date newDate = calendar.getTime();
                datePickerDialog.getDatePicker().setMinDate(newDate.getTime()-(newDate.getTime()%(24*60*60*1000)));
                return datePickerDialog;

            case DIALOG_TID:
                return new TimePickerDialog(this, kTimePickerListener, hour, min, true);

        }
        if (id == DIALOG_ID) {
            //return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);
            Calendar calendar = Calendar.getInstance();

            calendar.add(Calendar.DATE, 0); // Add 0 days to Calendar
            Date newDate = calendar.getTime();
            datePickerDialog.getDatePicker().setMinDate(newDate.getTime()-(newDate.getTime()%(24*60*60*1000)));
            return datePickerDialog;
        }else if (id == DIALOG_TID) {
            return new TimePickerDialog(this, kTimePickerListener, hour, min, false);
        }
        return null;

    }
    /*------------------------------------------------------------------------------------------시간, 날짜------------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------------시간------------------------------------------------------------------------------------------*/
    protected TimePickerDialog.OnTimeSetListener kTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour = hourOfDay;
            min = minute;
            //txtTime1.setText(String.format("%02d:%02d", hourOfDay, minute));
            //hour = hourOfDay % 12;

            t = (EditText) findViewById(R.id.time);
            t.setText(String.format("%02d:%02d", hourOfDay, minute));
            /*t.setText(String.format("%02d:%02d %s", hour == 0 ? 12 : hour,
                    minute, hourOfDay < 12 ? "am" : "pm"));
            *///t.setText(String.format("%02d:%02d", hourOfDay, minute));
            //Toast.makeText(booking.this,hour+" : "+min,Toast.LENGTH_SHORT).show();
        }
    };
    /*------------------------------------------------------------------------------------------날짜------------------------------------------------------------------------------------------*/
    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year_x, month_x, day_x);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = format.format(calendar.getTime());
            date = strDate;
            dp = (EditText) findViewById(R.id.date);
            dp.setText(strDate);
            //Toast.makeText(booking.this, year_x + "-" + month_x + "-" + day_x, Toast.LENGTH_SHORT).show();
        }
    };

    /*----------------------------------------------자동차사진등록------------------------------------------*/
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
                    ImageView image = (ImageView) findViewById(R.id.add_driver_iv_car);
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
    /*----------------------------------------------자동차사진등록------------------------------------------*/
     /*이미지 올리는 쓰레드*/
     /*이미지 올리는 쓰레드*/
    class UploadAsync extends AsyncTask<Void, Integer, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(NewAddDriverActivity.this);
            progressDialog.setMessage("잠시만 기다려 주세요.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog != null)
                progressDialog.dismiss();

            if (aBoolean){
                Log.e("ddd 차사진", "성공");
                //Toast.makeText(getApplicationContext(), R.string.string_upload_success, Toast.LENGTH_LONG).show();   // 확장자가 달라지면 파일이 두개 올라가는 경우가 있지만 뭐 크게 무리 없을 듯
            }
            else{
                Log.e("ddd 차사진", "실패");
              //  Toast.makeText(getApplicationContext(), R.string.string_upload_fail, Toast.LENGTH_LONG).show();
            }


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject jsonObject = CarImageUploader.uploadCarImage(roundimage_bitmap, user_car_photo);
                if (jsonObject != null)
                    return jsonObject.getString("result").equals("success");

            } catch (JSONException e) {
                Log.i("TAG", "Error : " + e.getLocalizedMessage());
            }
            return false;
        }
    }
}
