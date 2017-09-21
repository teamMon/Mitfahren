package com.example.jpar4.mitfahren.model;

/**
 * Created by jpar4 on 2017-09-19.
 */

public class TestCarpollItem {
    String start;
    String arrive;

    public TestCarpollItem(String start, String arrive){
        this.start = start;
        this.arrive = arrive;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }
}
