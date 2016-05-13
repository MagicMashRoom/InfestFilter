package com.call_sms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.call_sms.core.BlackListInfo;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;
import com.call_sms.settings.BlackListPopWindow;
import com.call_sms.ui.AddByCalllogActivity;
import com.call_sms.ui.AddByContactsActivity;
import com.call_sms.ui.AddByHandInfestActivity;
import com.call_sms.ui.AddBySmsActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BlackListFragment extends Fragment implements OnClickListener{

	private BlackListPopWindow mPopupWindow = null;
	private List<BlackListInfo> mListData = null;
    private List<BlackListInfo> mListDataAdapter = null;//adapter中的黑名单数据
	private RelativeLayout mNoBlackList = null;
	private ListView mListView = null;
	private RelativeLayout mInitView = null;
	private BlackListAdapter mAdapter = null;
	private Context mContext = null;

	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
         Utils.log("onCreate()");
		 super.onCreate(savedInstanceState);
		 mContext = getContext();
		 mListData = new ArrayList<BlackListInfo>();
		 mAdapter = new BlackListAdapter(mContext,mListData);
	 }

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	 {
		 View contentView = inflater.inflate(R.layout.blacklist_layout, container, false);
		 ImageView blackListAdd = (ImageView)contentView.findViewById(R.id.blacklist_add);
		 blackListAdd.setOnClickListener(this);
		 mNoBlackList = (RelativeLayout)contentView.findViewById(R.id.id_black_no_black);
		 mInitView = (RelativeLayout)contentView.findViewById(R.id.id_black_init_black);
		 mListView = (ListView)contentView.findViewById(R.id.id_black_list_view);
		 mListView.setAdapter(mAdapter);
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
		 mListData.clear();
		 if(FilterApplication.getInstance().getInitFlag()){
			 refreshUI();
		 }
	 }

	 @Override
	 public void onPause() 
	 {
		 super.onPause();
	 }

		
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.blacklist_add:
			mPopupWindow = new BlackListPopWindow(getActivity(), itemsInPopUpWindowOnClick);
			mPopupWindow.showAtLocation(getView().findViewById(R.id.id_blackList_button),Gravity.CENTER, 0,208);
			break;	
		}
	}

	/**
	 * 函数名称：itemsInPopUpWindowOnClick
	 * 功能描述：为弹出窗口实现监听类
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	private OnClickListener  itemsInPopUpWindowOnClick = new OnClickListener()
	{
		public void onClick(View v)
		{
			mPopupWindow.dismiss();
			switch (v.getId())
			{
				case R.id.id_blacklist_popupwindow_call:
					entryAddByCalllog();
					break;
				case R.id.id_blacklist_popupwindow_sms:
					entryAddBySms();
					break;
				case R.id.id_blacklist_popupwindow_contacts:
					entryAddByContacts();
					break;
				case R.id.id_blacklist_popupwindow_hand:
					entryAddByHand();
					break;
				case R.id.id_blacklist_popupwidow_cancel:

					break;
				default:
					break;
			}
		}
	};

	/**
	 * 函数名称：entryAddByCalllog
	 * 功能描述：进入通话记录添加Activity
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	private void entryAddByCalllog() {
		Intent i = new Intent();
		i.setClass(getContext(), AddByCalllogActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	/**
	 * 函数名称：entryAddBySms
	 * 功能描述：进入短信添加Activity
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	private void entryAddBySms() {
		Intent i = new Intent();
		i.setClass(getContext(), AddBySmsActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	/**
	 * 函数名称：entryAddByContacts
	 * 功能描述：进入联系人添加Activity
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	private void entryAddByContacts() {
		Intent i = new Intent();
		i.setClass(getContext(), AddByContactsActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	/**
	 * 函数名称：entryAddByHand
	 * 功能描述：进入手动添加Activity
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	private void entryAddByHand() {
		Intent i = new Intent();
		i.setClass(getContext(), AddByHandInfestActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	public void refreshUI(){

		mInitView.setVisibility(View.GONE);
		List<BlackListInfo> temp = FilterApplication.getInstance().getBlackList();
		Iterator<BlackListInfo> infoIterator = temp.iterator();
		while(infoIterator.hasNext()){
			mListData.add(infoIterator.next());
		}
		mAdapter.notifyDataSetChanged();
		int count = mAdapter.getCount();
		if(count == 0){
			mListView.setVisibility(View.GONE);
			mNoBlackList.setVisibility(View.VISIBLE);
		}else{
			mListView.setVisibility(View.VISIBLE);
			mNoBlackList.setVisibility(View.GONE);
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
		public Button delete;
	}

	/**
	 * 函数名称：BlackListAdapter
	 * 功能描述：自定义黑名单adapter
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	public class BlackListAdapter extends BaseAdapter {

		private LayoutInflater mLayoutInflater;

        public BlackListAdapter(Context context, List<BlackListInfo> listData)
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
				convertView = mLayoutInflater.inflate(R.layout.black_list_view, (ViewGroup)null);

				viewHolder.name = (TextView) convertView.findViewById(R.id.id_black_list_item_name);
				viewHolder.number = (TextView) convertView.findViewById(R.id.id_black_list_item_number);
				viewHolder.delete = (Button) convertView.findViewById(R.id.id_black_list_item_delete);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder)convertView.getTag();
				resetViewHolder(viewHolder);
			}

			BlackListInfo info = mListDataAdapter.get(position);
			viewHolder.name.setText(info.getPhoneName());
			viewHolder.number.setText(info.getPhoneNumber());

			viewHolder.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    mListDataAdapter.remove(position);
					FilterApplication.getInstance().removeBlackList(position);
					mAdapter.notifyDataSetChanged();
					int count = mAdapter.getCount();
					if(count == 0){
						mListView.setVisibility(View.GONE);
						mNoBlackList.setVisibility(View.VISIBLE);
					}else{
						mListView.setVisibility(View.VISIBLE);
						mNoBlackList.setVisibility(View.GONE);
					}
				}
			});
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
