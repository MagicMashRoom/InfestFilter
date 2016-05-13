package com.call_sms.ui;

/**
 * Created by konka on 2016-3-30.
 */

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.call_sms.core.KeywordInfo;
import com.call_sms.infest_filter.FilterApplication;
import com.call_sms.infest_filter.R;

/**
 * Created by konka on 2016-3-16.
 */
public class AddKeywordActivity extends Activity implements View.OnClickListener{

    private EditText content = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_keyword_view);
        Button back = (Button)findViewById(R.id.id_add_keyword_back);
        back.setOnClickListener(this);
        Button ok = (Button)findViewById(R.id.id_add_keyword_ok);
        ok.setOnClickListener(this);
        content = (EditText)findViewById(R.id.editTextKeyword);
        content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
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
            case R.id.id_add_keyword_back:
                this.finish();
                break;
            case R.id.id_add_keyword_ok:
                if(content.getText().toString().length()<1){
                    //提示
                    Toast.makeText(getApplicationContext(),"您没有输入关键字哟",Toast.LENGTH_SHORT).show();
                }else{
                    KeywordInfo info = new KeywordInfo();
                    info.setContent(content.getText().toString());
                    FilterApplication.getInstance().addKeywordListInfo(info);
                    info = null;
                    Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                break;
        }
    }
}

