package com.example.jpar4.mitfahren.network;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientBackground {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String msg;
    private String email;

    public ClientBackground(String email) {
        this.email = email;
    }

    public void connect() {
        try {
            //socket = new Socket("127.0.0.1", 7777);
            socket = new Socket("52.78.6.238", 7777);
            //System.out.println("서버 연결됨.");
            Log.e("ddd msg", "서버 연결됨.");

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            //out.writeUTF("�ȳ�! ���� �������̴�");
            out.writeUTF(email);
            //System.out.println("클라이언트 : 메시지 전송완료");
            Log.e("ddd msg", "클라이언트 : 메시지 전송완료" + email);

            while (in != null) {
                msg = in.readUTF();
            }


        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

/*	public static void main(String[] args) {
		ClientBackground clientBackground = new ClientBackground();
		clientBackground.connect();
	}*/

    public void sendMessage(String msg2) {
        try {
            out.writeUTF(msg2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
