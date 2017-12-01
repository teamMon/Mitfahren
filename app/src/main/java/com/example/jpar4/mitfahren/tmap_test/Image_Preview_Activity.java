package com.example.jpar4.mitfahren.tmap_test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.func.ChattingImageUploader;
import com.example.jpar4.mitfahren.imgaefilter.JJFilters;
import com.example.jpar4.mitfahren.imgaefilter.ThumbnailCallback;
import com.example.jpar4.mitfahren.imgaefilter.ThumbnailItem;
import com.example.jpar4.mitfahren.imgaefilter.ThumbnailsAdapter;
import com.example.jpar4.mitfahren.imgaefilter.ThumbnailsManager;
import com.example.jpar4.mitfahren.service.MyService;
import com.zomato.photofilters.imageprocessors.Filter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/*
* 사진찍은후 이미지 보여주는 화면
*
*
* */
public class Image_Preview_Activity extends AppCompatActivity implements View.OnClickListener, ThumbnailCallback{

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("NativeImageProcessor");
    }

    /*app객체 (로그인에 정보 이용에사용)*/
    Myapp app;
    /*app객체 (로그인에 정보 이용에 사용)*/
    private Activity activity;
    private RecyclerView thumbListView;

    TextView tv_preview_send;
    ImageView iv_preview_image;

    Uri imageURI= null;
    String carpool_ID = null;
    Bitmap imageBitmap;
    Bitmap imageBitmapFilterd = null;
    String imagePath;
    String file_lastthree = null; // 확장자
    String img_file_name = null; // 서버의 파일 주소

    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    MyService mService; //서비스객체
    boolean mBound;
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image__preview_activity);
         /*app객체*/
        app = (Myapp)getApplicationContext();
        activity = this;
        tv_preview_send = (TextView) findViewById(R.id.tv_preview_send);
        tv_preview_send.setOnClickListener(this);
        iv_preview_image = (ImageView) findViewById(R.id.iv_preview_image);

        Intent intent;
        intent =getIntent();
        imageURI=  intent.getParcelableExtra("imageURI");
        carpool_ID = intent.getStringExtra("carpool_ID");
        imageBitmap = intent.getParcelableExtra("imageBitmap");
        file_lastthree = intent.getStringExtra("file_lastthree");

        if(imageURI != null){
            iv_preview_image.setImageURI(imageURI);

        }else{
           // String aaa = app.getUser_email().substring(0,app.getUser_email().lastIndexOf("@"));
          //  Toast.makeText(app, aaa, Toast.LENGTH_SHORT).show();
            iv_preview_image.setImageBitmap(imageBitmap);// 이거 아래도 있음 둘중 하나 지워도 됨.
            /*필터 적용떄문에 업로드는 전송 누를떄 함.*/
   /*       UploadAsync upload_task = new UploadAsync();
            upload_task.execute();*/
        }
        initUIWidgets();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_preview_send :
                /*파일 서버에 업로드*/
                UploadAsync upload_task = new UploadAsync();
                upload_task.execute();
                break;

        }
    }
    /*----------------------------------------------이미지업로드------------------------------------------*/
     /*이미지 올리는 쓰레드*/
     /*이미지 올리는 쓰레드*/
    class UploadAsync extends AsyncTask<Void, Integer, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
 /*           progressDialog = new ProgressDialog(Image_Preview_Activity.this);
            progressDialog.setMessage("잠시만 기다려 주세요.");
            progressDialog.show();*/
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
/*            if (progressDialog != null)
                progressDialog.dismiss();*/

            if (aBoolean){
                Log.e("ddd 채팅이미지", "성공");
                //Toast.makeText(getApplicationContext(), R.string.string_upload_success, Toast.LENGTH_LONG).show();   // 확장자가 달라지면 파일이 두개 올라가는 경우가 있지만 뭐 크게 무리 없을 듯

                /**/

                                  /*---------------------------------------------------------------tcp통신으로 이미지메시지 전송----------------------------------------------------------------띠용------------------*/
                JSONObject sendMsgObj = new JSONObject();
                try{
                        /*
                        * 보내는 정보
                        * 1. 메시지 타입 2. 보내는 사람 이메일 3. 받는 사람 이메일 4. 등록된 카풀정보의 아이디(유일값) 5. 탑승자 출발 도착 좌표
                        *   메시지는 2, 이미지전송은 3,
                        *
                        * */
                    //핵심꼬임
                    sendMsgObj.put("kindOfmsg", "3"); //    kindOfmsg : 1 = carpool_applying ; 2 : chatting; 3 : 채팅 이미지 전송송
                    sendMsgObj.put("carpool_id", carpool_ID); // 등록된 카풀정보의 아이디(유일값), 방번호 어떻게 할지 생각해보고
                    //  sendMsgObj.put("sender", app.getUser_email()); // sender : 보내는 사람
                    sendMsgObj.put("sender", app.getUser_email());
                    sendMsgObj.put("sender_name", app.getUser_name());
                    sendMsgObj.put("sender_pic", app.getUser_photo());
                    //   sendMsgObj.put("receiver", item_new_driver_info.getUser_email()); // receiver : 받는 사람
                    //   sendMsgObj.put("receiver", "z@z.com");
                    sendMsgObj.put("contents", "(사진)");
                    sendMsgObj.put("img_file_name",img_file_name);


                    mService.sendMsg(sendMsgObj.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
   /*---------------------------------------------------------------tcp통신으로 이미지메시지 전송----------------------------------------------------------------------------------*/


                Intent intent = new Intent(Image_Preview_Activity.this, ChattingRoomActivity.class);
                intent.putExtra("imageURI", imageURI); // 이건 ver1 용 지금 사진 망해서 안씀.
                intent.putExtra("send_image", "send_image");
                intent.putExtra("carpool_ID", carpool_ID);
                // intent.putExtra("imageBitmap", imageBitmapFilterd); // 보내는 이미지
                /*
                * 필터된 이미지를 저장해서 전송하려했는데 에러가 자꾸 액티비티 관련 에러가 뜸
                * 그래서 원래있던 이미지를 보내봤더니 필터된 이미지가 전송됨. 추가한 라이브러리에서 원래이미지를 필터된이미지로 바꿔주는것같음.
                * 따라서 위에 있는 imageBitmapFilterd 이놈은 필요없어짐.
                * */
                intent.putExtra("imageBitmap", imageBitmap); // 원본 이미지
                intent.putExtra("img_file_name", img_file_name);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();


                /**/
            }
            else{
                Log.e("ddd 채팅이미지", "실패");
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

                Date date = new Date();
                String new_date = (new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(date));
                String new_time = (new SimpleDateFormat("hhmmss").format(date)); //
                String foloer_name = "chatting_img_"+new_date;  //폴더명 이미지가 저장되어있는
                img_file_name = foloer_name + "/" + app.getUser_email().substring(0,app.getUser_email().lastIndexOf("@"))+new_time+file_lastthree; // 파일명 : 폴더명+"/"+아이디+시간+확장자(file_lastthree = imagePath.substring(imagePath.lastIndexOf("."));
                Log.e("ddd img_file_name", img_file_name);

                JSONObject jsonObject = ChattingImageUploader.uploadChattingImage(imageBitmapFilterd, img_file_name);

                               //   foloer_name = app.getUser_email().substring(0, app.getUser_email().lastIndexOf(".") ) + app.getUser_email().substring(app.getUser_email().lastIndexOf(".") + 1);//폴더명 이미지 저장되어 있는
                 //   user_car_photo = foloer_name+"/car_"+new_date+foloer_name.substring(0,foloer_name.lastIndexOf("@"))+imagePath.substring(imagePath.lastIndexOf(".")); // 폴더명+ "/car_" + 파일명+ 확장자
                if (jsonObject != null)
                    return jsonObject.getString("result").equals("success");

            } catch (JSONException e) {
                Log.i("TAG", "Error : " + e.getLocalizedMessage());
            }
            return false;
        }
    }
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
          //  mService.registerCallback(mCallback); //콜백 등록
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
        // startService(intent);
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

    /*----------------------------------------이미지필터 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private void initUIWidgets() {
        thumbListView = (RecyclerView) findViewById(R.id.thumbnails);
       // placeHolderImageView = (ImageView) findViewById(R.id.place_holder_imageview);
        iv_preview_image = (ImageView) findViewById(R.id.iv_preview_image); // 위에거 이걸로 대체
        //iv_preview_image.setImageBitmap(BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.photo), 640, 640, false));
        iv_preview_image.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, 640, 640, false));
        initHorizontalList();
    }

    private void initHorizontalList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        thumbListView.setHasFixedSize(true);
        bindDataToAdapter();
    }

    private void bindDataToAdapter() {
        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                //Bitmap thumbImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.photo), 640, 640, false);
                Bitmap thumbImage = Bitmap.createScaledBitmap(imageBitmap, 640, 640, false);
                ThumbnailItem t1 = new ThumbnailItem();
                ThumbnailItem t2 = new ThumbnailItem();
                ThumbnailItem t3 = new ThumbnailItem();
                ThumbnailItem t4 = new ThumbnailItem();
                ThumbnailItem t5 = new ThumbnailItem();
                ThumbnailItem t6 = new ThumbnailItem();
                ThumbnailItem t7 = new ThumbnailItem();
     /*           ThumbnailItem t8 = new ThumbnailItem();
                ThumbnailItem t9 = new ThumbnailItem();
                ThumbnailItem t10 = new ThumbnailItem();*/

                t1.image = thumbImage;
                t2.image = thumbImage;
                t3.image = thumbImage;
                t4.image = thumbImage;
                t5.image = thumbImage;
                t6.image = thumbImage;
                t7.image = thumbImage;
 /*               t8.image = thumbImage;
                t9.image = thumbImage;
                t10.image = thumbImage;*/

                ThumbnailsManager.clearThumbs();
                ThumbnailsManager.addThumb(t1); // Original Image

                t2.filter = JJFilters.BrightnessSubfilter();
                ThumbnailsManager.addThumb(t2);

                t3.filter = JJFilters.getStarLitFilter();
                ThumbnailsManager.addThumb(t3);

                t4.filter = JJFilters.getBlueMessFilter();
                ThumbnailsManager.addThumb(t4);

                t5.filter = JJFilters.getAweStruckVibeFilter();
                ThumbnailsManager.addThumb(t5);

                t6.filter = JJFilters.getLimeStutterFilter();
                ThumbnailsManager.addThumb(t6);

                t7.filter = JJFilters.getNightWhisperFilter();
                ThumbnailsManager.addThumb(t7);

/*                t8.filter = JJFilters.ColorOverlaySubfilter();
                ThumbnailsManager.addThumb(t8);

                t9.filter = JJFilters.ContrastSubfilter();
                ThumbnailsManager.addThumb(t9);

                t10.filter = JJFilters.BrightnessSubfilter();
                ThumbnailsManager.addThumb(t10);*/

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) activity);
                thumbListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    @Override
    public void onThumbnailClick(Filter filter) {
        imageBitmapFilterd =filter.processFilter(Bitmap.createScaledBitmap(imageBitmap, 640, 640, false));
        iv_preview_image.setImageBitmap(imageBitmapFilterd);
    /*아래로하면 이미지 전송이 안됨.*/
/*        iv_preview_image.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(imageBitmap, 640, 640, false)));*/
    }
    /*----------------------------------------이미지필터 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
}
