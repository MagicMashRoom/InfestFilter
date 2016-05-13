package com.call_sms.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.call_sms.core.BlackListInfo;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;

/**
 * Created by konka on 2016-3-16.
 */
public class AddByHandInfestActivity extends Activity implements View.OnClickListener{

    private EditText name = null;
    private EditText number = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_by_hand_view);
        Button back = (Button)findViewById(R.id.id_byhand_back);
        back.setOnClickListener(this);
        Button ok = (Button)findViewById(R.id.add_by_hand_ok);
        ok.setOnClickListener(this);
        number = (EditText)findViewById(R.id.editTextNumber);
        number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        name = (EditText)findViewById(R.id.editTextName);
        name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
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
            case R.id.id_byhand_back:
                this.finish();
                break;
            case R.id.add_by_hand_ok:

                if (number.getText().toString().replaceAll("[0-9]", "").length()>0){
                    //提示
                    Toast.makeText(this.getBaseContext(),"你家电话号码有字母和符号？",Toast.LENGTH_SHORT).show();
                }else if(number.getText().toString().length()<3){
                    //提示
                    Toast.makeText(this.getBaseContext(),"号码也忒短了吧",Toast.LENGTH_SHORT).show();
                }else if(name.getText().toString().length()<1){
                    //提示
                    Toast.makeText(getApplicationContext(),"您没有输入名称哟",Toast.LENGTH_SHORT).show();
                }else{
                    BlackListInfo info = new BlackListInfo();
                    info.setPhoneName(name.getText().toString());
                    info.setPhoneNumber(number.getText().toString());
                    FilterApplication.getInstance().addBlackListInfo(info);
                    info = null;
                    Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                break;
        }
    }
}
