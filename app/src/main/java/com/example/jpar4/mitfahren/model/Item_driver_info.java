package com.example.jpar4.mitfahren.model;

/**
 * Created by jpar4 on 2017-08-07.
 */

public class Item_driver_info {
    /*
    * 운전자 이름
    * 운전자 출발지
    * 운전자 도착지
    * 운전자 출발시간
    * 운전자 동승인원
    *
    * 운전자 나이, 운전자 성별 추후 추가 예정
    */
    private String driver_name;
    private String driver_from;
    private String driver_to;
    private String driver_start_time;
    private int driver_carry_people_num;

    public Item_driver_info(String driver_name,
                     String driver_from,
                     String driver_to,
                     String driver_start_time,
                     int driver_carry_people_num
    ){
         this.driver_name = driver_name;
         this.driver_from = driver_from;
         this.driver_to = driver_to;
         this.driver_start_time = driver_start_time;
         this.driver_carry_people_num = driver_carry_people_num;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_from() {
        return driver_from;
    }

    public void setDriver_from(String driver_from) {
        this.driver_from = driver_from;
    }

    public String getDriver_to() {
        return driver_to;
    }

    public void setDriver_to(String driver_to) {
        this.driver_to = driver_to;
    }

    public String getDriver_start_time() {
        return driver_start_time;
    }

    public void setDriver_start_time(String driver_start_time) {
        this.driver_start_time = driver_start_time;
    }

    public int getDriver_carry_people_num() {
        return driver_carry_people_num;
    }

    public void setDriver_carry_people_num(int driver_carry_people_num) {
        this.driver_carry_people_num = driver_carry_people_num;
    }
}
