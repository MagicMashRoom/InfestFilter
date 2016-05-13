package com.call_sms.infest_filter;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;

import com.call_sms.core.BlackListInfo;
import com.call_sms.core.CallInfo;
import com.call_sms.core.InfestFilterConstant;
import com.call_sms.core.KeywordInfo;
import com.call_sms.core.SmsInfo;
import com.call_sms.core.SmsObserver;
import com.call_sms.data.DataBaseHelper;
import com.call_sms.data.FieldAttribute;
import com.call_sms.data.SettingsPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FilterApplication extends Application{
	
	public static FilterApplication mInstance = null;
	private static boolean mInitFlag = false;
	private SettingsPreference mSettingPreference = null; /** 共享数据对象 */
	private List<Activity> mActivityStack = null; /** Activity列表 */
	private List<BlackListInfo> mBlackList = null; /** 黑名单列表 */
	private List<KeywordInfo> mKeywordList = null; /** 关键字列表 */
	private List<SmsInfo> mSmsInfoList = null;/** 拦截短信列表 */
    private List<CallInfo> mCallInfoList = null;/** 拦截电话列表 */
	private DataBaseHelper mDataBaseHelper = null;  /** 数据库操作对象 */
	private SQLiteDatabase mDataBase = null;  /** 数据库对象 */
	private int addLength = 0;
	private int calllogLength;/**通话记录的长度*/
	private SmsObserver mSmsObserver = null;//新短信监听器
	private AudioManager mAudioManager;//音频管理器
	private boolean mCallInfestFlag = false;
    private int mCallInfestType = 3;

	/** 每次做添加操作的时添加数据的长度 */
	@Override
	public void onCreate() 
	{
		mInstance = this;		
		super.onCreate();
		/** 初始化全局数据 */
		this.mDataBaseHelper = new DataBaseHelper(this);
		this.mDataBase = mDataBaseHelper.getDatabase();
		this.mSettingPreference = SettingsPreference.getInstance(this);
		this.mActivityStack = new ArrayList<Activity>();
		this.mBlackList = new ArrayList<BlackListInfo>();

		this.mKeywordList = new ArrayList<KeywordInfo>();
		this.mSmsInfoList = new ArrayList<SmsInfo>();
        this.mCallInfoList = new ArrayList<CallInfo>();

		mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		/** 注册SmsObserver 短信数据库变化信息,初始化完成才注册 **/
		mSmsObserver = new SmsObserver(new Handler(),getApplicationContext(),mAudioManager);
        this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms"), true, mSmsObserver);
	}

	@Override
	public void onTerminate() 
	{
		super.onTerminate();
		Utils.log("onTerminate");
		mInstance = null;
		/** 释放数据库对象 */
		if(this.mDataBaseHelper != null)
		{
			this.mDataBase.close();
			this.mDataBaseHelper.close();
			this.mDataBase = null;
			this.mDataBaseHelper = null;
		}
		/** 释放共享数据类对象 */
		if(this.mSettingPreference != null)
		{
			this.mSettingPreference = null;
		}

		this.mBlackList.clear();
		this.mKeywordList.clear();
        this.mSmsInfoList.clear();
        this.mCallInfoList.clear();

	}
	

	public static FilterApplication getInstance()
	{
		return mInstance;
	}

	/**
	 * 函数名称：writeDefaultData
	 * 功能描述：写缺省数据
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	private void writeDefaultData(Cursor c1,Cursor c2) {

		/** 获取数据  Keyword*/
		try{
			while(c1.moveToNext()){
				KeywordInfo info = new KeywordInfo();
				info.setId(c1.getInt(FieldAttribute.Keyword.INDEX_ID_COLUMN));
				info.setContent(c1.getString(FieldAttribute.Keyword.INDEX_KEY));
				mKeywordList.add(info);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			c1.close();
		}

		/** 保存数据  BlackList*/
		try{
			while(c2.moveToNext()){
				BlackListInfo info = new BlackListInfo();
				info.setId(c2.getInt(FieldAttribute.Keyword.INDEX_ID_COLUMN));
				info.setPhoneName(c2.getString(FieldAttribute.BlackList.INDEX_NAME));
				info.setPhoneNumber(c2.getString(FieldAttribute.BlackList.INDEX_PHONE_NUMBER));
				mBlackList.add(info);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			c2.close();
		}

	}

	/**
	 * 函数名称：initializeFinish
	 * 功能描述：改变初始化标志
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：五
	 *
	 * 备          注：无
	 */
	public void initializeFinish(){
		this.mInitFlag = true;
	}

	/**
	 * 函数名称：initializeFinish
	 * 功能描述：改变初始化标志
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：五
	 *
	 * 备          注：无
	 */
	public boolean getInitFlag(){
		return this.mInitFlag;
	}

    public void setCallInfestFlag(boolean b){
        this.mCallInfestFlag = b;
    }

    public boolean getCallInfestFlag(){
        return this.mCallInfestFlag;
    }

    public void setCallInfestType(int i){
        this.mCallInfestType = i;
    }

    public int getCallInfestType(){
        return this.mCallInfestType;
    }
	/**
	 * 函数名称：sendMainBroadcast
	 * 功能描述：发送拦截助手广播
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void sendMainBroadcast()
	{
		/** 发送广播 */
		Intent intent = new Intent();
		intent.setAction(InfestFilterConstant.INFESTFILTER_BROADCAST_ACTION); /** action与接收器相同 */
		sendBroadcast(intent);

	}

	/**
	 * 函数名称：sendSmsBroadcast
	 * 功能描述：发送短信刷新广播
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void sendSmsBroadcast()
	{
		/** 发送广播 */
		Intent intent = new Intent();
		intent.setAction(InfestFilterConstant.SMSNEW_BROADCAST_ACTION); /** action与接收器相同 */
		sendBroadcast(intent);
		Utils.log("sendSmsBroadcast");

	}
	/**
	 * 函数名称：getDataBaseHelper
	 * 功能描述：获取数据库操作对象
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：数据库操作对象
	 * 备          注：无
	 */
	public DataBaseHelper getDataBaseHelper()
	{
		return this.mDataBaseHelper;
	}

	/**
	 * 函数名称：getSQLiteDatabase
	 * 功能描述：获取数据库对象
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：数据库对象
	 * 备          注：无
	 */
	public SQLiteDatabase getSQLiteDatabase()
	{
		return this.mDataBase;
	}
	/**
	 * 函数名称：getSettingPreference
	 * 功能描述：获取共享设置对象
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：共享数据对象
	 * 备          注：无S
	 */
	public SettingsPreference getSettingPreference()
	{
		return this.mSettingPreference;
	}

	/**
	 * 函数名称：push
	 * 功能描述：进栈
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void push(Activity paramActivity)
	{
		synchronized (this.mActivityStack)
		{
			if (!this.mActivityStack.contains(paramActivity))
			{
				this.mActivityStack.add(paramActivity);
			}
			return;
		}
	}

	/**
	 * 函数名称：push
	 * 功能描述：进栈
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void pop(Activity paramActivity)
	{
		synchronized (this.mActivityStack)
		{
			if (this.mActivityStack.contains(paramActivity))
			{
				this.mActivityStack.remove(paramActivity);
			}
			return;
		}
	}

	/**
	 * 函数名称：exit
	 * 功能描述：退出
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void exit()
	{
		synchronized (this.mActivityStack)
		{
			ArrayList<Activity> localArrayList = new ArrayList<Activity>(this.mActivityStack);
			Collections.reverse(localArrayList);
			this.mActivityStack.clear();
			Iterator<Activity> localIterator = localArrayList.iterator();
			while (localIterator.hasNext())
			{
				Activity localActivity = (Activity)localIterator.next();
				if (!localActivity.isFinishing())
				{
					localActivity.finish();
				}
			}
		}
	}

	/**
	 * 函数名称：getTopActivity
	 * 功能描述：获取顶层的activity
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public Activity getTopActivity()
	{
		Activity localActivity;
		synchronized (this.mActivityStack)
		{
			if (this.mActivityStack.size() <= 0)
			{
				localActivity = null;
			}
			else
			{
				localActivity = (Activity)this.mActivityStack.get(0);
			}
		}
		return localActivity;
	}
	/**
	 * 函数名称：addBlackListInfo
	 * 功能描述：单个添加黑名单
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void addBlackListInfo(BlackListInfo info) {
		synchronized (this.mBlackList) {

			Boolean addFlag = true;
			Iterator<BlackListInfo> it = mBlackList.iterator();
			while(it.hasNext()){
				BlackListInfo next = it.next();
				if(next.getPhoneNumber().equals(info.getPhoneNumber())||next.getPhoneNumber().equals(InfestFilterConstant.AREA_NUMBER+info.getPhoneNumber())||
						next.getPhoneNumber().equals(info.getPhoneNumber().replace(InfestFilterConstant.AREA_NUMBER,""))){
				addFlag = false;
				}
			}
			if(addFlag){
				ContentValues values = new ContentValues();
				values.clear();
				values.put(FieldAttribute.BlackList._NAME, info.getPhoneName());
				values.put(FieldAttribute.BlackList._PHONE_NUMBER,info.getPhoneNumber());
				int id = (int) mDataBase.insert(DataBaseHelper.DB_BLACK_LIST_TABLE, null, values);
				info.setId(id);
			this.mBlackList.add(info);
			}
		}
		addLength = addLength +1;
	}

	/**
	 * 函数名称：addBlackList
	 * 功能描述：添加黑名单
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void addBlackList(List<BlackListInfo> info) {
		synchronized (this.mBlackList) {
			Iterator<BlackListInfo> it = info.iterator();
			while(it.hasNext()) {
				BlackListInfo next = it.next();
				Boolean addFlag = true;
				Iterator<BlackListInfo> itBlack = mBlackList.iterator();
				while(itBlack.hasNext()){
					BlackListInfo nextBlack = itBlack.next();
					if(nextBlack.getPhoneNumber().equals(next.getPhoneNumber())){
						addFlag = false;
					}
				}
				if(addFlag){
					ContentValues values = new ContentValues();
					values.clear();
					values.put(FieldAttribute.BlackList._NAME, next.getPhoneName());
					values.put(FieldAttribute.BlackList._PHONE_NUMBER,next.getPhoneNumber());
					int id = (int) mDataBase.insert(DataBaseHelper.DB_BLACK_LIST_TABLE, null, values);
					Utils.log("id="+id);
					next.setId(id);
					this.mBlackList.add(next);
				}
			}
		}
		addLength = info.size();
		info = null;
	}

	/**
	 * 函数名称：getBlackList
	 * 功能描述：得到黑名单
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public List<BlackListInfo> getBlackList() {

		synchronized (this.mBlackList) {
			return mBlackList;
		}

	}

	/**
	 * 函数名称：removeBlackList
	 * 功能描述：通过位置黑名单
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void removeBlackList(int position) {

		synchronized (this.mBlackList) {
			BlackListInfo info = mBlackList.get(position);
			this.mBlackList.remove(position);
			/** 删除该条记录 */
			String where = FieldAttribute.Keyword._ID + "=?";
			String[] whereValue = {String.valueOf(info.getId())};
			Utils.log("ID=" + info.getId());
//			mDataBase.delete(DataBaseHelper.DB_BLACK_LIST_TABLE, where, whereValue);
			Utils.log("result=" + mDataBase.delete(DataBaseHelper.DB_BLACK_LIST_TABLE, where, whereValue));
		}
		addLength = addLength-1;
	}

	/**
	 * 函数名称：removeBlackList
	 * 功能描述：通过对象移除黑名单
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void removeBlackList(BlackListInfo info) {

		synchronized (this.mBlackList) {
			this.mBlackList.remove(info);
		}
		addLength = addLength-1;
	}

	/**
	 * 函数名称：getAddLength
	 * 功能描述：得到每次添加的数据的长度
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public int getAddLength() {

		return addLength;
	}

	/**
	 * 函数名称：resetAddLength
	 * 功能描述：得到每次添加的数据的长度
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void resetAddLength() {

		this.addLength = 0;
	}
	/**
	 * 函数名称：getCalllogLength
	 * 功能描述：得到通话记录的长度
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public int getCalllogLength() {
		return calllogLength;
	}

	/**
	 * 函数名称：setCalllogLength
	 * 功能描述：设置通话记录的长度
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void setCalllogLength(int i) {
		this.calllogLength = i;
	}

	/**
	 * 函数名称：addKeywordListInfo
	 * 功能描述：单个添加关键字
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void addKeywordList(List<KeywordInfo> info) {
		synchronized (this.mKeywordList) {
			this.mKeywordList.addAll(info);
		}
	}
	/**
	 * 函数名称：addKeywordListInfo
	 * 功能描述：单个添加关键字
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void addKeywordListInfo(KeywordInfo info) {
		synchronized (this.mKeywordList) {
			ContentValues values = new ContentValues();
			values.clear();
			values.put(FieldAttribute.Keyword._KEY, info.getContent());
			int id = (int) mDataBase.insert(DataBaseHelper.DB_KEYWORD_TABLE, null, values);
			info.setId(id);
			this.mKeywordList.add(info);
		}
	}

	/**
	 * 函数名称：removeKeywordListInfo
	 * 功能描述：单个删除关键字
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void removeKeywordListInfo(int position) {
		synchronized (this.mKeywordList) {
			KeywordInfo info = mKeywordList.get(position);
			this.mKeywordList.remove(position);
			/** 删除该条记录 */
			String where = FieldAttribute.Keyword._ID + "=?";
			String[] whereValue = {String.valueOf(info.getId())};
			mDataBase.delete(DataBaseHelper.DB_KEYWORD_TABLE, where, whereValue);
		}
	}

	/**
	 * 函数名称：getKeywordListInfo
	 * 功能描述：得到关键字列表
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public List<KeywordInfo> getKeywordListInfo() {
		synchronized (this.mKeywordList) {
			return mKeywordList;
		}
	}

	/**
	 * 函数名称：addSmsListInfo
	 * 功能描述：单个添加拦截短信
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void addSmsListInfo(SmsInfo info) {
		synchronized (this.mSmsInfoList) {
			this.mSmsInfoList.add(0,info);
			ContentValues values = new ContentValues();
			values.clear();
			values.put(FieldAttribute.SmsInfest._NAME, info.getPhoneName());
			values.put(FieldAttribute.SmsInfest._NUMBER, info.getPhoneNumber());
			values.put(FieldAttribute.SmsInfest._REASON, info.getReason());
			values.put(FieldAttribute.SmsInfest._SMSID, info.getSmsId());
            values.put(FieldAttribute.SmsInfest._TIME, info.getTime());
			values.put(FieldAttribute.SmsInfest._CONTENT, info.getContent());
			mDataBase.insert(DataBaseHelper.DB_SMS_TABLE, null, values);
		}
	}

    /**
     * 函数名称：addSmsListInfoInit
     * 功能描述：单个添加拦截短信
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void addSmsListInfoInit(SmsInfo info) {
        synchronized (this.mSmsInfoList) {
            this.mSmsInfoList.add(0,info);
        }
    }

	/**
	 * 函数名称：removeSmsListInfo
	 * 功能描述：单个删除拦截短信
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public void removeSmsListInfo(int position) {
		synchronized (this.mSmsInfoList) {
			SmsInfo info = mSmsInfoList.get(position);
            Utils.log("SmsId=" + info.getSmsId());
			/** 删除该条记录 */
			String where = FieldAttribute.SmsInfest._SMSID + "=?";
			String[] whereValue = {info.getSmsId()};
			mDataBase.delete(DataBaseHelper.DB_SMS_TABLE, where, whereValue);
            this.mSmsInfoList.remove(position);
		}
	}

    /**
     * 函数名称：getSmsListInfo
     * 功能描述：得到拦截短信列表
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public List<SmsInfo> getSmsListInfo() {
        synchronized (this.mSmsInfoList) {
            return this.mSmsInfoList;
        }
    }
	/**
	 * 函数名称：getCallListInfo
	 * 功能描述：得到拦截短信列表
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public List<CallInfo> getCallListInfo() {
		synchronized (this.mCallInfoList) {
			return this.mCallInfoList;
		}
	}

    /**
     * 函数名称：addSmsListInfo
     * 功能描述：单个添加拦截短信
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void addCallListInfo(CallInfo info) {
        synchronized (this.mCallInfoList) {
            ContentValues values = new ContentValues();
            values.clear();
            values.put(FieldAttribute.CallInfest._NAME, info.getPhoneName());
            values.put(FieldAttribute.CallInfest._NUMBER, info.getPhoneNumber());
            values.put(FieldAttribute.CallInfest._REASON, info.getReason());
            values.put(FieldAttribute.CallInfest._TIME, info.getTime());
            values.put(FieldAttribute.CallInfest._LOCATION, info.getLocation());
            int id = (int)mDataBase.insert(DataBaseHelper.DB_CALL_TABLE, null, values);
            info.setCallId(String.valueOf(id));
            this.mCallInfoList.add(0,info);
        }
    }


    /**
     * 函数名称：addSmsListInfo
     * 功能描述：单个添加拦截短信
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void addCallListInfoInit(CallInfo info) {
        synchronized (this.mCallInfoList) {
            this.mCallInfoList.add(0,info);
        }
    }
    /**
     * 函数名称：removeSmsListInfo
     * 功能描述：单个删除拦截短信
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void removeCallListInfo(int position) {
        synchronized (this.mCallInfoList) {
            CallInfo info = mCallInfoList.get(position);
            Utils.log("CallId="+info.getCallId());
            /** 删除该条记录 */
            String where = FieldAttribute.CallInfest._ID + "=?";
            String[] whereValue = {info.getCallId()};
            mDataBase.delete(DataBaseHelper.DB_CALL_TABLE, where, whereValue);
            this.mCallInfoList.remove(position);
        }
    }




}
