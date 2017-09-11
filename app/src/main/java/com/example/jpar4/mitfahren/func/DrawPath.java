package com.example.jpar4.mitfahren.func;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by jpar4 on 2017-09-09.
 * 사용안함
 */

public class DrawPath {
    /*For GetPathJson*/
    String startLat;
    String startLng;
    String endLat;
    String endLng;

    /*For ParsePathJson*/
    int totalDistance;
    ArrayList<LatLng> mapPoints; //LatLng 변수

    private GoogleMap mGoogleMap = null;
    private Polyline line;

    DrawPath(GoogleMap mGoogleMap, Polyline line,String startLat, String startLng, String endLat, String endLng){
        this.mGoogleMap = mGoogleMap;
        this.line = line;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
    }
}


