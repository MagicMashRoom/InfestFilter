package com.call_sms.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.call_sms.data.DataBaseHelper;
import com.call_sms.data.FieldAttribute;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by konka on 2016-3-30.
 */
public class CallFilterService extends Service {

    private Context mContext ;
    private boolean mSilence = false;//静音标志位
    private AudioManager mAudioManager;//音频管理器
    private int resetRingVolume = 5;
    private TelephonyManager mTelManager;//通话管理器
    private ITelephony mTelInterface;//通话管理接口
    private Method methodEndCall;
    private List<BlackListInfo> mBlackListInfo;
    private NotificationCompat.Builder mBuilderCall;//Notification构造器
    private static int notifyId = 1;//Notification的ID
    private CallInfo mCallInfo;
    //占线时转移，提示所拨的号码为正忙
    private final String BUSY_NUMBER = "%23%2367%23";
    //占线时转移，提示所拨的号码为空号
    private final String ENABLE_SERVICE = "tel:**67*13800000000%23";
    //占线时转移，提示所拨的号码为关机
    private final String ENABLE_POWEROFF_SERVICE = "tel:**67*13810538911%23";
    //占线时转移，提示所拨的号码为停机
    private final String ENABLE_STOP_SERVICE = "tel:**67*13701110216%23";

    String GSM_CALL_WAIT_OPEN = "tel:##67#";
    String GSM_CALL_WAIT_CLOSE = "tel:%23%2321%23";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = getApplicationContext();
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mBlackListInfo = FilterApplication.getInstance().getBlackList();
        if(mBlackListInfo.size()==0) {
            DataBaseHelper dbHelper = FilterApplication.getInstance().getDataBaseHelper();
            SQLiteDatabase db = FilterApplication.getInstance().getSQLiteDatabase();
            Cursor cursorBlack = dbHelper.getBlackListCursor(db);
            writeDefaultData(cursorBlack);  /** 写缺省数据 */
        }
        mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mCallInfo = new CallInfo();

        try
        {
            // Get the getITelephony() method
            Class classTelephony = Class.forName(mTelManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);
            // Invoke getITelephony() to get the ITelephony interface
            mTelInterface = (ITelephony) methodGetITelephony.invoke(mTelManager);
            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass = Class.forName(mTelInterface.getClass().getName());
            methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
        }
        catch (Exception ex)
        {
            // Many things can go wrong with reflection calls
            Utils.log("error in getting endCall() method: " + ex.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBlackListInfo = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent == null)
            return Service.START_STICKY;
        String phone = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        resetRingVolume = intent.getIntExtra("volume",5);
        if (phone != null)
            runFilter(phone);
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 函数名称：writeDefaultData
     * 功能描述：写缺省数据
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    private void writeDefaultData(Cursor c2) {
        /** 保存数据  BlackList*/
        try{
            while(c2.moveToNext()){

                BlackListInfo info = new BlackListInfo();
                info.setId(c2.getInt(FieldAttribute.Keyword.INDEX_ID_COLUMN));
                info.setPhoneName(c2.getString(FieldAttribute.BlackList.INDEX_NAME));
                info.setPhoneNumber(c2.getString(FieldAttribute.BlackList.INDEX_PHONE_NUMBER));
                mBlackListInfo.add(info);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            c2.close();
        }
    }

    private void runFilter(String phone)
    {
        if(isCalledByStranger(mContext,phone)){
            FilterApplication.getInstance().setCallInfestFlag(true);
            FilterApplication.getInstance().setCallInfestType(InfestFilterConstant.FLAG_CALL);
        }
        if(FilterApplication.getInstance().getSettingPreference().getPreferenceBlackListInfest()&&isPhoneInBlackList(phone)){
            Utils.log("getPreferenceBlackListInfest&&isPhoneInBlackList");//判断此电话号码在黑名单之中
            if (!killCall(phone))
            {
                Utils.log("Unable to kill incoming call");
            }
            FilterApplication.getInstance().setCallInfestFlag(true);
            FilterApplication.getInstance().setCallInfestType(InfestFilterConstant.BLACKLIST_CALL);
        }else if(FilterApplication.getInstance().getSettingPreference().getPreferenceStrangeCallInfest()&&isCalledByStranger(mContext, phone)){
                if (!killCall(phone))
                {
                    Utils.log("Unable to kill incoming call");
                }
            FilterApplication.getInstance().setCallInfestFlag(true);
            FilterApplication.getInstance().setCallInfestType(InfestFilterConstant.STRANGE_CALL);
        }
        ResetSilenceRing();
    }

    //判断电话是否在黑名单之中
    private boolean isPhoneInBlackList(String phone)
    {
        Iterator<BlackListInfo> iterator = mBlackListInfo.iterator();
        while (iterator.hasNext()){
            BlackListInfo next = iterator.next();
            if(phone.equals(next.getPhoneNumber())){
                mCallInfo.setPhoneName(next.getPhoneName());
                mCallInfo.setPhoneNumber(next.getPhoneNumber());
                return true;
            }
        }
            return false;
    }

    /**
     * 函数名称：isCalledByStranger
     * 功能描述： 通过打电话是否为陌生人
     * 输入参数：String number Context context
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */

    public boolean isCalledByStranger(Context con,String n) {
        boolean b = true;
        ContentResolver resolver = con.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + n);
        Cursor c = resolver.query(uri, new String[]{"display_name"}, null, null, null);
        mCallInfo.setPhoneName(n);
        mCallInfo.setPhoneNumber(n);
        try{
            while (c.moveToNext()) {
                b = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            c.close();
        }
        return b;
    }

    //挂断电话
    public boolean killCall(String head)
    {
        try
        {
            mTelInterface.endCall();
            Utils.log("killCall() success");
        }
        catch (Exception ex)
        { // Many things can go wrong with reflection calls
            Utils.log("killCall() error: " + ex.toString());
            return false;
        }
        return true;
    }

    //发出拦截通知
    private void reportNewReject()
    {
        if(FilterApplication.getInstance().getSettingPreference().getPreferenceNotification()){
        showFilterActivityNotify();
        }
    }

    /**
     * 显示通知栏点击跳转到指定Activity
     */
    public void showFilterActivityNotify() {
        // Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
        mBuilderCall = new NotificationCompat.Builder(mContext);
        mBuilderCall.setAutoCancel(true)//点击后让通知将消失
                .setContentTitle("您有一条新电话拦截")
                .setContentText("点击查看")
                .setTicker("拦截来电"+mCallInfo.getPhoneNumber())
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.forbid_little);
        Utils.log("showIntentActivityNotify");
        //点击的意图ACTION是跳转到Intent
        /** 用intent启动骚扰详情 */
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        CallInfo info = new CallInfo();
        Cursor cursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if(cursor.moveToNext()){
            Calendar cal=Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))));
            String date = String.format("%02d/%02d %02d:%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            info.setTime(date);
            String location = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));
            info.setLocation(location);
        }
        cursor.close();
        info.setPhoneName(mCallInfo.getPhoneName());
        info.setPhoneNumber(mCallInfo.getPhoneNumber());
        bundle.putSerializable(InfestFilterConstant.CALL_DATA, info);
        intent.putExtras(bundle);
        intent.setClass(getApplicationContext(), InfestContentCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mBuilderCall.setContentIntent(PendingIntent.getActivity(mContext, 1019, intent, 0));//requestCode不同的话PendingIntent也会不同，如Code相同
        //则会后面的PendingIntent代替前面的Intent
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1019, mBuilderCall.build());
    }


    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性:
     * 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }
    //拦截电话设置静音
//    private void SetSilenceRing() {
//        if (!mSilence) {
//            resetRingVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
//            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);//这里就是铃音没有的原因了
//            Utils.log("SetSilenceRing");
//            mSilence = true;
//        }
//    }

    //拦截成功恢复先前状态
    private void ResetSilenceRing() {
//            mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, resetRingVolume, 0);//恢复原先铃声的音量
            Utils.log("ResetSilenceRing="+resetRingVolume);
    }
}
