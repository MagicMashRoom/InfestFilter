package com.call_sms.core;

/**
 * Created by konka on 2016-3-30.
 */


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@SuppressLint("SdCardPath")
public class SmsObserver extends ContentObserver {
    /**
     * Activity对象
     */
    private Context mContext;
    private List<SmsInfo> infos;
    public static final String SMS_URI_INBOX = "content://sms/inbox";//收件箱短信
    private static boolean first = true;
    private String ID = null;
    private String IDNEW = null;

    private int int_id = -1;
    private int int_idnew = -1;

    private boolean mSilence = false;//静音标志位
    private AudioManager mAudioManager;//音频管理器
    private List<BlackListInfo> mBlackListInfo;
    private List<KeywordInfo> mKeywordInfo;
    private NotificationCompat.Builder mBuilder;
    private SimpleDateFormat formatter= new SimpleDateFormat("ddHHmmss");
    private int resetRingVolume = 5;//铃声音量

    public SmsObserver(Handler handler,Context con,AudioManager am) {
        super(handler);
        this.mContext = con;
        mBlackListInfo = FilterApplication.getInstance().getBlackList();
        mKeywordInfo = FilterApplication.getInstance().getKeywordListInfo();
        mAudioManager = am;
    }

    /**
     * 函数名称：onChange
     * 功能描述：监听数据库变化以得到新短信信息
     * 输入参数：boolean change
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        resetRingVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);//音量修改为零
        Uri uri = Uri.parse(SMS_URI_INBOX);// 设置一个uri来查看收件箱短信内容
        SmsContent smscontent = new SmsContent(mContext, uri);
        infos = smscontent.getSmsInfo();
        if(infos.size()>0){
            IDNEW  = infos.get(0).getSmsId();
            int_idnew = Integer.parseInt(IDNEW);
            if(int_id < int_idnew){
                if(int_id==-1){
                    ID = IDNEW;
                    int_id = int_idnew;
                }else {
                    ID = IDNEW;
                    int_id = int_idnew;
                    //收件箱发生变化
                    runFilter(infos.get(0));
                }
            }
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, resetRingVolume, 0);//恢复原先铃声的音量
    }


    private void runFilter(SmsInfo info)
    {

        if(FilterApplication.getInstance().getSettingPreference().getPreferenceBlackListInfest()&&isSmsInBlackList(info.getPhoneNumber())){//判断此短信在黑名单之中
            SetSilenceRing();
            Utils.log("black");
            infos.get(0).setReason(InfestFilterConstant.BLACKLIST_SMS);
            FilterApplication.getInstance().addSmsListInfo(info);
            reportNewReject(info);
            FilterApplication.getInstance().sendSmsBroadcast();
        }else if(FilterApplication.getInstance().getSettingPreference().getPreferenceKeyword()&&isSmsHaveKeywordFilter(info.getContent())){//或者短信内容中含有拦截关键字
            SetSilenceRing();
            Utils.log("keyword");
            infos.get(0).setReason(InfestFilterConstant.KEYWORD_SMS);
            FilterApplication.getInstance().addSmsListInfo(info);
            reportNewReject(info);
            FilterApplication.getInstance().sendSmsBroadcast();
        }else if(FilterApplication.getInstance().getSettingPreference().getPreferenceStrangeSmsInfest()&&isSmsSendByStranger(mContext,info.getPhoneNumber())){
            SetSilenceRing();
            Utils.log("strange");
            infos.get(0).setReason(InfestFilterConstant.STRANGE_SMS);
            FilterApplication.getInstance().addSmsListInfo(info);
            reportNewReject(info);
            FilterApplication.getInstance().sendSmsBroadcast();
        }
    }

    //判断短信发件人是否在黑名单之中
    private boolean isSmsInBlackList(String phone)
    {
        Iterator<BlackListInfo> iterator = mBlackListInfo.iterator();
        while (iterator.hasNext()){
            BlackListInfo next = iterator.next();
            if(next.getPhoneNumber().equals("")){
                if(phone.equals(next.getPhoneName())||phone.equals(InfestFilterConstant.AREA_NUMBER+next.getPhoneName())||
                        phone.equals(next.getPhoneName().replace(InfestFilterConstant.AREA_NUMBER,""))){
                    return true;
                }
            }
            else{
                if(phone.equals(next.getPhoneNumber())){
                return true;
            }
            }
        }
        return false;
    }

    //判断短信中是否含有拦截关键字
    private boolean isSmsHaveKeywordFilter(String content)
    {

        Iterator<KeywordInfo> iterator = mKeywordInfo.iterator();

        while (iterator.hasNext()){
            KeywordInfo next = iterator.next();
            if(content.indexOf(next.getContent())>=0){
                return true;
            }
            next = null;
        }
        return false;
    }

    /**
     * 函数名称：isSmsSendByStranger
     * 功能描述： 通过短信发信人是否为陌生人
     * 输入参数：String number Context context
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */

    public boolean isSmsSendByStranger(Context con,String n) {
        boolean b = true;
        ContentResolver resolver = con.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + n);
        Cursor c = resolver.query(uri, new String[]{"display_name"}, null, null, null);
        try{
            while (c.moveToNext()) {
                b = false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            c.close();
        }
        return b;
    }

    //发出拦截通知
    private void reportNewReject(SmsInfo info)
    {
        if(FilterApplication.getInstance().getSettingPreference().getPreferenceNotification()) {
            showFilterActivityNotify(info);
        }
    }

    /**
     * 显示通知栏点击跳转到指定Activity
     */
    public void showFilterActivityNotify(SmsInfo info) {
        // Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知

        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setAutoCancel(true)//点击后让通知将消失
                .setContentTitle("您有一条新拦截")
                .setContentText(info.getPhoneName())
                .setTicker(info.getContent())
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.forbid_little);
        //点击的意图ACTION是跳转到Intent
        /** 用intent启动骚扰详情 */
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putSerializable(InfestFilterConstant.SMS_DATA, info);
        bundle.putSerializable("position", -10);
        intent.putExtras(bundle);
        intent.setClass(mContext, InfestContentSmsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        int notifyId = Integer.valueOf(formatter.format(curDate)).intValue();
        Utils.log("notifyId=" + notifyId);
        mBuilder.setContentIntent(PendingIntent.getActivity(mContext, notifyId, intent, 0));//requestCode不同的话PendingIntent也会不同，如Code相同
        //则会后面的PendingIntent代替前面的Intent

        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notifyId, mBuilder.build());
    }


    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性:
     * 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(mContext, 1, new Intent(), flags);
        return pendingIntent;
    }

    //拦截短信设置静音
    private void SetSilenceRing() {
        if (!mSilence) {
            mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
            mSilence = true;
        }
    }

    //拦截成功恢复先前状态
    private void ResetSilenceRing() {
        if (mSilence) {
            mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
            mSilence = false;
        }
    }

}

