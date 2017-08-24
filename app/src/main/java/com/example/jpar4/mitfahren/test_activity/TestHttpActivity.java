package com.example.jpar4.mitfahren.test_activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.func.HttpAssoci_Func;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class TestHttpActivity extends AppCompatActivity {
    EditText mEditAddr;
    TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_http);
        mEditAddr = (EditText)findViewById(R.id.editAddr);
        mTextMessage = (TextView)findViewById(R.id.textMessage);

        // 네트워크 접속 여부를 구한다
     /*   if( isNetConnect() )
            mTextMessage.setText("Network is connected");
        else
            mTextMessage.setText("Network is disconnected");*/

        if(HttpAssoci_Func.isNetConnect(this.getApplicationContext()) )
            mTextMessage.setText("Network is connected");
        else
            mTextMessage.setText("Network is disconnected");
    }

    // 네트워크 접속 여부 반환
    public boolean isNetConnect() {
        try {
            ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected())
                return true;
        } catch (Exception e) {
            Log.d("tag", "Connection state error");
        }
        return false;
    }

    // HTTP 요청 결과 반환
    public String getHttpConnResult(String strUrl) {
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

    public void onBtnHttp1() {
        String addr = "http://m.cafe.naver.com/tizenity";
        // 쓰레드에 주소를 전달해서 데이터 요청 시작
        new HttpReqTask().execute(addr);
        // HTTP 요청 결과 반환
        //String response = getHttpConnResult(addr);
        //mTextMessage.setText(response);
    }

    // 아파치 HTTP 클라이언트를 사용해서 요청 결과 반환
    public String getHttpClientResult(String addr) {
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

    public void onBtnHttp2() {
        String juso = mEditAddr.getText().toString();
        String addr = "http://maps.googleapis.com/maps/api/geocode/json?address="
                + juso.replaceAll(" ", "%20") + "&sensor=false";
        new HttpReqTask().execute("", addr);
    }

    public void onBtnHttp3() {
        String juso = mEditAddr.getText().toString();
        String addr = "http://maps.googleapis.com/maps/api/geocode/json?address="
                + juso.replaceAll(" ", "%20") + "&sensor=false";
        new HttpReqTask().execute("", "", addr);
    }

    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.btnHttp1 :
                onBtnHttp1();
                break;
            case R.id.btnHttp2 :
                onBtnHttp2();
                break;
            case R.id.btnHttp3 :
                onBtnHttp3();
                break;
        }
    }

    private class HttpReqTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... arg) {
            String response = "";
            if( arg.length == 1 ) {
                return (String)getHttpConnResult(arg[0]);
            }

            response = getHttpClientResult(arg[ arg.length-1 ]);
            if( arg.length == 3 ) {
                try {
                    JSONObject jOb = new JSONObject(response).getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    String lat = jOb.getString("lat");
                    String lng = jOb.getString("lng");
                    response = lat + " / " + lng;
                }
                catch(JSONException e) {
                    Log.d("tag", "Parse Error");
                }
            }

            return response;
        }

        protected void onPostExecute(String result) {
            mTextMessage.setText(result);
        }
    }

}