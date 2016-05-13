package com.call_sms.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.call_sms.adapter.SmsAdapter;
import com.call_sms.core.InfestContentCallActivity;
import com.call_sms.core.InfestContentSmsActivity;
import com.call_sms.core.InfestFilterConstant;
import com.call_sms.core.SmsInfo;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.FilterSettingsActivity;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;

import java.util.ArrayList;
import java.util.List;
public class SmsFragment extends Fragment implements View.OnClickListener{

	private List<SmsInfo> mListData = null;
	private SmsAdapter mSmsAdapter = null;
	private ListView mListView = null;
	private RelativeLayout mNoSmsList = null;
	private SmsBroadcastReceiver mSmsBroadcastReceiver;
	private Context mContext = null;
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		 super.onCreate(savedInstanceState);

		 mContext = getContext();
		 mListData = new ArrayList<SmsInfo>();
         mListData.addAll(FilterApplication.getInstance().getSmsListInfo());
		 mSmsAdapter = new SmsAdapter(getContext(),mListData);
	 }

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	 {
		 View contentView = inflater.inflate(R.layout.sms_layout, container, false); 
		 ImageView settings = (ImageView)contentView.findViewById(R.id.id_sms_settings);
		 settings.setOnClickListener(this);
		 mListView = (ListView)contentView.findViewById(R.id.id_sms_list_view);
		 mListView.setAdapter(mSmsAdapter);
         mListView.setOnItemClickListener(new OnItemClickListener() {
			 @Override
			 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 /** 用intent启动骚扰详情 */
				 Intent intent = new Intent();
				 Bundle bundle = new Bundle();
				 bundle.putSerializable(InfestFilterConstant.SMS_DATA, mSmsAdapter.getItem(position));
                 bundle.putSerializable("position",position);
				 intent.putExtras(bundle);
				 intent.setClass(mContext, InfestContentSmsActivity.class);
				 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 startActivity(intent);

			 }
		 });
		 mNoSmsList = (RelativeLayout)contentView.findViewById(R.id.id_sms_no_sms);
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
         if(FilterApplication.getInstance().getInitFlag()){
             refreshUI();
         }
		 int count = mSmsAdapter.getCount();
		 if(count==0){
			 mListView.setVisibility(View.GONE);
             mNoSmsList.setVisibility(View.VISIBLE);
		 }else{
			 mListView.setVisibility(View.VISIBLE);
			 mNoSmsList.setVisibility(View.GONE);
		 }
		 if(mSmsBroadcastReceiver == null){
		 	IntentFilter intentFilter = new IntentFilter();
		 	intentFilter.addAction(InfestFilterConstant.SMSNEW_BROADCAST_ACTION);
		 	mSmsBroadcastReceiver = new SmsBroadcastReceiver();
		 	getActivity().registerReceiver(mSmsBroadcastReceiver, intentFilter);
		 }
	 }

	 @Override
	 public void onPause() 
	 {
		 super.onPause();
		 if(mSmsBroadcastReceiver!=null){
			 getActivity().unregisterReceiver(mSmsBroadcastReceiver);
			 mSmsBroadcastReceiver = null;
		 }
	 }
	 
	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.id_sms_settings:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(getActivity(), FilterSettingsActivity.class);
			getActivity().startActivity(intent);
			break;
		}
	}


	/** 自定义一个广播接收器 */
	private class SmsBroadcastReceiver extends BroadcastReceiver
	{
		/**
		 * 函数名称：onReceive
		 * 功能描述：接收信息
		 * 输入参数：无
		 * 输出参数：无
		 * 返  回   值：无
		 * 备          注：无
		 */
		@Override
		public void onReceive(Context context, Intent intent)
		{
			/**广播消息处理  */
			if(intent.getAction().equalsIgnoreCase(InfestFilterConstant.SMSNEW_BROADCAST_ACTION))
			{
				refreshUI();
			}
		}

		/**
		 * 函数名称：SmsBroadcastReceiver
		 * 功能描述：构造函数
		 * 输入参数：无
		 * 输出参数：无
		 * 返  回   值：无
		 * 备          注：构造函数，做一些初始化工作
		 */
		public SmsBroadcastReceiver()
		{
		}
	}

	private void refreshUI() {
		mListData.clear();
		mListData.addAll(FilterApplication.getInstance().getSmsListInfo());
		mSmsAdapter.notifyDataSetChanged();
		int count = mSmsAdapter.getCount();
		if(count!=0){
			mListView.setVisibility(View.VISIBLE);
			mNoSmsList.setVisibility(View.GONE);
		}
	}
}
