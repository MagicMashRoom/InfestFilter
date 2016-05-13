package com.call_sms.core;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.FilterMainActivity;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class InfestContentCallActivity extends Activity implements View.OnClickListener {

	private CallInfo mCallInfo = null;
	private boolean isSmsorCall = true;//默认值为true,意思是由短信列表传来的数据
	private String phoneNumber = "";

	private Handler mHandler = null;
	private Context mContext = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_main);
		mContext = getBaseContext();

		TextView name = (TextView) findViewById(R.id.id_infest_detail_name);
		TextView number = (TextView) findViewById(R.id.id_infest_detail_number);
		TextView time = (TextView) findViewById(R.id.id_infest_detail_time);
		TextView content = (TextView) findViewById(R.id.id_infest_detail_content);
		final TextView location = (TextView) findViewById(R.id.id_infest_detail_address);

		Button add = (Button) findViewById(R.id.infest_detail_add);
		add.setOnClickListener(this);
		Button delete = (Button) findViewById(R.id.infest_detail_delete);
		delete.setOnClickListener(this);

		FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
		fabButton.setOnClickListener(this);
		Intent intent = this.getIntent();

		mCallInfo = (CallInfo) intent.getSerializableExtra(InfestFilterConstant.CALL_DATA);
		if (mCallInfo != null) {//由电话列表传递过来的数据
			isSmsorCall = InfestFilterConstant.isCall;
			name.setText(mCallInfo.getPhoneName());
			number.setText(mCallInfo.getPhoneNumber());
			if (mCallInfo.getPhoneNumber().equals("")) {
				phoneNumber = mCallInfo.getPhoneName();
			} else {
				phoneNumber = mCallInfo.getPhoneNumber();
			}
			location.setText(mCallInfo.getLocation());
			time.setText(mCallInfo.getTime());
			Utils.log("Time="+mCallInfo.getTime());
		}

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				String val = data.getString("value");
				if (mCallInfo != null) {
					if(val.equals("")){
						location.setText(mCallInfo.getLocation());
					}else {
						location.setText(mCallInfo.getLocation() + val.substring(val.length() - 4, val.length()));
					}
				}else{
					location.setText(val);
				}
			}
		};

		new Thread() {
			@Override
			public void run() {
				try {
					String address = calcMobileCity(phoneNumber);
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("value",address);
					msg.setData(data);
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					Utils.log(""+e.toString());
				}
			}
		}.start();

		Button back = (Button) findViewById(R.id.id_back);
		back.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Utils.log("onDestroy");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.id_back:
				this.finish();
				break;
			case R.id.fab:
				entryDial();
				break;
			case R.id.infest_detail_delete:
                entrySaveContacts();
				break;
			case R.id.infest_detail_add:

				entryAddBlackList();
				break;
		}
	}

	/**
	 * 函数名称：entryDial
	 * 功能描述：执行打电话
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	private void entryDial() {
		if (phoneNumber != null && phoneNumber.length() > 0) {
			/** 用intent启动拨打电话 */
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
			Utils.log("entryCall");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	/**
	 * 函数名称：entrySaveContacts
	 * 功能描述：执行保存电话本
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	private void entrySaveContacts() {

		if (phoneNumber != null && phoneNumber.length() > 0) {
			Intent intent = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
			intent.setType("vnd.android.cursor.dir/person");
			intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber); /** 手机号码 */
			startActivity(intent);
		} else {
			Toast.makeText(mContext, "没有号码你存个什么劲", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 函数名称：entryAddBlacList
	 * 功能描述：执行添加到黑名单
	 * 输入参数：无
	 * 输出参数：无
	 * 返  回   值：无
	 * 备          注：无
	 */
	private void entryAddBlackList() {
		if (mCallInfo != null) {
			BlackListInfo info = new BlackListInfo();
			info.setPhoneName(mCallInfo.getPhoneName());
			if(mCallInfo.getPhoneNumber().equals("")) {
				info.setPhoneNumber(mCallInfo.getPhoneName());
			}else{
				info.setPhoneNumber(mCallInfo.getPhoneNumber());
			}
			FilterApplication.getInstance().addBlackListInfo(info);
			Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 测试手机号码是来自哪个城市的，利用财付通的API
	 *
	 * @param mobileNumber 手机号码
	 * @return
	 * @throws MalformedURLException
	 */
	public String calcMobileCity(String mobileNumber) throws MalformedURLException {
		String address = "";
		String urlString = "http://life.tenpay.com/cgi-bin/mobile/MobileQueryAttribution.cgi?chgmobile=" + mobileNumber;
		URL url = new URL(urlString);
		try {
			InputStream in = url.openStream();
			address = parseResponseXML(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}

	/**
	 * 解析返回的xml数据，并获得手机号归属地址
	 *
	 * @param inputStream
	 * @return
	 * @throws XmlPullParserException
	 */
	private static String parseResponseXML(InputStream inputStream) throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "gb2312");
		int eventType = parser.getEventType();  //产生第一个事件
		String address = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {  //只要不是文档结束事件
			switch (eventType) {
				case XmlPullParser.START_TAG:
					String name = parser.getName(); //获取解析器当前指向的元素的名称
					if ("city".equals(name)) {
						address = address + parser.nextText();
					}
					if ("province".equals(name)) {
						address = address + parser.nextText();
					}
					if ("supplier".equals(name)) {
						address = address + parser.nextText();
					}
					break;
			}
			eventType = parser.next();
		}
		return address;
	}

}
