package com.example.jpar4.mitfahren.model;

/**
 * Created by jpar4 on 2017-11-04.
 */

public class Item_ChattingRoomList {
    String carpool_id;
    String master_email;
    String car_pic;
    String room_title;
    String room_num;
    String num_unread_msg;

    public String getNum_unread_msg() {
        return num_unread_msg;
    }

    public void setNum_unread_msg(String num_unread_msg) {
        this.num_unread_msg = num_unread_msg;
    }

    public String getCarpool_id() {
        return carpool_id;
    }

    public void setCarpool_id(String carpool_id) {
        this.carpool_id = carpool_id;
    }

    public String getMaster_email() {
        return master_email;
    }

    public void setMaster_email(String master_email) {
        this.master_email = master_email;
    }

    public String getCar_pic() {
        return car_pic;
    }

    public void setCar_pic(String car_pic) {
        this.car_pic = car_pic;
    }

    public String getRoom_title() {
        return room_title;
    }

    public void setRoom_title(String room_title) {
        this.room_title = room_title;
    }

    public String getRoom_num() {
        return room_num;
    }

    public void setRoom_num(String room_num) {
        this.room_num = room_num;
    }
}
