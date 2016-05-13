package com.call_sms.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.call_sms.core.BlackListInfo;
import com.call_sms.core.InfestFilterConstant;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by konka on 2016-3-16.
 */
public class AddByContactsActivity extends Activity implements View.OnClickListener{

    private List<BlackListInfo> mBlackListInfo = null;
    private List<BlackListInfo> mContactsInfo = null;
    private List<BlackListInfo> mTempContactsInfo = null;
    private List<BlackListInfo> mListDataAdapter = null;
    private TextView mNoContactsView = null;
    private RelativeLayout mInitContactsView = null;
    private ContactsAdapter mAdapter = null;
    private ListView mListView = null;

    private Context mContext = null;
    private Handler mHandler = null;//装载刷新联系人线程的线程池
    private HandlerThread mThread = null;//刷新联系人线程
    private boolean initFlag = false;
    private final Thread mReadContactsThread = new Thread(){
        @Override
        public void run() {
            Utils.log("init");
            initContactsInfoList();
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case InfestFilterConstant.contactsInitEnd :
                    mInitContactsView.setVisibility(View.GONE);
                    refreshUI();
                    break;

            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getBaseContext();

        if(mThread ==null){
            mThread = new HandlerThread("contacts data");
            mThread.start();
        }
        if(mHandler == null){
            mHandler = new Handler(mThread.getLooper());
        }
        mHandler.post(mReadContactsThread);//开启加载联系人线程

        setContentView(R.layout.add_by_contacts_view);
        Button back = (Button)findViewById(R.id.id_bycontacts_back);
        back.setOnClickListener(this);
        Button ok = (Button)findViewById(R.id.add_by_contacts_ok);
        ok.setOnClickListener(this);

        mBlackListInfo = new ArrayList<BlackListInfo>();

        mNoContactsView = (TextView)findViewById(R.id.id_addcontacts_empty_view);
        mInitContactsView = (RelativeLayout)findViewById(R.id.id_addcontacts_init_view);
        mListView = (ListView)findViewById(R.id.id_addcontacts_list_view);
        mContactsInfo = new ArrayList<BlackListInfo>();
        mAdapter = new ContactsAdapter(mContext,mContactsInfo);
        mListView.setAdapter(mAdapter);
//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                switch (scrollState) {
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
//                        refreshUI();
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
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
            case R.id.id_bycontacts_back:
                this.finish();
                break;
            case R.id.add_by_contacts_ok:
                if(mBlackListInfo.size()>0){
                FilterApplication.getInstance().addBlackList(mBlackListInfo);
                Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                }
                this.finish();
                break;
        }
    }

    private void initContactsInfoList(){

        mTempContactsInfo = new ArrayList<BlackListInfo>();
        Cursor cursorRaw = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        int id = cursorRaw.getColumnIndex(ContactsContract.Contacts._ID);//取得联系人列表ID
        int name = cursorRaw.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);//取得姓名列

        int count = 0;
        try{
            while( cursorRaw.moveToNext())
            {
                String number= "";
                String IdContent = cursorRaw.getString(id);// 得到单个联系人的contacts_id值
                if(IdContent==null){
                    IdContent = "";
                    continue;
                }
                Cursor phone = getApplicationContext().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + IdContent, null, null);
                while (phone.moveToNext())
                {
                    number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    break;//只读取第一个联系人号码
                }
                phone.close();
                String nameContent = cursorRaw.getString(name);// 得到单个联系人的姓名值
                if(nameContent == null){
                    nameContent = "";
                }

                BlackListInfo info = new BlackListInfo();
                info.setPhoneNumber(number);
                info.setPhoneName(nameContent);
                mTempContactsInfo.add(info);

                count ++;
                if(count>100){
                    count = count -100;
                    Message msg = new Message();
                    msg.what =InfestFilterConstant.contactsInitEnd;
                    handler.sendMessage(msg);//发送联系人加载完毕Message
                }
            }

            Message msg = new Message();
            msg.what =InfestFilterConstant.contactsInitEnd;
            handler.sendMessage(msg);//发送联系人加载完毕Message

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            cursorRaw.close();
        }
    }

    private void refreshUI() {

        Iterator<BlackListInfo> it = mTempContactsInfo.iterator();

        mContactsInfo.clear();

        while(it.hasNext()){
            BlackListInfo next = it.next();
            if(FilterApplication.getInstance().getBlackList().size()>0) {
                Utils.log(FilterApplication.getInstance().getBlackList().contains(next) + "=contains");
            }
            mContactsInfo.add(next);
            next = null;
        }
        mAdapter.notifyDataSetChanged();

        int count = mAdapter.getCount();
        if(count == 0){
            mListView.setVisibility(View.GONE);
            mNoContactsView.setVisibility(View.VISIBLE);
        }else{
            mListView.setVisibility(View.VISIBLE);
            mNoContactsView.setVisibility(View.GONE);
        }
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
        public TextView number;
        public CheckBox selected;
    }

    /**
     * 函数名称：ContactsAdapter
     * 功能描述：自定义黑名单adapter
     * 输入参数：无
     * 输出参数：无
     * 返  回   值：无
     * 备          注：无
     */
    public class ContactsAdapter extends BaseAdapter {

        private LayoutInflater mLayoutInflater;

        public ContactsAdapter(Context context, List<BlackListInfo> listData)
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
        public BlackListInfo getItem(int position)
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
                convertView = mLayoutInflater.inflate(R.layout.add_by_contacts_view_item, (ViewGroup)null);

                viewHolder.name = (TextView) convertView.findViewById(R.id.id_add_by_contacts_name);
                viewHolder.number = (TextView) convertView.findViewById(R.id.id_add_by_contacts_number);
                viewHolder.selected = (CheckBox) convertView.findViewById(R.id.id_add_by_contacts_checkbox);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
                resetViewHolder(viewHolder);
            }

            initCheckValue(mListDataAdapter.size());

            final BlackListInfo info = mListDataAdapter.get(position);
            viewHolder.name.setText(info.getPhoneName());
            viewHolder.number.setText(info.getPhoneNumber());

            viewHolder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mBlackListInfo.add(info);
                        checkValueList.set(position, Type.Checked);
                    }else{
                        mBlackListInfo.remove(info);
                        checkValueList.set(position, Type.UnCheck);
                    }
                }
            });

            Utils.log("position="+position);
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
            viewHolder.number.setText("");

        }
    }


}
