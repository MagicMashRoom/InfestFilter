package com.call_sms.core;

/**
 * Created by konka on 2016-3-31.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.call_sms.infest_filter.Utils;

@SuppressLint("SdCardPath")
public class PhoneStateReceiver extends BroadcastReceiver {

    private static boolean incomingFlag = false;

    private static String incomingNumber = null;

    private Context mContext;

    private AudioManager mAudioManager;//音频管理器
    /**
     * 函数名称：onReceive
     * 功能描述：接收拨打电话和电话状态改变的广播
     * 输入参数：Context  Intent
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Utils.log("onReceive");
        mContext = context;
        // 如果是拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            incomingFlag = false;
        } else {
            mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            // 如果是来电
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingFlag = true;// 标识当前是来电
                    incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    if(incomingNumber!=null) {
//                        mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
                        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);//铃声的音量设置为零
                        Utils.log("setStreamMute");
                        Intent startCallFilterService = new Intent();
                        startCallFilterService.setClass(context, CallFilterService.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(TelephonyManager.EXTRA_INCOMING_NUMBER, incomingNumber);
                        bundle.putSerializable("volume", volume);
                        startCallFilterService.putExtras(bundle);
                        context.startService(startCallFilterService);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (incomingFlag) {
                        Utils.log("call in offhook :");
                        Intent endCallFilterService = new Intent();
                        endCallFilterService.setClass(context, CallFilterService.class);
                        context.stopService(endCallFilterService);
                    }
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    if (incomingFlag) {
                        Utils.log("call in idle :");
                        Intent stopCallFilterService = new Intent();
                        stopCallFilterService.setClass(context, CallFilterService.class);
                        context.stopService(stopCallFilterService);
                    }
                    break;

            }
        }

    }

}

