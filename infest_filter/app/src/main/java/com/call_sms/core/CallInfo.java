/**
 * ��Ȩ����     							 
 *																		 
 * �ļ����ƣ�CallInfo.java													 
 * �ļ���ʶ���绰��
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

package com.call_sms.core;


import java.io.Serializable;

public class CallInfo implements Serializable
{
	private String mPhoneName;
	private String mPhoneNumber;
	private String mCallId;
	private String mTime;
	private int mReason;
	private String mLocation;

	public CallInfo() 
	{
		this.mPhoneName ="";
		this.mPhoneNumber = "";
		this.mCallId = "";
		this.mTime = "";
		this.mReason = -1;
		this.mLocation="";
	}  

	public CallInfo(CallInfo info) 
	{
		this.mPhoneName = getPhoneName();
		this.mPhoneNumber = info.getPhoneNumber();
		this.mCallId = info.getCallId();
		this.mTime = info.getTime();
		this.mReason = info.getReason();
		this.mLocation = info.getLocation();
	}

	public String getPhoneName()
	{
		return this.mPhoneName;
	}

	public void setPhoneName(String name)
	{
		this.mPhoneName = name;
	}

	public String getPhoneNumber()
	{
		return this.mPhoneNumber;
	}

	public void setPhoneNumber(String number)
	{
		this.mPhoneNumber = number;
	}	

	public String getCallId()
	{
		return this.mCallId;
	}

	public void setCallId(String id)
	{
		this.mCallId = id;
	}	

	public String getTime()
	{
		return this.mTime;
	}

	public void setTime(String time)
	{
		this.mTime = time;
	}	

	public int getReason()
	{
		return this.mReason;
	}

	public void setReason(int reason)
	{
		this.mReason = reason;
	}

	public String getLocation()
	{
		return this.mLocation;
	}

	public void setLocation(String location)
	{
		this.mLocation = location;
	}
}
