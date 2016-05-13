package com.call_sms.infest_filter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.call_sms.settings.CallBackFilterSettingsActivity;
import com.call_sms.settings.KeywordFilterSettingsActivity;
import com.kyleduo.switchbutton.SwitchButton;

public class FilterSettingsActivity extends Activity implements View.OnClickListener{

//	private TextView subtitleCallBack;
	private TextView subtitleKeyword;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_settings);
		Button back = (Button)findViewById(R.id.id_back);
		back.setOnClickListener(this);
//		RelativeLayout callBackFilterSettings = (RelativeLayout)findViewById(R.id.id_call_filter_back_settings);
//		callBackFilterSettings.setOnClickListener(this);
		RelativeLayout keywordFilterSettings = (RelativeLayout)findViewById(R.id.id_keyword_filter_settings);
		keywordFilterSettings.setOnClickListener(this);

		SwitchButton blackList = (SwitchButton)findViewById(R.id.id_settings_blacklist);
		blackList.setChecked(FilterApplication.getInstance().getSettingPreference().getPreferenceBlackListInfest());
		blackList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				FilterApplication.getInstance().getSettingPreference().setPreferenceBlackListInfest(isChecked);
			}
		});

		SwitchButton strangeSms = (SwitchButton)findViewById(R.id.id_settings_strangesms);
		strangeSms.setChecked(FilterApplication.getInstance().getSettingPreference().getPreferenceStrangeSmsInfest());
		strangeSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				FilterApplication.getInstance().getSettingPreference().setPreferenceStrangeSmsInfest(isChecked);
			}
		});

		SwitchButton strangeCall = (SwitchButton)findViewById(R.id.id_settings_strangecall);
		strangeCall.setChecked(FilterApplication.getInstance().getSettingPreference().getPreferenceStrangeCallInfest());
		strangeCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				FilterApplication.getInstance().getSettingPreference().setPreferenceStrangeCallInfest(isChecked);
			}
		});

		SwitchButton notificationMark = (SwitchButton)findViewById(R.id.id_settings_notification);
		notificationMark.setChecked(FilterApplication.getInstance().getSettingPreference().getPreferenceNotification());
		notificationMark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				FilterApplication.getInstance().getSettingPreference().setPreferenceNotification(isChecked);
			}
		});

//		subtitleCallBack = (TextView)findViewById(R.id.id_settings_sub_callback);
		subtitleKeyword = (TextView)findViewById(R.id.id_settings_sub_keyword);
	}

	@Override
	public void onResume() 
	{
		super.onResume();
//		switch(FilterApplication.getInstance().getSettingPreference().getPreferenceCallBack()){
//			case 1:
//				subtitleCallBack.setText(R.string.string_settings_preference_subtitle_mang);
//				break;
//			case 2:
//				subtitleCallBack.setText(R.string.string_settings_preference_subtitle_poweroff);
//				break;
//			case 3:
//				subtitleCallBack.setText(R.string.string_settings_preference_subtitle_ting);
//				break;
//			case 4:
//				subtitleCallBack.setText(R.string.string_settings_preference_subtitle_null);
//				break;
//		}

		if(FilterApplication.getInstance().getSettingPreference().getPreferenceKeyword()){
			subtitleKeyword.setText(R.string.string_settings_preference_subtitle_open);
		}else{
			subtitleKeyword.setText(R.string.string_settings_preference_subtitle_close);
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
		case R.id.id_back:
			this.finish();
			break;
		case R.id.id_keyword_filter_settings:
			Intent intentCall = new Intent(Intent.ACTION_VIEW);
			intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentCall.setClass(this, KeywordFilterSettingsActivity.class);
			this.startActivity(intentCall);
			break;
//		case R.id.id_call_filter_back_settings:
//			Intent intentCallBack = new Intent(Intent.ACTION_VIEW);
//			intentCallBack.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intentCallBack.setClass(this, CallBackFilterSettingsActivity.class);
//			this.startActivity(intentCallBack);
//			break;

		}
	}
}
