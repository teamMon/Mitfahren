package com.example.jpar4.mitfahren.func;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jpar4 on 2017-08-11.
 */

public class HttpAssoci_Func {

    // 네트워크 접속 여부 반환
    public static boolean isNetConnect(Context context) {
        try {
            ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected())
                return true;
        } catch (Exception e) {
            Log.d("tag", "Connection state error");
        }
        return false;
    }

    // HTTP 요청 결과 반환
    public static String getHttpConnResult(String strUrl) {
        String line, result = new String();

        try {
            // Http 클라이언트 생성
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 접속 정보 설정
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // 접속 시작
            conn.connect();

            // 데이터 추출
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            while((line = reader.readLine()) != null) {
                result += line + '\n';
                if( result.length() > 2000 ) break;
            }
            // 접속 종료
            reader.close();
            conn.disconnect();
        }
        catch(Exception e) {
            Log.d("tag", "HttpURLConnection error");
        }
        return result;
    }

    // 아파치 HTTP 클라이언트를 사용해서 요청 결과 반환
    public  static String getHttpClientResult(String addr) {
        String line, result = new String();
        // 클라이언트 객체 생성
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(addr);

        try {
            // 서버와 접속해서 응답 결과를 구한다
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if( statusCode == 200 ) {
                // 데이터 추출
                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                while((line = reader.readLine()) != null) {
                    result += line + '\n';
                    if( result.length() > 2000 ) break;
                }
                reader.close();
            }
        } catch( IOException e) {
            Log.d("tag", "Http error-2");
        }
        return result;
    }

}
