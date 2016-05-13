
package com.call_sms.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.call_sms.infest_filter.R;

public class BlackListPopWindow extends PopupWindow
{
	private View mMenuView = null; 	

	private RelativeLayout entryAddByCalllog;
	private RelativeLayout entryAddBySms;
	private RelativeLayout entryAddByContacts;
	private RelativeLayout entryAddByHand;

	private Button cancel;

	@SuppressWarnings("deprecation")
	public BlackListPopWindow(Activity context, OnClickListener itemsOnClick)
    {
		super(context);  
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		mMenuView = inflater.inflate(R.layout.blacklist_popwindow, null);

		entryAddByCalllog = (RelativeLayout)mMenuView.findViewById(R.id.id_blacklist_popupwindow_call);
		entryAddByCalllog.setOnClickListener(itemsOnClick);
		entryAddBySms = (RelativeLayout)mMenuView.findViewById(R.id.id_blacklist_popupwindow_sms);
		entryAddBySms.setOnClickListener(itemsOnClick);
		entryAddByContacts = (RelativeLayout)mMenuView.findViewById(R.id.id_blacklist_popupwindow_contacts);
		entryAddByContacts.setOnClickListener(itemsOnClick);
		entryAddByHand = (RelativeLayout)mMenuView.findViewById(R.id.id_blacklist_popupwindow_hand);
		entryAddByHand.setOnClickListener(itemsOnClick);
		cancel = (Button)mMenuView.findViewById(R.id.id_blacklist_popupwidow_cancel);
		cancel.setOnClickListener(itemsOnClick);

        this.setContentView(mMenuView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw); 
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupWindowAnim);
        mMenuView.setOnTouchListener(new OnTouchListener() 
        {  
        	@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) 
        	{  
        		int height = mMenuView.findViewById(R.id.id_desktop_contacts_detail_popupwidow_layout).getTop();  
        		int y=(int) event.getY();  
        		if(event.getAction()==MotionEvent.ACTION_UP)
        		{  
        			if(y<height)
        			{  
        				dismiss();  
        			}  
        		}                 
        		return true;  
        	}  
        });  
    }
}