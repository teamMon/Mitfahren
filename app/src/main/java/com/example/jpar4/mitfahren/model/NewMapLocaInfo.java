package com.example.jpar4.mitfahren.model;

/**
 * Created by jpar4 on 2017-08-25.
 */

public class NewMapLocaInfo {
    String markername; // 출발지인지 도착지인지 구분할떄 사용
    String markertitle; // 위치 한글명
    Double lat ; // 위도
    Double lng ; // 경도

    public NewMapLocaInfo(){

    }

    public NewMapLocaInfo(String markername, String markertitle, Double lat, Double lng){
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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
