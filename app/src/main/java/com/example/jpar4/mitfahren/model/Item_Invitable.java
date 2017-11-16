package com.example.jpar4.mitfahren.model;

/**
 * Created by jpar4 on 2017-11-01.
 */

public class Item_Invitable {
    String carpool_id;
    String user_email;
    String user_name;
    String user_pic;
    boolean item_checked = false;

    public String getCarpool_id() {
        return carpool_id;
    }

    public void setCarpool_id(String carpool_id) {
        this.carpool_id = carpool_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public boolean isItem_checked() {
        return item_checked;
    }

    public void setItem_checked(boolean item_checked) {
        this.item_checked = item_checked;
    }
}
