package com.example.jpar4.mitfahren.func;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jpar4 on 2017-09-09.
 * 사용안함
 */

public class ParsePathJson extends AsyncTask<String, Integer, ArrayList<LatLng>> {
    // Parsing the data in non-ui thread

    int totalDistance;
    ArrayList<com.google.android.gms.maps.model.LatLng> mapPoints; //LatLng 변수

    @Override
    protected ArrayList<com.google.android.gms.maps.model.LatLng> doInBackground(String... jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData[0]); //통제이슨을 받아옴
            JSONArray features = jsonObject.getJSONArray("features"); // json 이름 형상 정보 입니다. (geojson 표준 규격) //키값 : Type과 features중 features가져옴

            mapPoints = new ArrayList<>(); //지도에 찍을 맵 좌표값을 저장할 어레이리스트를 만듬

            for (int i = 0; i < features.length(); i++) { // 블로그 참고 하였다.
                JSONObject test2 = features.getJSONObject(i);
                if (i == 0) {
                    JSONObject properties = test2.getJSONObject("properties"); // 프로퍼티에는 전체 거리 전체 시간 택시요금 네비안내 다음에올길이름등 정보가 들어있다.
                    totalDistance += properties.getInt("totalDistance"); // 경로 총 길이(단위: m)입니다. - pointType=S 일때 응답되는 정보입니다.

                }

                JSONObject geometry = test2.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates"); //좌표 정보입니다.
                String geoType = geometry.getString("type"); // 구간의 정보입니다.

                if (geoType.equals("Point")) { // 꼭지점에 해당하는 Point 객체
                    double lonJson = coordinates.getDouble(0);
                    double latJson = coordinates.getDouble(1);

//                        Log.d("TAG", "-");
//                        Log.d("TAG", lonJson + "," + latJson + "\n");
                    com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);
                    mapPoints.add(point);

                }

                if (geoType.equals("LineString")) { // 선에 해당하는 LineString 객체
                    for (int j = 0; j < coordinates.length(); j++) {
                        JSONArray JLinePoint = coordinates.getJSONArray(j);
                        double lonJson = JLinePoint.getDouble(0);
                        double latJson = JLinePoint.getDouble(1);

//                            Log.d("TAG", "-");
//                            Log.d("TAG", lonJson + "," + latJson + "\n");
                        com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);
                        mapPoints.add(point);

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapPoints;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(ArrayList<com.google.android.gms.maps.model.LatLng> result) {
        PolylineOptions lineOptions = null;
        lineOptions = new PolylineOptions();

        for (int i = 0; i < result.size(); i++) {
            com.google.android.gms.maps.model.LatLng point = result.get(i);

            LatLng position = new LatLng(point.latitude, point.longitude);

//                Log.e("position", position + "");
//                points.add(position);
//                Log.e("points", points + "");

            // 출발지 마커
    /*        if (i == result.size() - 1) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(position);
                markerOptions.title("출발지");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                marker = mGoogleMap.addMarker(markerOptions);


                // 출발지로 맵이동
                CameraPosition cp = new CameraPosition.Builder().target((position)).zoom(15).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
            }
*/
            // Adding all the points in the route to LineOptions
            lineOptions.add(position);
            lineOptions.width(20);
            lineOptions.color(Color.RED);
        }

        // 폴리라인을 그려준다.
      //  line = mGoogleMap.addPolyline(lineOptions);
        // 처음 맵이 그려질 때는 line변수가 없기 때문에 line변수가 있는지 없는 지 구별해줄 조건이 필요했음
     //   intialLine = true;

    }
}
