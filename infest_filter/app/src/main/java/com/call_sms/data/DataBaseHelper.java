/**
 * 版权所有          (C)2015,
 *
 * 文件名称：DataBaseHelper.java
 * 文件标识：数据库操作类
 * 内容摘要：
 * 其它说明：
 * 当前版本：1.0
 * 作	  者：
 * 完成日期：
 * 修改记录：
 * 修改日期：
 * 版   本  号：
 * 修   改  人：
 * 修改内容：
 * 备          注：
 */

package com.call_sms.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.call_sms.infest_filter.Utils;

public class DataBaseHelper extends SQLiteOpenHelper
{
    private final static int DATABASE_VERSION = 1;   /** 数据库版本  */
    public final static String	DATABASE_NAME	= "infest_filter.db";  /** 数据库名 */

    public  static final String DB_BLACK_LIST_TABLE = "black_list_table";  /**黑名单表名*/
    private static final String DB_BLACK_LIST_TABLE_CREATE =
        "CREATE TABLE " + DB_BLACK_LIST_TABLE +" ("
                + FieldAttribute.BlackList._ID + " integer primary key autoincrement,"
                + FieldAttribute.BlackList._NAME + " NVARCHAR,"
                + FieldAttribute.BlackList._PHONE_NUMBER + " NVARCHAR"
                + ");";

    public  static final String DB_KEYWORD_TABLE = "keyword_table";  /**关键字表名*/
    private static final String DB_KEYWORD_TABLE_CREATE =
        "CREATE TABLE " + DB_KEYWORD_TABLE +" ("
                + FieldAttribute.Keyword._ID + " integer primary key autoincrement,"
                + FieldAttribute.Keyword._KEY + " NVARCHAR"
                + ");";

    public  static final String DB_SMS_TABLE = "sms_table";  /**拦截短信表名*/
    private static final String DB_SMS_TABLE_CREATE =
        "CREATE TABLE " + DB_SMS_TABLE +" ("
                + FieldAttribute.SmsInfest._ID + " integer primary key autoincrement,"
                + FieldAttribute.SmsInfest._SMSID + " NAVRCHAR,"
                + FieldAttribute.SmsInfest._NAME + " NAVRCHAR,"
                + FieldAttribute.SmsInfest._NUMBER + " NAVRCHAR,"
                + FieldAttribute.SmsInfest._TIME + " NAVRCHAR,"
                + FieldAttribute.SmsInfest._CONTENT + " NAVRCHAR,"
                + FieldAttribute.SmsInfest._REASON + " INTERGER(4)"
                + ");";

    public  static final String DB_CALL_TABLE = "call_table";  /**拦截通话表名*/
    private static final String DB_CALL_TABLE_CREATE =
        "CREATE TABLE " + DB_CALL_TABLE +" ("
                + FieldAttribute.CallInfest._ID + " integer primary key autoincrement,"
                + FieldAttribute.CallInfest._CALLID + " NAVRCHAR,"
                + FieldAttribute.CallInfest._NUMBER + " NAVRCHAR,"
                + FieldAttribute.CallInfest._NAME + " NAVRCHAR,"
                + FieldAttribute.CallInfest._TIME + " NAVRCHAR,"
                + FieldAttribute.CallInfest._LOCATION + " NAVRCHAR,"
                + FieldAttribute.CallInfest._REASON + " NAVRCHAR"
                + ");";
    /**
     * 函数名称：DataBaseHelper
     * 功能描述：构造函数
     * 输入参数：@param context  上下文
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Utils.log("DataBaseHelperBePush");
    }

    /**
     * 函数名称：onCreate
     * 功能描述：创建数据库
     * 输入参数：@param db 数据库对象
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void onCreate(SQLiteDatabase db)
    {
        Utils.log("DataOnCreate");
        db.execSQL(DB_SMS_TABLE_CREATE);
        db.execSQL(DB_BLACK_LIST_TABLE_CREATE);
        db.execSQL(DB_KEYWORD_TABLE_CREATE);
        db.execSQL(DB_CALL_TABLE_CREATE);
    }

    /**
     * 函数名称：onUpgrade
     * 功能描述：更新数据库
     * 输入参数：@param db 数据库对象
     *          @param oldVersion  旧版本号
     *          @param newVersion  新版本号
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DB_BLACK_LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DB_KEYWORD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DB_SMS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DB_CALL_TABLE);
        onCreate(db);
    }

    /**
     * 函数名称：getDatabase
     * 功能描述：打开数据库
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：数据库对象
     * 备          注：无
     */
    public SQLiteDatabase  getDatabase()
    {
        SQLiteDatabase db;

        try
        {
            db = this.getWritableDatabase();
        }
        catch (SQLiteException ex)
        {
            db = this.getReadableDatabase();
        }

        return db;
    }

    /**
     * 函数名称：getBlackCursor
     * 功能描述：获取联系人数据库游标
     * 输入参数：@param db 数据库对象
     * 输出参数：无
     * 返  回   值：游标
     * 备          注：位置字段升序
     */
    public Cursor getBlackListCursor(SQLiteDatabase db)
    {
        return db.query(DB_BLACK_LIST_TABLE, null, null, null, null,null,FieldAttribute.BlackList._ID + " ASC");
    }

    /**
     * 函数名称：getKeywordCursor
     * 功能描述：获取应用数据库游标
     * 输入参数：@param db 数据库对象
     * 输出参数：无
     * 返  回   值：游标
     * 备          注：位置字段升序
     */
    public Cursor getKeywordCursor(SQLiteDatabase db)
    {
        return db.query(DB_KEYWORD_TABLE, null, null, null, null,null,FieldAttribute.Keyword._ID + " ASC");
    }

    /**
     * 函数名称：getSmsCursor
     * 功能描述：获取应用数据库游标
     * 输入参数：@param db 数据库对象
     * 输出参数：无
     * 返  回   值：游标
     * 备          注：位置字段升序
     */
    public Cursor getSmsCursor(SQLiteDatabase db)
    {
        return db.query(DB_SMS_TABLE, null, null, null, null,null,FieldAttribute.SmsInfest._ID + " ASC");
    }

    /**
     * 函数名称：getSmsCursor
     * 功能描述：获取应用数据库游标
     * 输入参数：@param db 数据库对象
     * 输出参数：无
     * 返  回   值：游标
     * 备          注：位置字段升序
     */
    public Cursor getCallCursor(SQLiteDatabase db)
    {
        return db.query(DB_CALL_TABLE, null, null, null, null,null,FieldAttribute.CallInfest._ID + " ASC");
    }
}
