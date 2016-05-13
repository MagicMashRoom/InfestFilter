package com.call_sms.infest_filter;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.call_sms.adapter.CallAdapter;
import com.call_sms.core.InfestFilterConstant;
import com.call_sms.core.InitializeDataTask;
import com.call_sms.fragment.BlackListFragment;
import com.call_sms.fragment.CallFragment;
import com.call_sms.fragment.SmsFragment;

import java.util.ArrayList;

public class FilterMainActivity extends FragmentActivity implements View.OnClickListener{

	private Context mContext = null;
	private ArrayList<Fragment> mTabFragmentsList = null;
	public static ViewPager mViewPager = null;
	private TabAdapter mTabAdapter = null;
	private static final int FRAGMENT_TAB_SMS = 0;
	private static final int FRAGMENT_TAB_CALL = 1;
	private static final int FRAGMENT_TAB_BLACKLIST = 2;
	private int mTotalTab = 3;

	private LinearLayout mLinearLayoutContent = null;
	private LinearLayout mLinearLayoutInit = null;

	private CallFragment mCallFragment = null;
	private SmsFragment mSmsFragment = null;
	private BlackListFragment mBlackListFragment = null;
	
	private TextView SmsTextView = null;
	private TextView CallTextView = null;
	private TextView BlackTextView = null;
	
	private int mCurrentIndex = 0;
	
	private int tab_position_one;
	private int tab_position_two;
	private int offset;
	
	private ImageView tabCursor;

	private FilterMainBroadcastReceiver mFilterMainBroadcastReceiver = null;
	private InitializeDataTask mInitializeDataTask = null;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_main);
		mContext = getApplicationContext();
		FilterApplication.getInstance().push(this);

		mLinearLayoutContent = (LinearLayout)findViewById(R.id.id_infest_main_content);
		mLinearLayoutInit = (LinearLayout)findViewById(R.id.id_infest_main_init);

		SmsTextView = (TextView) findViewById(R.id.tab_sms);
		SmsTextView.setOnClickListener(this);
		CallTextView = (TextView) findViewById(R.id.tab_call);
		CallTextView.setOnClickListener(this);
		BlackTextView = (TextView) findViewById(R.id.tab_black);
		BlackTextView.setOnClickListener(this);

		mViewPager = (ViewPager) findViewById(R.id.id_desktop_view_pager);
		InitImageView();
		initializeViewPager();

		if(FilterApplication.getInstance().getInitFlag()){
		Utils.log("getInitFlag");
		mLinearLayoutInit.setVisibility(View.GONE);
		mLinearLayoutContent.setVisibility(View.VISIBLE);
		}else{
			//dothing
		}
	}

	@SuppressWarnings("deprecation")
	private void initializeViewPager() {
		mTabFragmentsList = new ArrayList<Fragment>();

			mSmsFragment = new SmsFragment();
			mTabFragmentsList.add(mSmsFragment);

			mCallFragment = new CallFragment();
			mTabFragmentsList.add(mCallFragment);

			mBlackListFragment = new BlackListFragment();
			mTabFragmentsList.add(mBlackListFragment);

		mTabAdapter = new TabAdapter(getSupportFragmentManager(), mTabFragmentsList);
	    mViewPager.setAdapter(mTabAdapter);
	    mViewPager.setCurrentItem(mCurrentIndex);
        mViewPager.setOnPageChangeListener(new TabOnPageChangeListener());
        mViewPager.setOffscreenPageLimit(mTotalTab);
	}


	private void changeViewPagerItem(int item)
	{
		Animation animation = null ;
		if(item == mCurrentIndex){
			animation = new TranslateAnimation(0, 0, 0, 0);
		}
        switch (item){
            case 0:
                if(mCurrentIndex == 1){
                    animation = new TranslateAnimation(tab_position_one, 0, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                }else if(mCurrentIndex == 2){
                    animation = new TranslateAnimation(tab_position_two, 0, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                }
                break;

            case 1:
                if (mCurrentIndex == 0) {
                    animation = new TranslateAnimation(offset, tab_position_one, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                } else if (mCurrentIndex == 2) {
                    animation = new TranslateAnimation(tab_position_two, tab_position_one, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                }
                break;

            case 2:
                if (mCurrentIndex == 0) {
                    animation = new TranslateAnimation(offset, tab_position_two, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                } else if (mCurrentIndex == 1) {
                    animation = new TranslateAnimation(tab_position_one, tab_position_two, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                }
                break;
        }
        mCurrentIndex = item;
        animation.setFillAfter(true);
        animation.setDuration(300);
        tabCursor.startAnimation(animation);
	}

	private void changeViewPagerItemOnClick(int item)
	{
		Animation animation = null ;
		if(item == mCurrentIndex){
			animation = new TranslateAnimation(0, 0, 0, 0);
		}
        switch (item){
            case 0:
                if(mCurrentIndex == 1){
                    animation = new TranslateAnimation(tab_position_one, 0, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                }else if(mCurrentIndex == 2){
                    animation = new TranslateAnimation(tab_position_two, 0, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                }
                break;

            case 1:
                if (mCurrentIndex == 0) {
                    animation = new TranslateAnimation(offset, tab_position_one, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                } else if (mCurrentIndex == 2) {
                    animation = new TranslateAnimation(tab_position_two, tab_position_one, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                }
                break;

            case 2:
                if (mCurrentIndex == 0) {
                    animation = new TranslateAnimation(offset, tab_position_two, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                } else if (mCurrentIndex == 1) {
                    animation = new TranslateAnimation(tab_position_one, tab_position_two, 0, 0);
                    resetTabtextCollor();
                    setTabTextColor(item);
                }
                break;
        }
        mCurrentIndex = item;
        animation.setFillAfter(true);
        animation.setDuration(300);
        tabCursor.startAnimation(animation);
	}
	@SuppressWarnings("deprecation")
	private void setTabTextColor(int item) {
		// TODO Auto-generated method stub
		switch(item){
		case 0:
			SmsTextView.setTextColor(getResources().getColor(R.color.color_tabtext_color));
			break;
		case 1:
			CallTextView.setTextColor(getResources().getColor(R.color.color_tabtext_color));
			break;
		case 2:
			BlackTextView.setTextColor(getResources().getColor(R.color.color_tabtext_color));
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void resetTabtextCollor() {
		// TODO Auto-generated method stub
		SmsTextView.setTextColor(getResources().getColor(R.color.color_tabtext_color_grey));
		CallTextView.setTextColor(getResources().getColor(R.color.color_tabtext_color_grey));
		BlackTextView.setTextColor(getResources().getColor(R.color.color_tabtext_color_grey));
	}

	public class TabOnPageChangeListener implements OnPageChangeListener
	{
		@Override
        public void onPageSelected(int arg0)
		{
			changeViewPagerItem(arg0);
		}

		@Override
	    public void onPageScrolled(int arg0, float arg1, int arg2)
		{
		}

	    @Override
	    public void onPageScrollStateChanged(int arg0)
	    {
	    }
	}

	/**
     * ��ʼ������
     */
    private void InitImageView() {
        tabCursor = (ImageView) findViewById(R.id.tab_selected);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenW = dm.widthPixels;
        int bmpW;
        bmpW = (screenW/3);
        offset = 0;
        tab_position_one = (int) (screenW / 3.0);
        tab_position_two = tab_position_one * 2;

    }

	@Override
	public void onResume() 
	{
		super.onResume();

		if(mFilterMainBroadcastReceiver == null) /** 注册广播 */
		{
			mFilterMainBroadcastReceiver = new FilterMainBroadcastReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(InfestFilterConstant.INFESTFILTER_BROADCAST_ACTION);
			registerReceiver(mFilterMainBroadcastReceiver, filter);
		}
		if(!FilterApplication.getInstance().getInitFlag()) {
			Utils.log("InitializeDataTask");
			mInitializeDataTask = new InitializeDataTask(getApplicationContext());
			mInitializeDataTask.execute();
		}
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		if(mFilterMainBroadcastReceiver != null) /** 注销广播 */
		{
			unregisterReceiver(mFilterMainBroadcastReceiver);
			mFilterMainBroadcastReceiver = null;
		}
	}

	@Override
	public void onDestroy() 
	{		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.tab_sms:
			mViewPager.setCurrentItem(FRAGMENT_TAB_SMS);
			break;
		case R.id.tab_call:
			mViewPager.setCurrentItem(FRAGMENT_TAB_CALL);
			break;
		case R.id.tab_black:
			mViewPager.setCurrentItem(FRAGMENT_TAB_BLACKLIST);
			break;
		}
	}

	/** 自定义一个广播接收器 */
	private class FilterMainBroadcastReceiver extends BroadcastReceiver
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
			Utils.log("onReceive");
			/**广播消息处理  */
			if(intent.getAction().equalsIgnoreCase(InfestFilterConstant.INFESTFILTER_BROADCAST_ACTION))
			{
				mLinearLayoutInit.setVisibility(View.GONE);
				mLinearLayoutContent.setVisibility(View.VISIBLE);
			}
		}

		/**
		 * 函数名称：FilterMainBroadcastReceiver
		 * 功能描述：构造函数
		 * 输入参数：无
		 * 输出参数：无
		 * 返  回   值：无
		 * 备          注：构造函数，做一些初始化工作
		 */
		public FilterMainBroadcastReceiver()
		{
		}
	}

}
