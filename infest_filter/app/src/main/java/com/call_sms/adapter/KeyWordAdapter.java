package com.call_sms.adapter;
/**
 * 版权所有
 *
 * 文件名称：AppMoreAdapter.java
 * 文件标识：适配器类
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


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.call_sms.core.BlackListInfo;
import com.call_sms.core.KeywordInfo;
import com.call_sms.infest_filter.R;

import java.util.List;

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
        mListData = listData;
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
    public KeywordInfo getItem(int position)
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

        KeywordInfo info = mListData.get(position);
        viewHolder.content.setText(info.getContent());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListData.remove(position);

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


