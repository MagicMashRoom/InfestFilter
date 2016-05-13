package com.call_sms.fragment;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.call_sms.adapter.CallAdapter;
import com.call_sms.core.CallInfo;
import com.call_sms.core.InfestContentCallActivity;
import com.call_sms.core.InfestFilterConstant;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.FilterSettingsActivity;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CallFragment extends Fragment implements View.OnClickListener{

	private Context mContext = null;
	private RelativeLayout mNoCalllogView = null;//无通话记录布局
	private ListView mCalllogListView = null;//通话记录布局
	private CallAdapter mCallAdapter = null; //通话记录adapter
	private List<CallInfo> mListData = null;//拦截电话记录
	private CallLogsContentObserver mCallLogsContent = null;

	private NotificationCompat.Builder mBuilderCall;//Notification构造器
    private SimpleDateFormat formatter= new SimpleDateFormat("ddHHmmss");

    @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {             
		 super.onCreate(savedInstanceState);
		 mContext = getContext();
         mListData = new ArrayList<CallInfo>();
		 mListData.addAll(FilterApplication.getInstance().getCallListInfo());
         Utils.log("size="+mListData.size());
		 mCallAdapter = new CallAdapter(getContext(), mListData);

	 }

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	 {
		 View contentView = inflater.inflate(R.layout.call_layout, container, false);
		 ImageView settings = (ImageView)contentView.findViewById(R.id.id_call_settings);
		 settings.setOnClickListener(this);
		 mCalllogListView = (ListView)contentView.findViewById(R.id.id_call_list_view);
		 mCalllogListView.setAdapter(mCallAdapter);
		 mCalllogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		 @Override
		 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			 Utils.log("IntentStart");
			 /** 用intent启动骚扰详情 */
			 Intent intent = new Intent();
			 Bundle bundle = new Bundle();
			 CallInfo cif = mListData.get(position);
			 bundle.putSerializable(InfestFilterConstant.CALL_DATA, cif);
			 intent.putExtras(bundle);
			 intent.setClass(mContext, InfestContentCallActivity.class);
			 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 startActivity(intent);
		 }
	 });
		 mCallLogsContent = new CallLogsContentObserver(new Handler());
		 mNoCalllogView = (RelativeLayout)contentView.findViewById(R.id.id_call_no_call);
		 return contentView;
	 }

	 @Override
	 public void onActivityCreated(Bundle savedInstanceState) 
	 {
		 super.onActivityCreated(savedInstanceState);
	 }

	 @Override
	 public void onResume() 
	 {
		 super.onResume();
         int count = mCallAdapter.getCount();
         if (count == 0) {
             mCalllogListView.setVisibility(View.GONE);
             mNoCalllogView.setVisibility(View.VISIBLE);
         } else {
             mCalllogListView.setVisibility(View.VISIBLE);
             mNoCalllogView.setVisibility(View.GONE);
         }
		 /** 注册通话记录监控 */
		 getActivity().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, false, mCallLogsContent);

	 }

	 @Override
	 public void onPause() 
	 {
		 super.onPause(); 
	 }
	
	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.id_call_settings:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(getActivity(), FilterSettingsActivity.class);
			getActivity().startActivity(intent);
			break;
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
		public void onChange(boolean selfChange) {
            if (FilterApplication.getInstance().getCallInfestFlag()) {
                ContentResolver resolver = mContext.getContentResolver();
                Utils.log("getCallInfestFlag");
                Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
                try{
                    if (cursor.moveToNext()) {
                        CallInfo info = new CallInfo();
                        if (cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)) == null) {
                            info.setPhoneName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                            info.setPhoneNumber("");
                        } else {
                            info.setPhoneName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
                            info.setPhoneNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))));
                        String date = String.format("%02d/%02d %02d:%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                        info.setTime(date);
                        String location = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));
                        info.setLocation(location);
                        info.setReason(FilterApplication.getInstance().getCallInfestType());
                        showFilterActivityNotify(info);
                        FilterApplication.getInstance().addCallListInfo(info);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    cursor.close();
                }

                mListData.clear();
                mListData.addAll(FilterApplication.getInstance().getCallListInfo());
                mCallAdapter.notifyDataSetChanged();

                int count = mCallAdapter.getCount();
                if (count == 0) {
                    mCalllogListView.setVisibility(View.GONE);
                    mNoCalllogView.setVisibility(View.VISIBLE);
                } else {
                    mCalllogListView.setVisibility(View.VISIBLE);
                    mNoCalllogView.setVisibility(View.GONE);
                }
                FilterApplication.getInstance().setCallInfestFlag(false);
            }
        }
	}

	/**
	 * 显示通知栏点击跳转到指定Activity
	 */
	public void showFilterActivityNotify(CallInfo info) {
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		mBuilderCall = new NotificationCompat.Builder(mContext);
		mBuilderCall.setAutoCancel(true)//点击后让通知将消失
				.setContentTitle("您有一条新电话拦截")
				.setContentText("点击查看")
				.setTicker("拦截来电"+info.getPhoneNumber())
				.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
				.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
				.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
						//Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
				.setSmallIcon(R.drawable.forbid_little);
		Utils.log("showIntentActivityNotify");
		//点击的意图ACTION是跳转到Intent
		/** 用intent启动骚扰详情 */
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable(InfestFilterConstant.CALL_DATA, info);
		intent.putExtras(bundle);
		intent.setClass(mContext, InfestContentCallActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        int notifyId = Integer.valueOf(formatter.format(curDate)).intValue();
        Utils.log("notifyId="+notifyId);
		mBuilderCall.setContentIntent(PendingIntent.getActivity(mContext, notifyId, intent, 0));//requestCode不同的话PendingIntent也会不同，如Code相同
		//则会后面的PendingIntent代替前面的Intent
		NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(notifyId, mBuilderCall.build());
	}

	/**
	 * @获取默认的pendingIntent,为了防止2.3及以下版本报错
	 * @flags属性:
	 * 在顶部常驻:Notification.FLAG_ONGOING_EVENT
	 * 点击去除： Notification.FLAG_AUTO_CANCEL
	 */
	public PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(mContext, 1, new Intent(), flags);
		return pendingIntent;
	}
}
