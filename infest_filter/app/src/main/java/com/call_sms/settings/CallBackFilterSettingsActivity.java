package com.call_sms.settings;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.R.id;
import com.call_sms.infest_filter.R.layout;
import com.call_sms.infest_filter.Utils;


public class CallBackFilterSettingsActivity extends Activity implements View.OnClickListener{

	private RadioButton radioButton1 = null;
	private RadioButton radioButton2 = null;
	private RadioButton radioButton3 = null;
	private RadioButton radioButton4 = null;

	//占线时转移，提示所拨的号码为空号
	private final String ENABLE_SERVICE = "tel:**67*13800000000%23";
	//占线时转移，提示所拨的号码为关机
	private final String ENABLE_POWEROFF_SERVICE = "tel:**67*13810538911%23";
	//占线时转移，提示所拨的号码为停机
	private final String ENABLE_STOP_SERVICE = "tel:**67*13701110216%23";

	String GSM_CALL_WAIT_OPEN = "tel:##67#";
	String GSM_CALL_WAIT_CLOSE = "tel:%23%2321%23";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.callback_activity_filter_settings);
		Button back = (Button)findViewById(id.id_callback_back);
		back.setOnClickListener(this);

		RadioGroup callBack = (RadioGroup)findViewById(R.id.id_settings_callback);

		radioButton1 = (RadioButton)findViewById(R.id.id_settings_callback1);
		radioButton2 = (RadioButton)findViewById(R.id.id_settings_callback2);
		radioButton3 = (RadioButton)findViewById(R.id.id_settings_callback3);
		radioButton4 = (RadioButton)findViewById(R.id.id_settings_callback4);
		switch(FilterApplication.getInstance().getSettingPreference().getPreferenceCallBack())
		{
			case 1:
				radioButton1.setChecked(true);
				break;
			case 2:
				radioButton2.setChecked(true);
				break;
			case 3:
				radioButton3.setChecked(true);
				break;
			case 4:
				radioButton4.setChecked(true);
				break;
		}

		callBack.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
					case R.id.id_settings_callback1:
						FilterApplication.getInstance().getSettingPreference().setPreferenceCallBack(1);
						break;
					case R.id.id_settings_callback2:
						FilterApplication.getInstance().getSettingPreference().setPreferenceCallBack(2);
						//启用关机提示
						Utils.log("iPowerOff");
						Intent iPowerOff = new Intent(Intent.ACTION_CALL);
						iPowerOff.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						iPowerOff.setData(Uri.parse(ENABLE_POWEROFF_SERVICE));
						startActivity(iPowerOff);
						break;
					case R.id.id_settings_callback3:
						FilterApplication.getInstance().getSettingPreference().setPreferenceCallBack(3);
						//启用停机提示
						Utils.log("iStop");
						Intent iStop = new Intent(Intent.ACTION_CALL);
						iStop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						iStop.setData(Uri.parse(ENABLE_STOP_SERVICE));
						startActivity(iStop);
						break;
					case R.id.id_settings_callback4:
						FilterApplication.getInstance().getSettingPreference().setPreferenceCallBack(4);
						//启用空号提示
						Utils.log("iEmpty");
						Intent iEmpty = new Intent(Intent.ACTION_CALL);
						iEmpty.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						iEmpty.setData(Uri.parse(ENABLE_SERVICE));
						startActivity(iEmpty);
						break;
				}
			}
		});
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
		case id.id_callback_back:
			this.finish();
			break;
		}
	}
}
