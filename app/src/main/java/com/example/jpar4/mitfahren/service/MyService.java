package com.example.jpar4.mitfahren.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.jpar4.mitfahren.network.NetworkTask;

import java.util.Random;

/**
 * Created by jpar4 on 2017-09-29.
 */

public class MyService extends Service {

    /*TCP연결용*/
    NetworkTask networkTask;

    public IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder{
        public MyService getService(){
            return MyService.this;
        }
    }

   public int showTheNumber(){
       return new Random().nextInt(99);
   }

   public void setConnectionTCP(String email){
       networkTask = new NetworkTask(this , email);
       networkTask.start();
   }

   public void sendMsg(String msg){
       networkTask.sendMessage(msg);
   }


}
