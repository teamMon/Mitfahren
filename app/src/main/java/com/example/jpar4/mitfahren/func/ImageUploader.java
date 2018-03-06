package com.example.jpar4.mitfahren.func;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.Bitmap.CompressFormat.PNG;


/**
 * Created by Pratik Butani
 */
public class ImageUploader {

    /**
     * Upload URL of your folder with php file name...
     * You will find this file in php_upload folder in this project
     * You can copy that folder and paste in your htdocs folder...
     */
    private static final String URL_UPLOAD_IMAGE = "http://13.124.251.123/upload/image_upload.php"; //13.124.251.123 http://52.78.6.238/upload/image_upload.php

    /*public static JSONObject uploadImage(String sourceImageFile) {*/
    public static JSONObject uploadImage(Bitmap roundImage, String sourceImageFile, String folder_name) {// sourceImageFile = imagePath

        try {
            /*File sourceFile = new File(sourceImageFile);
            Picasso.with(mContext).load(new File(imagePath))
                    .transform(PicassoTransformations.resizeTransformation)*/
            //File sourceFile = new File(sourceImageFile);


            //Bitmap b = BitmapFactory.decodeFile(sourceImageFile);
            Bitmap bResized = Bitmap.createScaledBitmap(roundImage, 400, 400, false);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bResized.compress(PNG, 0 /*ignored for PNG*/, stream);
            byte[] bitmapdata = stream.toByteArray();





            //Log.d("TAG", "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            //String filename = sourceImageFile.substring(sourceImageFile.lastIndexOf("/")+1);
            String email_id = folder_name.substring(0,folder_name.lastIndexOf("@"));
            String filename = sourceImageFile.substring(sourceImageFile.lastIndexOf(".")); //하면 확장자 마지막 .부터 끝까지 자르기. => 확장자


            /**
             * OKHTTP2
             */
//            RequestBody requestBody = new MultipartBuilder()
//                    .type(MultipartBuilder.FORM)
//                    .addFormDataPart("member_id", memberId)
//                    .addFormDataPart("file", "profile.png", RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
//                    .build();

            /**
             * OKHTTP3
             */
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    /*.addFormDataPart("uploaded_file", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))*/
                    /*.addFormDataPart("uploaded_file", filename, RequestBody.create(MEDIA_TYPE_PNG,  bitmapdata))*/
                    .addFormDataPart("uploaded_file", email_id+filename, RequestBody.create(MEDIA_TYPE_PNG,  bitmapdata))
                    .addFormDataPart("folder_name", folder_name)
                    .build();

            Request request = new Request.Builder()
                    .url(URL_UPLOAD_IMAGE)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Log.e("TAG", "Error: " + res);
            return new JSONObject(res);

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e("TAG", "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("TAG", "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }
}
