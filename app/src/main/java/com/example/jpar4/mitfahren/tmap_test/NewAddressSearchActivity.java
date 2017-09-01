package com.example.jpar4.mitfahren.tmap_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.jpar4.mitfahren.R;


public class NewAddressSearchActivity extends AppCompatActivity {
    /*주소받아오기 위한 REQUESTCODE*/
    private static final int GET_ADDRESS_REQUEST_CODE = 3001;
    int REQUEST_CODE;

    private WebView webView;
  //  private TextView result;
    private Handler handler;
    Boolean start_arrive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        start_arrive = intent.getBooleanExtra("start_arrive", true);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if(start_arrive) ab.setTitle("출발지 검색");
        else ab.setTitle("도착지 검색");

        setContentView(R.layout.new_activity_address_search);

        //result = (TextView) findViewById(R.id.result);


        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
    }
    public void init_webView() {
        // WebView 설정
        webView = (WebView) findViewById(R.id.webView);
        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        // 두 번째 파라미터는 사용될 php에도 동일하게 사용해야함
        webView.addJavascriptInterface(new AndroidBridge(), "TestApp");
        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());
        // webview url load
        /*
        * 지금 아래 Url 주소는 내가 만든 php파일이 아니므로 후에 안될 수 있음 차후에 php파일을 내 써버에 올릴 것!
        * */
      /*  webView.loadUrl("http://codeman77.ivyro.net/getAddress.php");*/

        webView.loadUrl("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/getAddress.php");
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                  /*  String result_msg = String.format("(%s) %s %s", arg1, arg2, arg3);*/
                    String result_msg = String.format("%s %s", arg2, arg3);
                    //result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
           //         result.setText(String.format(result_msg));
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    //init_webView();
                    /*Intent intent = new Intent(TestSearchActivity.this, com.example.jpar4.mitfahren.activity.SearchActivity.class);
                    startActivity(intent);*/
                   // if(REQUEST_CODE==(GET_ADDRESS_REQUEST_CODE)){
                        Intent intent = getIntent();
                        intent.putExtra("start_arrive", start_arrive);
                        intent.putExtra("result_msg", result_msg);
                        setResult(RESULT_OK, intent);
                        finish();
                   // }
                }
            });

        }
    }


}
