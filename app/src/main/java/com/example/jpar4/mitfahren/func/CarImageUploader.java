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
public class CarImageUploader {

    /**
     * Upload URL of your folder with php file name...
     * You will find this file in php_upload folder in this project
     * You can copy that folder and paste in your htdocs folder...
     */
    private static final String URL_UPLOAD_IMAGE = "http://13.124.251.123/upload/car_image_upload.php";

    /*public static JSONObject uploadImage(String sourceImageFile) {*/
    public static JSONObject uploadCarImage(Bitmap roundImage, String user_car_photo) {// sourceImageFile = imagePath

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
            String folder_name = user_car_photo.substring(0,user_car_photo.lastIndexOf("/"));
            String filename = user_car_photo.substring(user_car_photo.lastIndexOf("/")); // ex) user_car_photo = qlql@qlqlcom/car_qlql.jpg
                                                                                                                    //(폴더명) / (파일명)


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
                    .addFormDataPart("uploaded_file", filename, RequestBody.create(MEDIA_TYPE_PNG,  bitmapdata)) // ex) xx. png
                    .addFormDataPart("folder_name", folder_name) //폴더명
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
