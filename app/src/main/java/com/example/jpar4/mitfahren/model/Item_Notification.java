package com.example.jpar4.mitfahren.model;

/**
 * Created by jpar4 on 2017-10-09.
 */

public class Item_Notification {
    String carpool_ID;

    public String getCarpool_ID() {
        return carpool_ID;
    }

    public void setCarpool_ID(String carpool_ID) {
        this.carpool_ID = carpool_ID;
    }

    String noti_title;
    String noti_date;
    String noti_profile_pic;
    String noti_content;
    String rider_num;

    String rider_start_lat; //탑승자용
    String rider_start_lng;
    String rider_arrive_lat;
    String rider_arrive_lng;
    String rider_email;

    String user_start_lat;//운전자용
    String user_start_lng;
    String user_arrive_lat;
    String user_arrive_lng;

    /*라이더 클릭했는지 안했는지*/
    Boolean rider_clicked = true;

    public String getRider_num() {
        return rider_num;
    }

    public void setRider_num(String rider_num) {
        this.rider_num = rider_num;
    }

    public String getRider_email() {
        return rider_email;
    }

    public void setRider_email(String rider_email) {
        this.rider_email = rider_email;
    }

    public Boolean getRider_clicked() {
        return rider_clicked;
    }

    public void setRider_clicked(Boolean rider_clicked) {
        this.rider_clicked = rider_clicked;
    }

    public String getNoti_title() {
        return noti_title;
    }

    public void setNoti_title(String noti_title) {
        this.noti_title = noti_title;
    }

    public String getNoti_date() {
        return noti_date;
    }

    public void setNoti_date(String noti_date) {
        this.noti_date = noti_date;
    }

    public String getNoti_profile_pic() {
        return noti_profile_pic;
    }

    public void setNoti_profile_pic(String noti_profile_pic) {
        this.noti_profile_pic = noti_profile_pic;
    }

    public String getNoti_content() {
        return noti_content;
    }

    public void setNoti_content(String noti_content) {
        this.noti_content = noti_content;
    }

    public String getRider_start_lat() {
        return rider_start_lat;
    }

    public void setRider_start_lat(String rider_start_lat) {
        this.rider_start_lat = rider_start_lat;
    }

    public String getRider_start_lng() {
        return rider_start_lng;
    }

    public void setRider_start_lng(String rider_start_lng) {
        this.rider_start_lng = rider_start_lng;
    }

    public String getRider_arrive_lat() {
        return rider_arrive_lat;
    }

    public void setRider_arrive_lat(String rider_arrive_lat) {
        this.rider_arrive_lat = rider_arrive_lat;
    }

    public String getRider_arrive_lng() {
        return rider_arrive_lng;
    }

    public void setRider_arrive_lng(String rider_arrive_lng) {
        this.rider_arrive_lng = rider_arrive_lng;
    }

    public String getUser_start_lat() {
        return user_start_lat;
    }

    public void setUser_start_lat(String user_start_lat) {
        this.user_start_lat = user_start_lat;
    }

    public String getUser_start_lng() {
        return user_start_lng;
    }

    public void setUser_start_lng(String user_start_lng) {
        this.user_start_lng = user_start_lng;
    }

    public String getUser_arrive_lat() {
        return user_arrive_lat;
    }

    public void setUser_arrive_lat(String user_arrive_lat) {
        this.user_arrive_lat = user_arrive_lat;
    }

    public String getUser_arrive_lng() {
        return user_arrive_lng;
    }

    public void setUser_arrive_lng(String user_arrive_lng) {
        this.user_arrive_lng = user_arrive_lng;
    }
}
