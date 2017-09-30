package com.example.jpar4.mitfahren.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

/**
 * Created by jpar4 on 2017-09-19.
 */

public class Item_New_Driver_Info implements Serializable, ClusterItem{
    String user_email;
    String user_start_date;
    String user_start_time;
    String user_with_poeple;
    String user_start_lat;
    String user_start_lng;
    String user_arrive_lat;
    String user_arrive_lng;
    String user_car_photo;
    String user_having_rider ;
    String user_carpool_complete;
    String user_start;
    String user_arrive;
    LatLng location;
    String address;
    String distance_option;
    String distance_meters;
    String user_sex;

    public String getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(String user_sex) {
        this.user_sex = user_sex;
    }

    public String getDistance_option() {
        return distance_option;
    }

    public void setDistance_option(String distance_option) {
        this.distance_option = distance_option;
    }

    public String getDistance_meters() {
        return distance_meters;
    }

    public void setDistance_meters(String distance_meters) {
        this.distance_meters = distance_meters;
    }

    public Item_New_Driver_Info(){

    }

    public Item_New_Driver_Info(String a, String b){//테스트용
this.user_start = a;
        this.user_arrive=b;
    }

    public Item_New_Driver_Info(Item_New_Driver_Info item){
        this.user_email = item.getUser_email();
        this.user_start_date = item.getUser_start_date();
        this.user_start_time = item.getUser_start_time();
        this.user_with_poeple = item.getUser_with_poeple();
        this.user_start_lat = item.getUser_start_lat();
        this.user_start_lng = item.getUser_start_lng();
        this.user_arrive_lat = item.getUser_arrive_lat();
        this.user_arrive_lng = item.getUser_arrive_lng();
        this.user_car_photo = item.getUser_car_photo();
        this.user_having_rider = item.getUser_having_rider();
        this.user_carpool_complete = item.getUser_carpool_complete();
        this.user_start = item.getUser_start();
        this.user_arrive = item.getUser_arrive();

    }

    public Item_New_Driver_Info(LatLng location, String address){
        this.location = location;
        this.address = address;
    }

    public Item_New_Driver_Info(Item_New_Driver_Info item, String address){
        this(item);
        this.location = new LatLng(Double.parseDouble(item.getUser_start_lat()), Double.parseDouble(item.getUser_start_lng()));
        this.address = address;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser_start() {
        return user_start;
    }

    public void setUser_start(String user_start) {
        this.user_start = user_start;
    }

    public String getUser_arrive() {
        return user_arrive;
    }

    public void setUser_arrive(String user_arrive) {
        this.user_arrive = user_arrive;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_start_date() {
        return user_start_date;
    }

    public void setUser_start_date(String user_start_date) {
        this.user_start_date = user_start_date;
    }

    public String getUser_start_time() {
        return user_start_time;
    }

    public void setUser_start_time(String user_start_time) {
        this.user_start_time = user_start_time;
    }

    public String getUser_with_poeple() {
        return user_with_poeple;
    }

    public void setUser_with_poeple(String user_with_poeple) {
        this.user_with_poeple = user_with_poeple;
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

    public String getUser_car_photo() {
        return user_car_photo;
    }

    public void setUser_car_photo(String user_car_photo) {
        this.user_car_photo = user_car_photo;
    }

    public String getUser_having_rider() {
        return user_having_rider;
    }

    public void setUser_having_rider(String user_having_rider) {
        this.user_having_rider = user_having_rider;
    }

    public String getUser_carpool_complete() {
        return user_carpool_complete;
    }

    public void setUser_carpool_complete(String user_carpool_complete) {
        this.user_carpool_complete = user_carpool_complete;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }
}
