package com.example.jpar4.mitfahren.func;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jpar4 on 2017-09-26.
 */

public class StringDateCompare {
    String date1, date2;
    int result = 404;// 404는 설정날짜가 없을때
    public StringDateCompare(){

    }


    public int DateCompare(String date1, String date2){ //첫번째에 디비 날짜, 두번째에 설정날짜
        this.date1 = date1;
        this.date2 = date2;
        Date db_date = null; Date date_from_date = null; Date date_to_date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            db_date= dateFormat.parse(date1);
            date_from_date = dateFormat.parse(date2);
     //       Log.e("ddd compare", date1+date2);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(date_from_date != null) result = db_date.compareTo(date_from_date); //설정날짜는 없을 수 도 있으니까.. 널아닐때만

        return result;// 0 : 같은 날짜, 1 : 디비 날짜가 클때 (나중일때), -1 : 설정날짜가 클 때(나줄일때)
    }
}
