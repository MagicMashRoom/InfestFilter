package com.call_sms.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.call_sms.adapter.CallAdapter;
import com.call_sms.core.BlackListInfo;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by konka on 2016-3-16.
 */
public class AddByCalllogActivity extends Activity implements View.OnClickListener{

    private Context mContext = null;
    private RelativeLayout mInitCalllogView = null;//初始化通话记录布局
    private TextView mNoCalllogView = null;//无通话记录布局
    private ListView mCalllogListView = null;//通话记录布局
    private CallLogsLoaderListener mCallLogsCallback = null;//通话记录回调
    private CallAdapter mCallAdapter = null; //通话记录adapter
    private CallLogsContentObserver mCallLogsContent = null;
    private Cursor mCalllogCursor = null;

    private List<BlackListInfo> mBlackListInfo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getBaseContext();
        setContentView(R.layout.add_by_calllog_view);
        Button back = (Button)findViewById(R.id.id_bycalllog_back);
        back.setOnClickListener(this);
        Button ok = (Button)findViewById(R.id.add_by_calllog_ok);
        ok.setOnClickListener(this);

        mInitCalllogView =(RelativeLayout)findViewById(R.id.id_addcalllog_init_view);
        mNoCalllogView = (TextView)findViewById(R.id.id_addcalllog_empty_view);
        mCalllogListView = (ListView)findViewById(R.id.id_addcallog_list_view);
        mCallLogsCallback = new CallLogsLoaderListener();
        getLoaderManager().initLoader(1, null, mCallLogsCallback);  /** 通话记录列表 */
        mCallAdapter = new CallAdapter(mContext, null);
        mCalllogListView.setAdapter(mCallAdapter);

        mBlackListInfo = new ArrayList<BlackListInfo>();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.id_bycalllog_back:
                this.finish();
                break;
            case R.id.add_by_calllog_ok:
                FilterApplication.getInstance().addBlackList(mBlackListInfo);
                if(mBlackListInfo.size()>0){
                Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                }
                this.finish();
        }
    }

    /** 加载器的监听器 */
    private class CallLogsLoaderListener implements LoaderManager.LoaderCallbacks<Cursor>
    {
        @Override
        public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args)
        {
            return new CursorLoader(mContext, CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        }

        @Override
        public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data)
        {
            mCalllogCursor = data;
            FilterApplication.getInstance().setCalllogLength(data.getCount());
            Utils.log("size=" + FilterApplication.getInstance().getCalllogLength());
            mCallAdapter.swapCursor(data);
            mInitCalllogView.setVisibility(View.GONE);
            int count = mCallAdapter.getCount();
            if(count==0){
                mCalllogListView.setVisibility(View.GONE);
                mNoCalllogView.setVisibility(View.VISIBLE);
            }else{
                mCalllogListView.setVisibility(View.VISIBLE);
                mNoCalllogView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoaderReset(android.content.Loader<Cursor> loader)
        {
            mCalllogCursor = null;
            mCallAdapter.swapCursor(null);
        }
    }

    /** 监控通话记录是否有改变  */
    private class CallLogsContentObserver extends ContentObserver
    {
        public CallLogsContentObserver(Handler handler)
        {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange)
        {
            Cursor cursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            mCalllogCursor = cursor;
            mCallAdapter.swapCursor(mCalllogCursor);
            mCallAdapter.notifyDataSetChanged();
            int count = mCallAdapter.getCount();
            if(count==0){
                mCalllogListView.setVisibility(View.GONE);
                mNoCalllogView.setVisibility(View.VISIBLE);
            }else{
                mCalllogListView.setVisibility(View.VISIBLE);
                mNoCalllogView.setVisibility(View.GONE);
            }
        }
    }


    public enum Type {
        Checked, UnCheck;
    }

    private List<Type> checkValueList = new ArrayList<Type>();

    public class CallAdapter extends CursorAdapter {

        private Context mContext = null;

        /**
         * 函数名称：addCallAdapter
         * 功能描述：构造函数
         * 输入参数：@param context  上下文
         *
         * @param context
         * @param c
         */
        public CallAdapter(Context context, Cursor c) {
            super(context, c);
            mContext = context;
        }

        private void initCheckValue(int count) {
            for(int i=0;i<count;i++){
                Type t = Type.UnCheck;
                checkValueList.add(t);
            }
        }

        /**
         * 函数名称：newView
         * 功能描述：获取ListView中的Item 界面元素的函数
         * 输入参数：@param arg0
         *          @param arg1  当前游标
         *          @param arg2
         * 输出参数：无
         * 返  回   值：无
         * 备          注：该函数第一次回调用后，如果数据增加后也会再调用，但是重绘是不会调用的。
         *        数据增加后,回调用该函数来生成与新增数据相对应的view
         */
        @Override
        public View newView(Context arg0, Cursor arg1, ViewGroup arg2)
        {
            View convertView = View.inflate(mContext, R.layout.add_by_calllog_view_item, null);

            return convertView;
        }

        /**
         * 函数名称：bindView
         * 功能描述：绑定视图函数
         * 输入参数：@param arg0  当前ListView 的Item View，在View 中包含了ListView Item 界面元素
         *          @param arg1
         *          @param arg2  当前游标
         * 输出参数：无
         * 返  回   值：无
         * 备          注：函数第一次回调用后，如果数据更新也会再调用，但重绘会再次调用的，总
         *          的来说应该是在调用bindView如果发现view为空会先调用newView来生成view。
         */
        @Override
        public void bindView(View arg0, Context arg1, Cursor arg2)
        {
            TextView nameView =  (TextView)arg0.findViewById(R.id.id_add_by_calllog_name);//联系人姓名
            TextView contentView = (TextView)arg0.findViewById(R.id.id_add_by_calllog_content);//通话记录内容
            CheckBox selected  = (CheckBox)arg0.findViewById(R.id.id_add_by_calllog_checkbox);//勾选框
            /** 通话记录的姓名 或者号码 */
            final BlackListInfo info = new BlackListInfo();
            final int position = arg2.getPosition();
            /** 初始化比对值*/
            if(checkValueList.size()==0){
                initCheckValue(arg2.getCount());
            }

            String name = arg2.getString(arg2.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = arg2.getString(arg2.getColumnIndex(CallLog.Calls.NUMBER));

            Calendar cal=Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(arg2.getString(arg2.getColumnIndex(CallLog.Calls.DATE))));
            String date = String.format("%02d/%02d %02d:%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

            String location = arg2.getString(arg2.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));

            if(name == null)
            {
                nameView.setText(number);
                contentView.setText("["+date+"]"+" "+location);
                info.setPhoneName(number);
                info.setPhoneNumber(number);
            }
            else
            {
                nameView.setText(name);
                contentView.setText("["+date+"]"+" "+location);
                info.setPhoneName(name);
                info.setPhoneNumber(number);
            }

            selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mBlackListInfo.add(info);
                        checkValueList.set(position, Type.Checked);
                    } else {
                        mBlackListInfo.remove(info);
                        checkValueList.set(position, Type.UnCheck);
                    }
                }
            });
            /** 防止因为View重用导致checkBox的值混乱*/
            if (checkValueList.get(position) == Type.Checked) {
                selected.setChecked(true);
            } else if (checkValueList.get(position) == Type.UnCheck) {
                selected.setChecked(false);
            } else {
                selected.setChecked(false);
            }
        }
    }

}
