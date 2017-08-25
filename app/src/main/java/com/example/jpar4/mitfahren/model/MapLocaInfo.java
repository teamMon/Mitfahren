package com.example.jpar4.mitfahren.model;

/**
 * Created by jpar4 on 2017-08-25.
 */

public class MapLocaInfo {
    String markername; // 출발지인지 도착지인지 구분할떄 사용
    String markertitle; // 위치 한글명
    float lat ; // 위도
    float lng ; // 경도

    public MapLocaInfo(){

    }

    public MapLocaInfo(String markername, String markertitle, float lat, float lng){
        this.markername = markername;
        this.markertitle = markertitle;
        this.lat = lat;
        this.lng = lng;
    }

    public String getMarkername() {
        return markername;
    }

    public void setMarkername(String markername) {
        this.markername = markername;
    }

    public String getMarkertitle() {
        return markertitle;
    }

    public void setMarkertitle(String markertitle) {
        this.markertitle = markertitle;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
