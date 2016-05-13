package com.call_sms.settings;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.call_sms.adapter.KeyWordAdapter;
import com.call_sms.core.KeywordInfo;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.R.id;
import com.call_sms.infest_filter.R.layout;
import com.call_sms.infest_filter.Utils;
import com.call_sms.ui.AddKeywordActivity;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class KeywordFilterSettingsActivity extends Activity implements View.OnClickListener{

	private List<KeywordInfo> mListData = null;
	private List<KeywordInfo> mListDataAdapter = null;//adapter中的数据
	private KeyWordAdapter mKeywordAdapter = null;
	private ListView mListView = null;
	private TextView mNoKeywordList = null;
	private TextView mCloseKeyword = null;
	private LinearLayout mOpenKeywordFilter = null;
	private Button mAddKeywordButton = null;

	private Context mContext = null;
	private Boolean openKeywordFilter = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.keyword_activity_filter_settings);
		Button back = (Button)findViewById(id.id_keyword_back);
		back.setOnClickListener(this);

		mContext = getApplicationContext();

		mListData = new ArrayList<KeywordInfo>();
		mKeywordAdapter = new KeyWordAdapter(mContext,mListData);
		mListView = (ListView)findViewById(R.id.id_keyword_list_view);
		mListView.setAdapter(mKeywordAdapter);

		mNoKeywordList = (TextView)findViewById(id.id_keyword_have_nokeyword);
		mCloseKeyword = (TextView)findViewById(id.id_keyword_have_closekeyword);
		mOpenKeywordFilter = (LinearLayout)findViewById(id.id_keyword_open);

		mAddKeywordButton = (Button)findViewById(id.id_add_keyword_button);
		mAddKeywordButton.setOnClickListener(this);
		mAddKeywordButton.setClickable(false);
		SwitchButton keywordInfest = (SwitchButton)findViewById(R.id.id_settings_keyword);
		keywordInfest.setChecked(FilterApplication.getInstance().getSettingPreference().getPreferenceKeyword());
		if(FilterApplication.getInstance().getSettingPreference().getPreferenceKeyword()){
			mCloseKeyword.setVisibility(View.GONE);
			mAddKeywordButton.setClickable(true);
			mOpenKeywordFilter.setVisibility(View.VISIBLE);
		}
		keywordInfest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				FilterApplication.getInstance().getSettingPreference().setPreferenceKeyword(isChecked);
				if (isChecked) {
					Utils.log("isChecked=" + isChecked);
					openKeywordFilter = true;
					mCloseKeyword.setVisibility(View.GONE);
					mAddKeywordButton.setClickable(true);
					mOpenKeywordFilter.setVisibility(View.VISIBLE);
				} else {
					openKeywordFilter = false;
					mCloseKeyword.setVisibility(View.VISIBLE);
					mAddKeywordButton.setClickable(false);
					mOpenKeywordFilter.setVisibility(View.GONE);
				}
			}
		});



	}

	@Override
	public void onResume() 
	{
		super.onResume();
		Utils.log("onResume");
		mListData.clear();

		List<KeywordInfo> temp = FilterApplication.getInstance().getKeywordListInfo();
		Iterator<KeywordInfo> infoIterator = temp.iterator();
		while(infoIterator.hasNext()){
			mListData.add(infoIterator.next());
		}

		mKeywordAdapter.notifyDataSetChanged();
		int count = mKeywordAdapter.getCount();
		Utils.log("count="+count);
		if(count == 0){
			mListView.setVisibility(View.GONE);
			mNoKeywordList.setVisibility(View.VISIBLE);
		}else{
			mListView.setVisibility(View.VISIBLE);
			mNoKeywordList.setVisibility(View.GONE);
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
		case id.id_keyword_back:
			this.finish();
			break;
		case id.id_add_keyword_button:
			Intent i = new Intent();
			i.setClass(this, AddKeywordActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			break;
		}
	}

	public class KeyWordAdapter extends BaseAdapter
	{
		private Context mContext = null;
		private List<KeywordInfo> mListData = null;
		private LayoutInflater mLayoutInflater = null;

		/** 数据类 */
		class ViewHolder
		{
			TextView content;
			Button delete;
		}

		/**
		 * 函数名称：SmsAdapter
		 * 功能描述：构造函数
		 * 输入参数：@param context  上下文
		 *          @param c 当前游标
		 * 输出参数：无
		 * 返  回   值：无
		 * 备          注：函数第一次回调用后，如果数据更新也会再调用，但重绘会再次调用的，总
		 *          的来说应该是在调用bindView如果发现view为空会先调用newView来生成view。
		 */
		public KeyWordAdapter(Context context, List<KeywordInfo> listData)
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
		public KeywordInfo getItem(int position)
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
				convertView = mLayoutInflater.inflate(R.layout.keyword_list_item, (ViewGroup)null);

				viewHolder.content = (TextView) convertView.findViewById(R.id.id_keyword_list_item_name);
				viewHolder.delete = (Button) convertView.findViewById(R.id.keyword_delete_button);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder)convertView.getTag();
				resetViewHolder(viewHolder);
			}

			KeywordInfo info = mListDataAdapter.get(position);
			viewHolder.content.setText(info.getContent());

			viewHolder.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mListDataAdapter.remove(position);
					FilterApplication.getInstance().removeKeywordListInfo(position);
					mKeywordAdapter.notifyDataSetChanged();
					int count = mKeywordAdapter.getCount();
					Utils.log("count=" + count);
					if(count == 0){
						mListView.setVisibility(View.GONE);
						mNoKeywordList.setVisibility(View.VISIBLE);
					}else{
						mListView.setVisibility(View.VISIBLE);
						mNoKeywordList.setVisibility(View.GONE);
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
			viewHolder.content.setText("");

		}

	}
}
