package com.call_sms.core;

/**
 * Created by konka on 2016-3-30.
 */


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.call_sms.infest_filter.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SmsContent {
    private Context mContext;
    private Uri uri;
    private List<SmsInfo> infos = null;

    public SmsContent(Context con, Uri uri) {
        infos = new ArrayList<SmsInfo>();
        this.uri = uri;
        this.mContext = con;
    }

    public List<SmsInfo> getSmsInfo() {
        String[] projection = new String[] { "_id", "thread_id","address", "person",
                "body", "date"};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null,
                "date desc");
        int idColumn = cursor.getColumnIndex("_id");
        int nameColumn = cursor.getColumnIndex("person");
        int phoneNumberColumn = cursor.getColumnIndex("address");
        int smsbodyColumn = cursor.getColumnIndex("body");
        int dateColumn = cursor.getColumnIndex("date");
        int threadColumn = cursor.getColumnIndex("thread_id");
        try{
        if(cursor != null&&cursor.moveToNext()) {
            SmsInfo smsinfo = new SmsInfo();
            smsinfo.setSmsId(cursor.getString(idColumn));
            if(cursor.getString(nameColumn).equals("0")){
                smsinfo.setPhoneNumber(cursor.getString(phoneNumberColumn));
                smsinfo.setPhoneName(cursor.getString(phoneNumberColumn));
            }else{
                smsinfo.setPhoneNumber(cursor.getString(phoneNumberColumn));
                smsinfo.setPhoneName(cursor.getString(nameColumn));
            }

            Calendar cal=Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(cursor.getString(dateColumn)));
            smsinfo.setTime(String.format("%02d/%02d %02d:%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

            smsinfo.setContent(cursor.getString(smsbodyColumn));
            smsinfo.setReason(-1);
            infos.add(smsinfo);
        }
        }catch(Exception e){
        e.printStackTrace();
        }finally {
        cursor.close();
        }
        return infos;
    }
}
