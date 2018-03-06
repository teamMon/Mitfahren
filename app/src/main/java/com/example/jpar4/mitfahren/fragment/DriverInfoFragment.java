package com.example.jpar4.mitfahren.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.ItemView.ItemCarpoolListForDriverView;
import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverInfoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "DriverInfoFragment";
    //private RecyclerView searchList;
    Context context;
    Activity mActivity;
    ListView listView;
    DriverInfoAdapter adapter;
    /*app객체 (로그인에 사용)*/
    Myapp app;
    SwipeRefreshLayout mSwipeRefreshLayout;
//TEST CODE
/*    String[] names = {"소녀시대", "AOA", "IOI", "걸스데이"};
    String[] ages = {"28", "22", "20", "25"};*/

    public DriverInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_fragment_driver_info, container, false);
      //  searchList = (RecyclerView) view.findViewById(R.id.driver_search_list);
    //    searchList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        context=getContext();
        mActivity = getActivity();
        app = (Myapp)context.getApplicationContext();
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setOnRefreshListener(this);

        listView = (ListView) view.findViewById(R.id.listView_item_carpool);
        adapter = new DriverInfoAdapter();
//TEST CODE
      /*  adapter.addItem(new Item_New_Driver_Info(names[0], ages[0])); // 데이터 추가
        adapter.addItem(new Item_New_Driver_Info(names[1], ages[1]));
        adapter.addItem(new Item_New_Driver_Info(names[2], ages[2]));
        adapter.addItem(new Item_New_Driver_Info(names[3], ages[3]));*/

/*데이터 추가*/
        GetDriverInfo task = new GetDriverInfo();
        task.execute();
/*데이터 추가*/

        listView.setAdapter(adapter); // 리스트에 어댑터를 추가
        return view;
    }

    @Override
    public void onRefresh() {
        adapter.clearItems();
/*데이터 추가*/
        GetDriverInfo task = new GetDriverInfo();
        task.execute();
/*데이터 추가*/

        // 새로고침 완료
        mSwipeRefreshLayout.setRefreshing(false);
    }

    class DriverInfoAdapter extends BaseAdapter {
        ArrayList<Item_New_Driver_Info> items = new ArrayList<>(); // 아이템 보관하기 위한 소스

        public void clearItems(){
            items.clear();
         /*   int count = items.size();
            for(int i =0; i<count;i++){
                items.remove(i);
            }*/
        }

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

            ItemCarpoolListForDriverView view = null;
            if (convertView == null) { // 뷰가 없으면 생성
                view = new ItemCarpoolListForDriverView(context.getApplicationContext(), mActivity);
            } else {// 재사용
                view = (ItemCarpoolListForDriverView) convertView;
            }

            Item_New_Driver_Info curItem = items.get(position);// 현재 아이템
            view.setStart(curItem.getUser_start());
            view.setArrive(curItem.getUser_arrive());
            view.setItem_new_driver_info(curItem);
            return view;
        }
        public void idDescSort(){
            Comparator<Item_New_Driver_Info> idDesc = new Comparator<Item_New_Driver_Info>() {
                @Override
                public int compare(Item_New_Driver_Info item1, Item_New_Driver_Info item2) {
                    int ret = 0 ;

                    if (Integer.parseInt(item1.getItem_id()) < Integer.parseInt(item2.getItem_id()))
                        ret = 1 ;
                    else if (Integer.parseInt(item1.getItem_id()) ==  Integer.parseInt(item2.getItem_id()))
                        ret = 0 ;
                    else
                        ret = -1 ;

                    return ret ;
                    //-1이랑 1이랑 바꾸면 내림차순으로 바뀜
                }
            };
            Collections.sort(items, idDesc) ;
        }
    }
      /*-----------------------------------------------------------------------디비에서 운전자 정보를 가져옮-----------------------------------------------------------------------*/

    class GetDriverInfo extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(context,
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
                    if(app.getUser_email().equals(arr.getJSONObject(i).getString("user_email"))){ // 현재 로그인한 사람과 등록된 이메일이 같을 경우만 보여줌
                        Item_New_Driver_Info item = new Item_New_Driver_Info();
                        item.setItem_id(arr.getJSONObject(i).getString("0")); // item_id
                        item.setCarpool_id(arr.getJSONObject(i).getString("id"));
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
                }
                adapter.idDescSort();
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

            String serverURL = "http://ec2-13-124-251-123.ap-northeast-2.compute.amazonaws.com/db/testgetalldriverinfo.php";
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
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    Double.parseDouble(Latitude),
                    Double.parseDouble(Longitude),
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(context, "지오코더 서비스2 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스2 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

}
