/**
 * 版权所有          (C)2015,
 *
 * 文件名称：SettingPreference.java
 * 文件标识：设置内部共享数据类
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsPreference
{
    /** 配置相关变量 */
    public static final String PREFERENCE_NAME = "settings_preference";
    private SharedPreferences mSharedSetting = null;
    private static SettingsPreference mInstance = null;/**类的单例*/

    public static final String PREFERENCE_KEY_BLACKLIST_INFEST = "preferences_blacklist_infest"; /** 黑名单拦截 */
    public static final String PREFERENCE_KEY_STRANGECALL_INFEST = "preferences_strangecall_infest";   /** 陌生号码短信拦截 */
    public static final String PREFERENCE_KEY_STRANGESMS_INFEST = "preferences_strangesms_infest"; /** 陌生号码来电拦截 */
    public static final String PREFERENCE_KEY_NOTIFICATION_INFEST = "preferences_notification_infest"; /** 拦截提醒 */
    public static final String PREFERENCE_KEY_CALLBACK = "preferences_callback_settings"; /** 电话拦截返回音 */
    public static final String PREFERENCE_KEY_KEYWORD = "preferences_keyword_infest"; /** 关键词拦截 */
    /**
     * 函数名称：SettingPreference
     * 功能描述：构造函数
     * 输入参数：@param context 上下文
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public SettingsPreference(Context context)
    {
        mSharedSetting = context.getSharedPreferences(PREFERENCE_NAME, 0); /** 读取配置 */
    }

    /**
     * 函数名称：getInstance
     * 功能描述：单例模式的有限状态机
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：单一事例
     * 备          注：无
     */
    public static SettingsPreference getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new SettingsPreference(context);
        }
        return mInstance;
    }

    /**
     * 函数名称：getPreferenceBlackListInfest
     * 功能描述：获取黑名单拦截设置状态
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：来电播报状态
     * 备          注：无
     */
    public boolean getPreferenceBlackListInfest()
    {
        return mSharedSetting.getBoolean(PREFERENCE_KEY_BLACKLIST_INFEST, true);
    }

    /**
     * 函数名称：setPreferenceBlackListInfest
     * 功能描述：设置黑名单拦截状态
     * 输入参数：@param value  状态
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void setPreferenceBlackListInfest(boolean value)
    {
        /** 保存配置 */
        Editor editor = mSharedSetting.edit();
        editor.putBoolean(PREFERENCE_KEY_BLACKLIST_INFEST, value);
        editor.commit();
    }

    /**
     * 函数名称：getPreferenceStrangeCallInfest
     * 功能描述：获取陌生号码来电拦截状态
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：短信播报状态
     * 备          注：无
     */
    public boolean getPreferenceStrangeCallInfest()
    {
        return mSharedSetting.getBoolean(PREFERENCE_KEY_STRANGECALL_INFEST, true);
    }

    /**
     * 函数名称：setPreferenceStrangeCallInfest
     * 功能描述：设置陌生号码来电状态
     * 输入参数：@param value  状态
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void setPreferenceStrangeCallInfest(boolean value)
    {
        /** 保存配置 */
        Editor editor = mSharedSetting.edit();
        editor.putBoolean(PREFERENCE_KEY_STRANGECALL_INFEST, value);
        editor.commit();
    }

    /**
     * 函数名称：getPreferencevoiceDial
     * 功能描述：得到陌生号码短信拦截状态
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：来电播报状态
     * 备          注：无
     */
    public boolean getPreferenceStrangeSmsInfest()
    {
        return mSharedSetting.getBoolean(PREFERENCE_KEY_STRANGESMS_INFEST, true);
    }

    /**
     * 函数名称：setPreferenceStrangeSmsInfest
     * 功能描述：设置陌生号码短信拦截
     * 输入参数：@param value  状态
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void setPreferenceStrangeSmsInfest(boolean value)
    {
        /** 保存配置 */
        Editor editor = mSharedSetting.edit();
        editor.putBoolean(PREFERENCE_KEY_STRANGESMS_INFEST, value);
        editor.commit();
    }

    /**
     * 函数名称：getPreferenceNotification
     * 功能描述：获取拦截提醒设置状态
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：来电播报状态
     * 备          注：无
     */
    public boolean getPreferenceNotification()
    {
        return mSharedSetting.getBoolean(PREFERENCE_KEY_NOTIFICATION_INFEST, true);
    }

    /**
     * 函数名称：setPreferenceNotification
     * 功能描述：设置拦截提醒状态
     * 输入参数：@param value  状态
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void setPreferenceNotification(boolean value)
    {
        /** 保存配置 */
        Editor editor = mSharedSetting.edit();
        editor.putBoolean(PREFERENCE_KEY_NOTIFICATION_INFEST, value);
        editor.commit();
    }

    /**
     * 函数名称：getPreferenceCallBack
     * 功能描述：获取返回音选项状态
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：短信播报状态
     * 备          注：无
     */
    public int getPreferenceCallBack()
    {
        return mSharedSetting.getInt(PREFERENCE_KEY_CALLBACK,1);
    }

    /**
     * 函数名称：setPreferenceCallBack
     * 功能描述：设置返回音选项状态
     * 输入参数：@param value  状态
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void setPreferenceCallBack(int value)
    {
        /** 保存配置 */
        Editor editor = mSharedSetting.edit();
        editor.putInt(PREFERENCE_KEY_CALLBACK, value);
        editor.commit();
    }

    /**
     * 函数名称：getPreferenceKeyword
     * 功能描述：得到关键字拦截状态
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：来电播报状态
     * 备          注：无
     */
    public boolean getPreferenceKeyword()
    {
        return mSharedSetting.getBoolean(PREFERENCE_KEY_KEYWORD, true);
    }

    /**
     * 函数名称：setPreferenceKeyword
     * 功能描述：设置关键字拦截
     * 输入参数：@param value  状态
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public void setPreferenceKeyword(boolean value)
    {
        /** 保存配置 */
        Editor editor = mSharedSetting.edit();
        editor.putBoolean(PREFERENCE_KEY_KEYWORD, value);
        editor.commit();
    }
}
