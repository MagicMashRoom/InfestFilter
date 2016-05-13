package com.call_sms.core;

/**
 * Created by konka on 2016-4-7.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.call_sms.data.DataBaseHelper;
import com.call_sms.data.FieldAttribute;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class InitializeDataTask extends AsyncTask<Void, Void, Void>
{
    private WeakReference<Context> mContext = null; /** 上下文 */
    private List<KeywordInfo> mKeywordList = null; /** 关键字列表  */
    private List<BlackListInfo> mBlackList = null; /** 黑名单列表  */
    private List<SmsInfo> mSmsList = null; /** 短信列表  */
    /**
     * 函数名称：InitializeDataTask
     * 功能描述：构造函数
     * 输入参数：@param context  上下文
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public InitializeDataTask(Context context)
    {
        mContext = new WeakReference<Context>(context);
    }

    /**
     * 函数名称：onPreExecute
     * 功能描述：UI调用的执行函数
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        Utils.log("onPreExecute");
        mKeywordList = new ArrayList<KeywordInfo>();
        mBlackList = new ArrayList<BlackListInfo>();
    }

    /**
     * 函数名称：doInBackground
     * 功能描述：后台刷新数据
     * 输入参数：@param  params
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    @Override
    protected Void doInBackground(Void... params)
    {
        DataBaseHelper dbHelper = FilterApplication.getInstance().getDataBaseHelper();
        SQLiteDatabase db = FilterApplication.getInstance().getSQLiteDatabase();
        Cursor cursorKeyword = dbHelper.getKeywordCursor(db);
        Cursor cursorBlack = dbHelper.getBlackListCursor(db);
        Cursor cursorSms = dbHelper.getSmsCursor(db);
        Cursor cursorCall = dbHelper.getCallCursor(db);
        writeDefaultData(cursorKeyword,cursorBlack,cursorSms,cursorCall);  /** 写缺省数据 */
        return null;
    }

    /**
     * 函数名称：onPostExecute
     * 功能描述：线程最后执行步骤
     * 输入参数：@param  result
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    @Override
    protected void onPostExecute(Void result)
    {
        if(!isCancelled()) /** 线程没有被外部停止 */
        {
            FilterApplication.getInstance().addBlackList(mBlackList);
            FilterApplication.getInstance().addKeywordList(mKeywordList);

            FilterApplication.getInstance().sendMainBroadcast();
            Utils.log("sendMainBroadcast");
            FilterApplication.getInstance().initializeFinish(); /** 装载结束 */
        }

        this.mBlackList.clear();
        this.mKeywordList.clear();
        this.cancel(true);
    }




    /**
     * 函数名称：writeDefaultData
     * 功能描述：写缺省数据
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    private void writeDefaultData(Cursor c1,Cursor c2,Cursor c3,Cursor c4) {
        /** 获取数据  Keyword*/
        try{
            while(c1.moveToNext()){

                KeywordInfo info = new KeywordInfo();
                info.setId(c1.getInt(FieldAttribute.Keyword.INDEX_ID_COLUMN));
                info.setContent(c1.getString(FieldAttribute.Keyword.INDEX_KEY));
                mKeywordList.add(info);
                info = null;
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
                info.setId(c2.getInt(FieldAttribute.BlackList.INDEX_ID_COLUMN));
                info.setPhoneName(c2.getString(FieldAttribute.BlackList.INDEX_NAME));
                info.setPhoneNumber(c2.getString(FieldAttribute.BlackList.INDEX_PHONE_NUMBER));
                mBlackList.add(info);
                info = null;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            c2.close();
        }

        /** 保存数据  Sms*/
        try{
            while(c3.moveToNext()){
                SmsInfo info = new SmsInfo();
                info.setSmsId(c3.getString(FieldAttribute.SmsInfest.INDEX_SMSID_COLUMN));
                info.setPhoneName(c3.getString(FieldAttribute.SmsInfest.INDEX_NAME_COLUMN));
                info.setPhoneNumber(c3.getString(FieldAttribute.SmsInfest.INDEX_NUMBER_COLUMN));
                info.setContent(c3.getString(FieldAttribute.SmsInfest.INDEX_CONTENT_COLUMN));
                info.setTime(c3.getString(FieldAttribute.SmsInfest.INDEX_TIME_COLUMN));
                info.setReason(c3.getInt(FieldAttribute.SmsInfest.INDEX_REASON_COLUMN));
                FilterApplication.getInstance().addSmsListInfoInit(info);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            c3.close();
        }

        /** 保存数据  Call*/
        try{
            while(c4.moveToNext()){
                CallInfo info = new CallInfo();
                info.setCallId(c4.getString(FieldAttribute.CallInfest.INDEX_ID_COLUMN));
                info.setPhoneName(c4.getString(FieldAttribute.CallInfest.INDEX_NAME_COLUMN));
                info.setPhoneNumber(c4.getString(FieldAttribute.CallInfest.INDEX_NUMBER_COLUMN));
                info.setLocation(c4.getString(FieldAttribute.CallInfest.INDEX_LOCATION_COLUMN));
                info.setTime(c4.getString(FieldAttribute.CallInfest.INDEX_TIME_COLUMN));
                info.setReason(c4.getInt(FieldAttribute.CallInfest.INDEX_REASON_COLUMN));
                FilterApplication.getInstance().addCallListInfoInit(info);
                Utils.log("phoneNumber="+info.getPhoneNumber());
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            c4.close();
        }
    }

}

