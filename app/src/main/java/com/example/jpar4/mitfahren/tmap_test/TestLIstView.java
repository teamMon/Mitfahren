package com.example.jpar4.mitfahren.tmap_test;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.ItemView.TestCarpoolItemView;
import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.model.Item_New_Driver_Info;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestLIstView extends AppCompatActivity {
    private static final String TAG = "TestListView";
    ListView listView;
    TestAdapter adapter;

/*    String[] names = {"소녀시대", "AOA", "IOI", "걸스데이"};
    String[] ages = {"28", "22", "20", "25"};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_test_list_view);

        /*운전자 정보 가져옴*/
        GetDriverInfo2 task = new GetDriverInfo2();
        task.execute();

        listView = (ListView) findViewById(R.id.listView);
        adapter = new TestAdapter();

       /* adapter.addItem(new TestCarpollItem(names[0], ages[0])); // 데이터 추가
        adapter.addItem(new TestCarpollItem(names[1], ages[1]));
        adapter.addItem(new TestCarpollItem(names[2], ages[2]));
        adapter.addItem(new TestCarpollItem(names[3], ages[3]));*/

        listView.setAdapter(adapter); // 리스트에 어댑터를 추가
    }


    class TestAdapter extends BaseAdapter {
            ArrayList<Item_New_Driver_Info> items = new ArrayList<>(); // 아이템 보관하기 위한 소스


            @Override
            public int getCount() { // 리스트뷰가 어댑터한테 리스트 몇 개가지고 있냐고 물어보면 실행하는 메소드
                return items.size();
            }

            public void addItem(Item_New_Driver_Info item) { // 데이터를 추가해주는 메서드
                items.add(item);
            }

            @Override
            public Object getItem(int position) { // position == index
                return items.get(position); // 실제값을 넘겨줌
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) { //View convertView 리스트가 많아지면 메모리가 많이 먹음, 그래서 재사용을 함으로써 재사용을 위한 인자

                TestCarpoolItemView view = null;
                if (convertView == null) { // 뷰가 없으면 생성
                    view = new TestCarpoolItemView(getApplicationContext());
                } else {// 재사용
                    view = (TestCarpoolItemView) convertView;
                }

                Item_New_Driver_Info curItem = items.get(position);// 현재 아이템
                view.setStart(curItem.getUser_start());
                view.setArrive(curItem.getUser_arrive());
                view.setItem_new_driver_info(curItem);
                return view;
            }
        }

    /*-----------------------------------------------------------------------디비에서 운전자 정보를 가져옮-----------------------------------------------------------------------*/

    class GetDriverInfo2 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(TestLIstView.this,
                    "잠시만 기다려 주세요.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            /*result로 JsonArray를 받아와서 Json으로 하나씪 분리해서 값을 뽑아야됨*/
            try{
                //JSONObject obj = new JSONObject(result);
                JSONArray arr = new JSONArray(result);
                ArrayList<Item_New_Driver_Info> itemList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++)
                {
                    Item_New_Driver_Info item = new Item_New_Driver_Info();
                    item.setUser_email(arr.getJSONObject(i).getString("user_email"));
                    item.setUser_start_date(arr.getJSONObject(i).getString("user_start_date"));
                    item.setUser_start_time(arr.getJSONObject(i).getString("user_start_time"));
                    item.setUser_with_poeple(arr.getJSONObject(i).getString("user_with_poeple"));
                    item.setUser_start_lat(arr.getJSONObject(i).getString("user_start_lat"));
                    item.setUser_start_lng(arr.getJSONObject(i).getString("user_start_lng"));
                    item.setUser_arrive_lat(arr.getJSONObject(i).getString("user_arrive_lat"));
                    item.setUser_arrive_lng(arr.getJSONObject(i).getString("user_arrive_lng"));
                    item.setUser_car_photo(arr.getJSONObject(i).getString("user_car_photo"));
                    item.setUser_having_rider(arr.getJSONObject(i).getString("user_having_rider"));
                    item.setUser_carpool_complete(arr.getJSONObject(i).getString("user_carpool_complete"));

                    item.setUser_start(getAddress(arr.getJSONObject(i).getString("user_start_lat"),arr.getJSONObject(i).getString("user_start_lng")));
                    item.setUser_arrive(getAddress(arr.getJSONObject(i).getString("user_arrive_lat"),arr.getJSONObject(i).getString("user_arrive_lng")));

                   // itemList.add(item);
                    adapter.addItem(item);

                    //JSONObject obj = new JSONObject(arr.get(i).toString());
                  //  Log.e("json"+i, arr.get(i).toString());
                    Log.e("ddd json"+i, arr.getJSONObject(i).toString());
                  Log.e("ddd user_email", arr.getJSONObject(i).getString("user_email")) ;
                }
                adapter.notifyDataSetChanged();

                Log.e("ddd",arr.get(0).toString());
            }catch (Exception e){
e.printStackTrace();
            }

            //  Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
/*            String[] arr_result = result.split("#");
            for (int i = 0; i < arr_result.length; i++) {
               Log.e("result check", arr_result[i]);
            }*/

       // Log.e("ddd",result);

        }


        @Override
        protected String doInBackground(String... params) {

            /*user_email,user_name,user_pwd,user_age,user_sex*/
          //  String user_email = (String) params[0];

            String serverURL = "http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/db/testgetalldriverinfo.php";
          ///  String postParameters = "user_email=" + user_email;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
               // outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "EmailCheck: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
/*-------------------------------------------------주소변환---------------------------------------------------------------*/
public String getAddress(String Latitude, String Longitude){

    //지오코더... GPS를 주소로 변환
    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    List<Address> addresses;

    try {

        addresses = geocoder.getFromLocation(
                Double.parseDouble(Latitude),
                Double.parseDouble(Longitude),
                1);
    } catch (IOException ioException) {
        //네트워크 문제
        Toast.makeText(this, "지오코더 서비스2 사용불가", Toast.LENGTH_LONG).show();
        return "지오코더 서비스2 사용불가";
    } catch (IllegalArgumentException illegalArgumentException) {
        Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
        return "잘못된 GPS 좌표";

    }


    if (addresses == null || addresses.size() == 0) {
        Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
        return "주소 미발견";

    } else {
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();
    }

}
}




