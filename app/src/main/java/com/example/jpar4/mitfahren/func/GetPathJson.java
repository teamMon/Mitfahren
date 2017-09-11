package com.example.jpar4.mitfahren.func;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



/**
 * Created by jpar4 on 2017-09-09.
 * 사용안함
 */

public class GetPathJson extends AsyncTask<String, Void, String> {
    // 자동차 안내 tmap
        String startLat;
        String startLng;
        String endLat;
        String endLng;


        @Override
        protected String doInBackground(String... params) {
            /*시작 위도, 경도, 도착 위도, 경도*/
            startLat = params[0];
            startLng = params[1];
            endLat = params[2];
            endLng = params[3];
            String result="";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://apis.skplanetx.com/tmap/routes?version=1&appKey=eb2b21b5-65d8-3686-9664-1442352bab42&startX=" + startLng + "&startY=" + startLat + "&endX=" + endLng + "&endY=" + endLat + "&reqCoordType=WGS84GEO&resCoordType=WGS84GEO")
                    .build(); // 이 url에 좌표를 전송하여 json구조를 전송받는다. 구글의 좌표(위도, 경도)와 다음의 좌표 (경도, 위도) 순서가 다르다
            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("ddd[GetPathJson]출도json", result);
            // 결과값을 파싱해주는 스레드에 넘김
/*            TParserTask tParserTask = new TParserTask();
            tParserTask.execute(result);*/
        }
 }

