package com.call_sms.infest_filter;

import android.util.Log;

public class Utils {
	 final static String TAG = "FilterMainActivity";
	 final static boolean DEBUG = true;

     public static void log(String content) {
    	 if(DEBUG){
    		 Log.d(TAG, content);
    	 }
     }

     public static void log(String tag, String content) {
    	 if(DEBUG){
    		 Log.d(TAG, "[" + tag + "]" + " " + content);
    	 }
     }


}
