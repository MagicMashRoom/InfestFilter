/**
 * ��Ȩ����     							 
 *																		 
 * �ļ����ƣ�ContactsAdapter.java													 
 * �ļ���ʶ����������
 * ����ժҪ�� 
 * ����˵���� 
 * ��ǰ�汾��1.0													
 * ��	  �ߣ�
 * ������ڣ�
 * �޸ļ�¼��
 * �޸����ڣ�															 
 * ��   ��  �ţ�															 
 * ��   ��  �ˣ�															 
 * �޸����ݣ�
 * ��          ע�� 
 */

package com.call_sms.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.call_sms.core.CallInfo;
import com.call_sms.core.InfestFilterConstant;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;

import java.util.List;

public class CallAdapter extends BaseAdapter
{
	private Context mContext = null;
	private List<CallInfo> mListData = null;
	private LayoutInflater mLayoutInflater = null;

	/** 数据类 */
	class ViewHolder
	{
		ImageView iconView;
		TextView nameView;
		TextView dateView;
		TextView numberView;
		TextView reasonView;
	}
	/**
	 * 函数名称：AppDeleteAdapter
	 * 功能描述：构造函数
	 * 输入参数：@param context  上下文
	 *          @param listData 数据
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：函数第一次回调用后，如果数据更新也会再调用，但重绘会再次调用的，总
	 *          的来说应该是在调用bindView如果发现view为空会先调用newView来生成view。
	 */

	public CallAdapter(Context context, List<CallInfo> listData)
	{
		mContext = context;
		mListData = listData;
        Utils.log("mListData.size="+mListData.size());
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
		return mListData.size();
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
	public CallInfo getItem(int position)
	{
		return mListData.get(position);
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
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			/** 在程序中动态加载每行的布局   	 */
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.call_list_view, (ViewGroup)null);
			viewHolder.iconView =  (ImageView)convertView.findViewById(R.id.id_call_button_flag);
			viewHolder.nameView =  (TextView)convertView.findViewById(R.id.id_call_list_item_name);//联系人姓名
			viewHolder.dateView =  (TextView)convertView.findViewById(R.id.id_call_list_item_time);//通话记录时间

			viewHolder.numberView = (TextView)convertView.findViewById(R.id.id_call_list_item_number);//通话记录电话号码
			viewHolder.reasonView = (TextView)convertView.findViewById(R.id.id_call_list_item_reason);//拦截原因
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder)convertView.getTag();
			resetViewHolder(viewHolder);
		}

		CallInfo info = mListData.get(position);
        Utils.log("info.nameinit=" + info.getPhoneName());
		if(info.getCallId().equals("")){
			//空数据
            Utils.log("wokao");
		}else {
			switch (info.getReason()) {
				case InfestFilterConstant.BLACKLIST_CALL:
					viewHolder.iconView.setBackgroundResource(R.drawable.forbid);
                    viewHolder.reasonView.setText("黑名单");
					break;
				case InfestFilterConstant.STRANGE_CALL:
					viewHolder.iconView.setBackgroundResource(R.drawable.help);
                    viewHolder.reasonView.setText("陌生人");
					break;
                default:
                    viewHolder.iconView.setBackgroundResource(R.drawable.flag);
                    viewHolder.reasonView.setText("疑似骚扰");
			}
            viewHolder.nameView.setText(info.getPhoneName());
            Utils.log("info.name=" + info.getPhoneName());
			viewHolder.dateView.setText(info.getTime());
			viewHolder.numberView.setText(info.getPhoneNumber());
            Utils.log("info.number=" + info.getPhoneNumber());

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
		viewHolder.dateView.setText("");
		viewHolder.nameView.setText("");
		viewHolder.numberView.setText("");
		viewHolder.reasonView.setText("");

	}
}
