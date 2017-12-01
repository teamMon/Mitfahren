package com.example.jpar4.mitfahren.ItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.adapter.ChattingAdapter;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.apprtc.ConnectActivity;
import com.example.jpar4.mitfahren.model.Item_Chatting;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by jpar4 on 2017-11-02.
 */

public class ItemChattingView extends RelativeLayout implements View.OnClickListener, View.OnLongClickListener{

    Myapp app;
    Context context;
    Activity mActivity;
    ChattingAdapter chattingAdapter;
    int position;

    ImageView msg_sender_pic;
    TextView msg_sender_name, msg_received_content, msg_received_time, msg_sent_content, msg_sent_time;

    ImageView img_received_content;// 받은 이미지
    TextView img_received_time;// 받은 이미지 시간
    ImageView img_sent_content;// 보낸 이미지
    TextView img_sent_time;// 보낸 이미지 시간

    Item_Chatting item_chatting;
    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    SQLiteDatabase sqliteDB;
    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ItemChattingView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public ItemChattingView(Context context, Activity mActivity,ChattingAdapter chattingAdapter, int position ) {
        super(context);
        this.context = context;
        this.mActivity = mActivity;
        this.chattingAdapter = chattingAdapter;
        this.position = position;

        init(context);
    }

    public ItemChattingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //인플래이션
        inflater.inflate(R.layout.item_chatting_msg, this, true);
        app = (Myapp) context.getApplicationContext();
        msg_sender_pic = (ImageView) findViewById(R.id.msg_sender_pic);
        msg_sender_pic.setOnClickListener(this);
        msg_sender_pic.setOnLongClickListener(this);


        msg_sender_name = (TextView) findViewById(R.id.msg_sender_name);
        msg_received_content = (TextView) findViewById(R.id.msg_received_content);
        msg_received_content.setOnClickListener(this);
        msg_received_content.setOnLongClickListener(this);

        msg_received_time = (TextView) findViewById(R.id.msg_received_time);
        msg_sent_content = (TextView) findViewById(R.id.msg_sent_content);
        msg_sent_content.setOnClickListener(this);
        msg_sent_content.setOnLongClickListener(this);

        msg_sent_time = (TextView) findViewById(R.id.msg_sent_time);

        img_received_content = (ImageView) findViewById(R.id.img_received_content);
        img_received_content.setOnClickListener(this);
        img_received_content.setOnLongClickListener(this);

        img_received_time = (TextView) findViewById(R.id.img_received_time);

        img_sent_content = (ImageView) findViewById(R.id.img_sent_content);
        img_sent_content.setOnClickListener(this);
        img_sent_content.setOnLongClickListener(this);

        img_sent_time = (TextView) findViewById(R.id.img_sent_time);
         /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
        sqliteDB = init_database();
        init_tables();

        /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    }

    public void setItem_Chatting(Item_Chatting item, int mPosition) { //img_file_name: chatting_img_20171117/z014615.jpg
        item_chatting = item;
        this.position = mPosition;
       // Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + Item_Chatting.getUser_pic()).into(invitable_user_pic);
        if(item_chatting.isSent()){ //보내는거
            if(item_chatting.getImg_file_name() != null){ // 보낸 이미지 주소가 있으면 = 이미지 전송
                msg_sent_content.setVisibility(GONE);
                msg_sent_time.setVisibility(GONE);
                msg_sender_pic.setVisibility(GONE);
                msg_sender_name.setVisibility(GONE);
                msg_received_content.setVisibility(GONE);
                msg_received_time.setVisibility(GONE);
                img_received_content.setVisibility(GONE);
                img_received_time.setVisibility(GONE);

                img_sent_content.setVisibility(VISIBLE); //보낸 이미지
                img_sent_time.setVisibility(VISIBLE); //보낸 이미지 시간

                Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getImg_file_name()).into(img_sent_content);
                img_sent_time.setText(item_chatting.getSent_time());

            }
            else{ // 메시지 전송
                img_sent_content.setVisibility(GONE);
                img_sent_time.setVisibility(GONE);
                msg_sent_content.setVisibility(VISIBLE); // 보낸 메시지
                msg_sent_time.setVisibility(VISIBLE); // 보낸 메시지 시간

                msg_sender_pic.setVisibility(GONE);
                msg_sender_name.setVisibility(GONE);
                msg_received_content.setVisibility(GONE);
                msg_received_time.setVisibility(GONE);
                img_received_content.setVisibility(GONE);
                img_received_time.setVisibility(GONE);

                msg_sent_content.setText(item_chatting.getSent_content());
                msg_sent_time.setText(item_chatting.getSent_time());
            }


        }else{ // 빋는거
            if(item_chatting.getImg_file_name() != null) { // 받은 이미지 주소가 있으면 = 이미지 받음
                img_received_content.setVisibility(VISIBLE); //받은 이미지
                img_received_time.setVisibility(VISIBLE); // 받은 이미지 시간
                msg_sender_pic.setVisibility(VISIBLE); // 보낸 사람 프로필
                msg_sender_name.setVisibility(VISIBLE); // 보낸 사람 이름
                msg_received_content.setVisibility(GONE);
                msg_received_time.setVisibility(GONE);

                msg_sent_content.setVisibility(GONE);
                msg_sent_time.setVisibility(GONE);
                img_sent_content.setVisibility(GONE);
                img_sent_time.setVisibility(GONE);

                Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getImg_file_name()).into(img_received_content);
                img_received_time.setText(item_chatting.getReceived_time());

                msg_sender_name.setText(item_chatting.getSender_name());
                Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getSender_pic()).into(msg_sender_pic);
            }
            else{// 메시지 받음
                img_received_content.setVisibility(GONE);
                img_received_time.setVisibility(GONE);
                msg_sender_pic.setVisibility(VISIBLE); // 보낸 사람 프로필
                msg_sender_name.setVisibility(VISIBLE); // 보낸 사람 이름
                msg_received_content.setVisibility(VISIBLE); // 받은 메시지
                msg_received_time.setVisibility(VISIBLE); // 받은 메시지 시간

                msg_sent_content.setVisibility(GONE);
                msg_sent_time.setVisibility(GONE);
                img_sent_content.setVisibility(GONE);
                img_sent_time.setVisibility(GONE);

                msg_sender_name.setText(item_chatting.getSender_name());
                msg_received_content.setText(item_chatting.getReceived_content());
                msg_received_time.setText(item_chatting.getReceived_time());
                Picasso.with(context).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getSender_pic()).into(msg_sender_pic);
            }
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_sender_pic:
                Intent intent = new Intent(mActivity, ConnectActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                /*intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/
                intent.putExtra("profile_pic", item_chatting.getSender_pic());
                intent.putExtra("prfile_name", item_chatting.getSender_name());
                intent.putExtra("carpool_ID", item_chatting.getCarpool_id());
                String new_time = (new SimpleDateFormat("hhmmss").format(new Date())); //
                intent.putExtra("room_ID", "roomid"+new_time);
                context.startActivity(intent);
                break;
            case R.id.img_received_content:
                Toast.makeText(app, "받은사진클릭"+item_chatting.getMsg_id()+ " "+position, Toast.LENGTH_SHORT).show();
                break;
            case R.id.msg_received_content:
                Toast.makeText(app, "받은메시지클릭"+item_chatting.getMsg_id()+ " "+position, Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_sent_content:
                Toast.makeText(app, "보낸사진클릭"+item_chatting.getMsg_id()+ " "+position, Toast.LENGTH_SHORT).show();
                break;
            case R.id.msg_sent_content:
                Toast.makeText(app, "보낸메시지클릭"+item_chatting.getMsg_id()+ " "+position, Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.img_received_content:
            //    Toast.makeText(app, "받은사진클릭", Toast.LENGTH_SHORT).show();
                makeDialog("img");
                break;
            case R.id.msg_received_content:
            //    Toast.makeText(app, "받은메시지클릭", Toast.LENGTH_SHORT).show();
                makeDialog("msg");
                break;
            case R.id.img_sent_content:
       //        Toast.makeText(app, "보낸사진클릭", Toast.LENGTH_SHORT).show();
                makeDialog("img");
                break;
            case R.id.msg_sent_content:
           //     Toast.makeText(app, "보낸메시지클릭", Toast.LENGTH_SHORT).show();
                makeDialog("msg");
                break;
        }
        return false;
    }
    public void makeDialog(final String checkcheck){ // checkcheck는 이게 뭔지 구분해주는거 텍스트 메시지인지, 이미지인지
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        String firstItem = null;
        if(checkcheck.equals("msg") ){
            firstItem = "텍스트 복사";
        }
        else { //이미지 일때
            firstItem = "이미지 저장";
        }
        //  final CharSequence[] items = {"앨범선택", "사진촬영", "취소"};
        final CharSequence[] items = {firstItem, "삭제", "취소"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);     // 여기서 this는 Activity의 this

// 여기서 부터는 알림창의 속성 설정
        builder.setTitle("작업선택")        // 제목 설정
                .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index){
                        switch (index){
                            case 0:
                                // Toast.makeText(getApplicationContext(), items[index], Toast.LENGTH_SHORT).show();
                                if(checkcheck.equals("msg") ){
                                    if(item_chatting.isSent()) { //보내는거
                                        textCopy(item_chatting.getSent_content());
                                    }
                                    else{
                                        textCopy(item_chatting.getReceived_content());
                                    }


                                }
                                else { //이미지 일때
                                    //Toast.makeText(context, "11", Toast.LENGTH_SHORT).show();
                                    String imgUrl = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + item_chatting.getImg_file_name();
                                    new ImageDownload().execute(imgUrl);
                                }

                                break;
                            case 1: //삭제
                                dialogtDeleteCheck();
                             /*   delete_msg();
                                chattingAdapter.deleteItem(position);
                                chattingAdapter.notifyDataSetChanged();*/

                                //Toast.makeText(getApplicationContext(), items[index], Toast.LENGTH_SHORT).show();
                                // captureCamera();
                                // captureCamera2();
                                break;
                            case 2:
                                break;
                        }

                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    /**
     * Image SDCard Save (input Bitmap -> saved file JPEG)
     * Writer intruder(Kwangseob Kim)
     * @param bitmap : input bitmap file
     * @param folder : input folder name
     * @param name   : output file name
     */
    public static void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name){
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

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }

    /**
     * url 이미지 다운로드
     *
     */

    private class ImageDownload extends AsyncTask<String, Void, Void> {
        /**
         * 파일명
         */
        private String fileName;
        /**
         * 저장할 폴더
         */
        private final String SAVE_FOLDER = "/Pictures";

        @Override
        protected Void doInBackground(String... params) {

           // File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "chatting_pic");
            //다운로드 경로를 지정
            String savePath = Environment.getExternalStorageDirectory().toString() + SAVE_FOLDER;

            File dir = new File(savePath);

            //상위 디렉토리가 존재하지 않을 경우 생성
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //파일 이름 :날짜_시간
            Date day = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
            fileName = String.valueOf(sdf.format(day));

/*            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + ".jpg";*/

            //웹 서버 쪽 파일이 있는 경로
            String fileUrl = params[0];

            //다운로드 폴더에 동일한 파일명이 존재하는지 확인
            if (new File(savePath + "/" + fileName).exists() == false) {
            } else {

            }


            String localPath = savePath + "/" + fileName + ".jpg";

            try {
                URL imgUrl = new URL(fileUrl);
                //서버와 접속하는 클라이언트 객체 생성
                HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                int len = conn.getContentLength();
                byte[] tmpByte = new byte[len];

                //입력 스트림을 구한다
                InputStream is = conn.getInputStream();
                File file = new File(localPath);

                //파일 저장 스트림 생성
                FileOutputStream fos = new FileOutputStream(file);
                int read;

                //입력 스트림을 파일로 저장
                for (;;) {
                    read = is.read(tmpByte);
                    if (read <= 0) {
                        break;
                    }
                    fos.write(tmpByte, 0, read); //file 생성
                }

                is.close();
                fos.close();
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //저장한 이미지 열기
    //        Intent i = new Intent(Intent.ACTION_VIEW);
      //      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String targetDir = Environment.getExternalStorageDirectory().toString() + SAVE_FOLDER;
            File file = new File(targetDir + "/" + fileName + ".jpg");

            //type 지정 (이미지)
        //    i.setDataAndType(Uri.fromFile(file), "image/*");

//            context.startActivity(i);

            //이미지 스캔해서 갤러리 업데이트
           context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void textCopy(String copytext){
        ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", copytext);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, "복사되었습니다.", Toast.LENGTH_SHORT).show();
    }

    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private SQLiteDatabase init_database() {
        Log.e("ddd", "데이터베이스세팅");
        SQLiteDatabase db = null ;
        // File file = getDatabasePath("chatting.db");
        File file = new File(context.getFilesDir(), "chatting.db");
        Log.e("ddd PATH : ", file.toString());

        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null) ;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (db == null) {
            Log.e("ddd DB creation failed", file.getAbsolutePath().toString()) ; }
        return db ;
    }

    private void init_tables() {
 /*       sendMsgObj.put("kindOfmsg", "2"); //    kindOfmsg : 1 = carpool_applying ; 2 : chatting
        sendMsgObj.put("carpool_id", carpool_ID); // 등록된 카풀정보의 아이디(유일값), 방번호 어떻게 할지 생각해보고
        //  sendMsgObj.put("sender", app.getUser_email()); // sender : 보내는 사람
        sendMsgObj.put("sender", app.getUser_email());
        sendMsgObj.put("sender_name", app.getUser_name());
        sendMsgObj.put("sender_pic", app.getUser_photo());
        //   sendMsgObj.put("receiver", item_new_driver_info.getUser_email()); // receiver : 받는 사람
        //   sendMsgObj.put("receiver", "z@z.com");
        sendMsgObj.put("contents", sent_text);*/

        if (sqliteDB != null) {
     /*       String sqlDelete = "DELETE FROM CHATTING_TABLE" ;
            sqliteDB.execSQL(sqlDelete) ;*/
/*            String sqlDropTbl = "DROP TABLE CHATTING_TABLE" ;
            sqliteDB.execSQL(sqlDropTbl) ;*/


            String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS CHATTING_TABLE (" +
                    "NO "           + "INTEGER PRIMARY KEY NOT NULL," +
                    "MSG_TYPE "         + "TEXT," +
                    "CARPOOL_ID "         + "TEXT," +
                    "SENDER "         + "TEXT," +
                    "SENDER_NAME "         + "TEXT," +
                    "SENDER_PIC "        + "TEXT," +
                    "TIME "        + "TEXT," +
                    "IS_READ "        + "TEXT," +
                    "CONTENTS "       + "TEXT," +
                    "IMG_MSG "        + "TEXT" +")" ;
            //    "OVER20 "       + "INTEGER" + ")" ;

            System.out.println(sqlCreateTbl) ;
            Log.e("ddd sqlCreateTbl", sqlCreateTbl);
            sqliteDB.execSQL(sqlCreateTbl) ;
        }
    }

    private void delete_msg(){ // 설계상의 문제로 정확하지 않을 수 있음 다음에 할때는 PRkey를 각각의 아이템에 저장할 수 있도록 할것.
            String sqlDelete = "DELETE FROM CHATTING_TABLE WHERE CARPOOL_ID="+"'"+item_chatting.getCarpool_id()+"' AND NO=" +"'"+item_chatting.getMsg_id()+"'" ;
            sqliteDB.execSQL(sqlDelete) ;
    }
    /*----------------------------------------채팅 데이터베이스 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    public void dialogtDeleteCheck(){
        AlertDialog.Builder builder
                = new AlertDialog.Builder(mActivity,AlertDialog.THEME_HOLO_DARK);
        builder .setMessage("선택한 메시지를 삭제하시겠습니까?")
                .setPositiveButton("취소", null
                )
                .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                        delete_msg();
                        chattingAdapter.deleteItem(position);
                        chattingAdapter.notifyDataSetChanged();
                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기

    }
}
