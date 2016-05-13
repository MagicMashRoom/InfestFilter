package com.call_sms.core;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;
import com.call_sms.infest_filter.Utils;

public class InfestContentSmsActivity extends Activity implements View.OnClickListener {

    private SmsInfo mSmsInfo = null;
    private Context mContext = null;
    private int position = -1;
    private Button add = null;
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

        add = (Button) findViewById(R.id.infest_detail_add);
        add.setOnClickListener(this);
        Button delete = (Button) findViewById(R.id.infest_detail_delete);
        delete.setOnClickListener(this);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(this);
        Intent intent = this.getIntent();
        mSmsInfo = (SmsInfo) intent.getSerializableExtra(InfestFilterConstant.SMS_DATA);
        position = (int)intent.getSerializableExtra("position");
        Utils.log("position="+position);
        if (mSmsInfo != null) {//由短信列表传递过来的数据
            name.setText(mSmsInfo.getPhoneName());
            number.setText(mSmsInfo.getPhoneNumber());
            time.setText(mSmsInfo.getTime());
            content.setText(mSmsInfo.getContent());
            fabButton.setVisibility(View.GONE);
            add.setText("从拦截列表中恢复");
            delete.setText("删除该短信");
        }
        if(position == -10){
            add.setVisibility(View.GONE);
        }
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
            case R.id.infest_detail_delete:
                entryDeleteSms();
                break;
            case R.id.infest_detail_add:
                entryBackSms();
                break;
        }
    }
    /*
     * Delete all SMS one by one
     */
    public void entryBackSms() {
        if(position > -1){
            FilterApplication.getInstance().removeSmsListInfo(position);
            FilterApplication.getInstance().sendSmsBroadcast();
            Toast.makeText(mContext, "恢复成功", Toast.LENGTH_SHORT).show();
            add.setClickable(false);
        }
    }

    /*
 * Delete all SMS one by one
 */
    public void entryDeleteSms() {
        String threadId = "";

        try {
            ContentResolver mContentResolver = getContentResolver();
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = mContentResolver.query(uriSms,
                    new String[]{"_id","thread_id"}, null, null, null);
            Utils.log("id="+mSmsInfo.getSmsId());
            while(c.moveToNext())
            {
                String id = c.getString(c.getColumnIndex("_id"));
                if(id.equals(mSmsInfo.getSmsId())){
                    threadId = c.getString(c.getColumnIndex("thread_id"));
                    Toast.makeText(mContext, "长按选择你要删除的信息", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if(!threadId.equals("")){
                startActivity(createIntent(mContext,threadId));
            }
            //否则的话数据更新错误
        } catch (Exception e) {
            Log.d("deleteSMS", "Exception:: " + e);
        }
    }

    public Intent createIntent(Context context, String threadId) {
        long t = Long.valueOf(threadId).longValue();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.android.mms","com.android.mms.ui.ComposeMessageActivity");
        if (t > 0) {
            intent.setData(getUri(t));
        }
        return intent;
    }

    /**
     * Return the Uri for all messages in the given thread ID.
     * @deprecated
     */
    public Uri getUri(long threadId) {
        // and call getUri() on it, but this guarantees no blocking.
        return ContentUris.withAppendedId(Telephony.Threads.CONTENT_URI, threadId);
    }
}
