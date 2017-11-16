package com.example.jpar4.mitfahren.model;

/**
 * Created by jpar4 on 2017-11-02.
 */

public class Item_Chatting {
    boolean sent = true; // true = sent or false = receive;
    String carpool_id;// 카풀아이디
    String sender_pic; //보낸사람 사진
    String sender_name; // 보낸사람 이름
    String received_content; // 받은 텍스트
    String received_time; // 메시지 받은 시간
    String sent_time; // 메시지 보낸 시간
    String sent_content; // 메시지 내용
    String msg_date;// 채팅 날짜

    public String getCarpool_id() {
        return carpool_id;
    }

    public void setCarpool_id(String carpool_id) {
        this.carpool_id = carpool_id;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getSender_pic() {
        return sender_pic;
    }

    public void setSender_pic(String sender_pic) {
        this.sender_pic = sender_pic;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceived_content() {
        return received_content;
    }

    public void setReceived_content(String received_content) {
        this.received_content = received_content;
    }

    public String getReceived_time() {
        return received_time;
    }

    public void setReceived_time(String received_time) {
        this.received_time = received_time;
    }

    public String getSent_time() {
        return sent_time;
    }

    public void setSent_time(String sent_time) {
        this.sent_time = sent_time;
    }

    public String getSent_content() {
        return sent_content;
    }

    public void setSent_content(String sent_content) {
        this.sent_content = sent_content;
    }

    public String getMsg_date() {
        return msg_date;
    }

    public void setMsg_date(String msg_date) {
        this.msg_date = msg_date;
    }
}
