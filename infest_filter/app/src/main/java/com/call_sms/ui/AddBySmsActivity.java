package com.call_sms.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.call_sms.core.BlackListInfo;
import com.call_sms.core.SmsInfo;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by konka on 2016-3-16.
 */
public class AddBySmsActivity extends Activity implements View.OnClickListener{

    private List<BlackListInfo> mBlackListInfo = null;
    private List<BlackListInfo> mTempContactsInfo = null;
    private List<SmsInfo> mSmsInfo = null;
    private List<SmsInfo> mListDataAdapter = null;
    private TextView mNoSmsView = null;
    private AddSmsAdapter mAdapter = null;/**自定义的添加短信的适配器*/
    private ListView mListView = null;
    private String threadId;
    private Context mContext = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_by_sms_view);

        Button back = (Button)findViewById(R.id.id_bysms_back);
        back.setOnClickListener(this);
        Button ok = (Button)findViewById(R.id.add_by_sms_ok);
        ok.setOnClickListener(this);

        mBlackListInfo = new ArrayList<BlackListInfo>();
        mTempContactsInfo = new ArrayList<BlackListInfo>();

        mNoSmsView = (TextView)findViewById(R.id.id_addsms_empty_view);

        mListView = (ListView)findViewById(R.id.id_addsms_list_view);
        mSmsInfo = new ArrayList<SmsInfo>();
        mSmsInfo.addAll(getSmsData(mContext));
        mAdapter = new AddSmsAdapter(mContext,mSmsInfo);/** 放入上下文 和已经初始化好的sms信息*/
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        int count = mAdapter.getCount();
        if(count == 0){
            mListView.setVisibility(View.GONE);
            mNoSmsView.setVisibility(View.VISIBLE);
        }else{
            mListView.setVisibility(View.VISIBLE);
            mNoSmsView.setVisibility(View.GONE);
        }
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
            case R.id.id_bysms_back:
                this.finish();
                break;
            case R.id.add_by_sms_ok:
                if(mBlackListInfo.size()>0){
                    FilterApplication.getInstance().addBlackList(mBlackListInfo);
                    Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                }
                this.finish();
                break;
        }
    }

    /**得到短信数据库内容*/
    public List<SmsInfo> getSmsData(Context c){

        Utils.log("getSmsData");
        List<SmsInfo> ListData = new ArrayList<SmsInfo>();

        String[] projection = new String[] { "thread_id", "address", "person",
                "body", "date", "type" };
        Cursor cursor = c.getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, projection, null, null,
                "date desc");
        Utils.log("getCursor");
        int threadIdColumn = cursor.getColumnIndex("thread_id");
        int nameColumn = cursor.getColumnIndex("person");
        int phoneNumberColumn = cursor.getColumnIndex("address");
        int smsbodyColumn = cursor.getColumnIndex("body");
        int dateColumn = cursor.getColumnIndex("date");
        try{
        while(cursor != null&&cursor.moveToNext()) {
            String threadid = cursor.getString(threadIdColumn);
            if(!threadid.equals(threadId)){//threadId应该比原先大
            threadId = threadid;

            SmsInfo smsinfo = new SmsInfo();

            String name = cursor.getString(nameColumn);
                Utils.log("name="+name);
            String number = cursor.getString(phoneNumberColumn);

            BlackListInfo info = new BlackListInfo();
            if(name.equals("0")){
                smsinfo.setPhoneNumber("");
                smsinfo.setPhoneName(number);
                info.setPhoneNumber(number);
                info.setPhoneName(number);
            }else{
                smsinfo.setPhoneNumber(number);
                smsinfo.setPhoneName(name);
                info.setPhoneNumber(number);
                info.setPhoneName(name);
            }
            mTempContactsInfo.add(info);

            smsinfo.setContent(trim(cursor.getString(smsbodyColumn)));

            Calendar cal=Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(cursor.getString(dateColumn)));
            smsinfo.setTime(String.format("%02d/%02d %02d:%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

            ListData.add(smsinfo);
            }
        }
        }catch(Exception e){
        e.printStackTrace();
        }finally {
        cursor.close();
        }
        return ListData;
    }
//去除首尾空格
    public String trim(String str){
        str = str.trim();
        while (str.startsWith("　")) {//这里判断是不是全角空格
            str = str.substring(1, str.length()).trim();
        }
        while (str.endsWith("　")) {
            str = str.substring(0, str.length() - 1).trim();
        }
        return str;
    }

    public enum Type {
        Checked, UnCheck;
    }
    private List<Type> checkValueList = new ArrayList<Type>();//联系人adapter数据对比列表

    private void initCheckValue(int count) {//初始化联系人对比列表
        for(int i=0;i<count;i++){
            Type t = Type.UnCheck;
            checkValueList.add(t);
        }
    }
    /**
     * 函数名称：ViewHolder
     * 功能描述：ViewHolder类
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public final class ViewHolder {
        public TextView name;
        public TextView content;
        public CheckBox selected;
    }

    /**
     * 函数名称：BlackListAdapter
     * 功能描述：自定义黑名单adapter
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public class AddSmsAdapter extends BaseAdapter {

        private LayoutInflater mLayoutInflater;

        public AddSmsAdapter(Context context, List<SmsInfo> listData)
        {
            mContext = context;
            mListDataAdapter = listData;
            /** 在程序中动态加载每行的布局   	 */
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        /**
         * 函数名称：getCount
         * 功能描述：获取总数
         * 输入参数：无
         * 输出参数：无
         * 返  回   值：无
         * 备          注：无
         */
        @Override
        public int getCount()
        {
            return mListDataAdapter.size();
        }

        /**
         * 函数名称：getItem
         * 功能描述：
         * 输入参数：@param position
         * 输出参数：无
         * 返  回   值：无
         * 备          注：无
         */
        @Override
        public SmsInfo getItem(int position)
        {
            return mListDataAdapter.get(position);
        }

        /**
         * 函数名称：getItemId
         * 功能描述：
         * 输入参数：@param position
         * 输出参数：无
         * 返  回   值：无
         * 备          注：无
         */
        @Override
        public long getItemId(int position)
        {
            return position;
        }


        /**
         * 函数名称：getView
         * 功能描述： 这个方法getView()，是用来逐一绘制每一条item
         * 输入参数： @param  position   就是位置从0开始
         *           @param  convertView  是Spinner,ListView,GridView中每一项要显示的view
         *           @param  parent 父窗体了，也就是Spinner,ListView,GridView了
         * 输出参数：无
         * 返  回   值： 无
         * 备          注：无
         */
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder = null;

            if (convertView == null)
            {
                /** 在程序中动态加载每行的布局   	 */
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.add_by_sms_view_item, (ViewGroup)null);

                viewHolder.name = (TextView) convertView.findViewById(R.id.id_add_by_sms_name);
                viewHolder.content = (TextView) convertView.findViewById(R.id.id_add_by_sms_content);
                viewHolder.selected = (CheckBox) convertView.findViewById(R.id.id_add_by_sms_checkbox);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
                resetViewHolder(viewHolder);
            }

            /** 初始化比对值*/
            if(checkValueList.size()==0){
                initCheckValue(mListDataAdapter.size());
            }

            SmsInfo info = mListDataAdapter.get(position);
            viewHolder.name.setText(info.getPhoneName());
            viewHolder.content.setText("["+info.getTime()+"]"+" "+info.getContent());

//            final BlackListInfo blackInfo = new BlackListInfo();
//            blackInfo.setPhoneNumber(info.getPhoneNumber());
//            blackInfo.setPhoneName(info.getPhoneName());
            viewHolder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mBlackListInfo.add(mTempContactsInfo.get(position));
                        checkValueList.set(position, Type.Checked);
                    }else{
                        mBlackListInfo.remove(mTempContactsInfo.get(position));
                        checkValueList.set(position, Type.UnCheck);
                    }
                }
            });

            /** 防止因为View重用导致checkBox的值混乱*/
            if (checkValueList.get(position) == Type.Checked) {
                viewHolder.selected.setChecked(true);
            } else if (checkValueList.get(position) == Type.UnCheck) {
                viewHolder.selected.setChecked(false);
            } else {
                viewHolder.selected.setChecked(false);
            }
            return convertView;
        }

        /**
         * 函数名称：resetViewHolder
         * 功能描述：复位viewHolder
         * 输入参数：@param viewHolder
         * 输出参数：无
         * 返  回   值：无
         * 备          注：如果不复位viewHolder，则滚动，点击操作会导致重复和混乱
         */
        private void resetViewHolder(ViewHolder viewHolder)
        {
            viewHolder.name.setText("");
            viewHolder.content.setText("");

        }
    }

}
