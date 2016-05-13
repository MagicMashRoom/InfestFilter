package com.call_sms.core;

/**
 * Created by konka on 2016-3-14.
 */
public class InfestFilterConstant {
    public static final String SMS_DATA = "Sms_Info";//传递点击的smsinfo数据
    public static final String CALL_DATA = "Call_Info";//传递点击的callinfo数据

    public static final int BLACKLIST_SMS = 1 ;//黑名单拦截短信
    public static final int KEYWORD_SMS = 2 ;//关键词拦截短信
    public static final int STRANGE_SMS = 3 ;//默认短信拦截

    public static final int BLACKLIST_CALL = 1 ;//黑名单拦截短信
    public static final int STRANGE_CALL = 2 ;//默认短信拦截
    public static final int FLAG_CALL = 3;//标记电话

    public static final boolean isSms = true;//短信跳转到的详情界面
    public static final boolean isCall = false;//电话跳转到的详情界面

    public static final int contactsInitEnd = 1;//联系人列表加载完毕
    public static final int FILTER_INITIALIZE_FINISH = 8;//初始化完成广播
    public static final String INFESTFILTER_BROADCAST_ACTION = "Infest_Filter_Broadcast";//广播ACTION
    public static final String SMSNEW_BROADCAST_ACTION = "Sms_Filter_Broadcast";//广播ACTION

    public static final String AREA_NUMBER = "+86";
}
