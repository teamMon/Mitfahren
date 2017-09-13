package com.example.jpar4.mitfahren.app;

import android.app.Application;

/**
 * Created by jpar4 on 2017-09-07.
 */

public class Myapp extends Application {
    private String user_email;
    private String user_name;
    private String user_age;
    private String user_sex;
    private String user_drive_num;
    private String user_ride_num;
    private String user_photo;
    private boolean loginOK;

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

    public String getUser_age() {
        return user_age;
    }

    public void setUser_age(String user_age) {
        this.user_age = user_age;
    }

    public String getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(String user_sex) {
        this.user_sex = user_sex;
    }

    public String getUser_drive_num() {
        return user_drive_num;
    }

    public void setUser_drive_num(String user_drive_num) {
        this.user_drive_num = user_drive_num;
    }

    public String getUser_ride_num() {
        return user_ride_num;
    }

    public void setUser_ride_num(String user_ride_num) {
        this.user_ride_num = user_ride_num;
    }

    public boolean isLoginOK() {
        return loginOK;
    }

    public void setLoginOK(boolean loginOK) {
        this.loginOK = loginOK;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }
}