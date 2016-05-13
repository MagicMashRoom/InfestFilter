package com.call_sms.data;

/**
 * Created by konka on 2016-3-15.
 */
public class FieldAttribute {
    /** 黑名单表字段 */
    public class BlackList
    {
        /** 字段 */
        public static final String _ID = "_id";
        public static final String _NAME = "_name";
        public static final String _PHONE_NUMBER = "_number";
        /** 字段索引值 */
        public static final int INDEX_ID_COLUMN = 0;
        public static final int INDEX_NAME = 1;
        public static final int INDEX_PHONE_NUMBER = 2;
    }

    /** 关键表字段 */
    public class Keyword
    {
        /** 字段 */
        public static final String _ID = "_id";
        public static final String _KEY = "_key";
        /** 字段索引值 */
        public static final int INDEX_ID_COLUMN = 0;
        public static final int INDEX_KEY = 1;
    }

    /** 拦截短信字段 */
    public class SmsInfest
    {
        /** 字段 */
        public static final String _ID = "_id";
        public static final String _SMSID = "_smsid";
        public static final String _NUMBER = "_number";
        public static final String _NAME = "_name";
        public static final String _TIME = "_time";
        public static final String _CONTENT = "_content";
        public static final String _REASON = "_reason";

        /** 字段索引值 */
        public static final int INDEX_ID_COLUMN = 0;
        public static final int INDEX_SMSID_COLUMN = 1;
        public static final int INDEX_NUMBER_COLUMN = 2;
        public static final int INDEX_NAME_COLUMN = 3;
        public static final int INDEX_TIME_COLUMN = 4;
        public static final int INDEX_CONTENT_COLUMN = 5;
        public static final int INDEX_REASON_COLUMN = 6;
    }

    /** 拦截短信字段 */
    public class CallInfest
    {
        /** 字段 */
        public static final String _ID = "_id";
        public static final String _CALLID = "_callid";
        public static final String _NUMBER = "_number";
        public static final String _NAME = "_name";
        public static final String _TIME = "_time";
        public static final String _LOCATION = "_locatioon";
        public static final String _REASON = "_reason";

        /** 字段索引值 */
        public static final int INDEX_ID_COLUMN = 0;
        public static final int INDEX_CALLID_COLUMN = 1;
        public static final int INDEX_NUMBER_COLUMN = 2;
        public static final int INDEX_NAME_COLUMN = 3;
        public static final int INDEX_TIME_COLUMN = 4;
        public static final int INDEX_LOCATION_COLUMN = 5;
        public static final int INDEX_REASON_COLUMN = 6;
    }
    /**
     * 函数名称：FieldAttribute
     * 功能描述：构造函数
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public FieldAttribute()
    {
        //记住一定要生成构造函数
    }
}
