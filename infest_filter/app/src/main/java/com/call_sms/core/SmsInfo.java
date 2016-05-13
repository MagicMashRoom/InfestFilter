/**
 * ��Ȩ����     							 
 *																		 
 * �ļ����ƣ�SmsInfo.java													 
 * �ļ���ʶ��������
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

public class SmsInfo implements Serializable
{
	private String mPhoneName;
	private String mPhoneNumber;
	private String mSmsId;
	private String mTime;
	private String mContent;
	private int mReason;

	public SmsInfo() 
	{
		this.mPhoneName = "";
		this.mPhoneNumber = "";
		this.mSmsId = "";
		this.mTime = "";
		this.mContent = "";
		this.mReason = -1;
	}  

	public SmsInfo(SmsInfo info) 
	{
		this.mPhoneName = info.getPhoneName();
		this.mPhoneNumber = info.getPhoneNumber();
		this.mSmsId = info.getSmsId();
		this.mTime = info.getTime();
		this.mContent = info.getContent();
		this.mReason = info.getReason();
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

	public String getSmsId()
	{
		return this.mSmsId;
	}

	public void setSmsId(String id)
	{
		this.mSmsId = id;
	}	

	public String getTime()
	{
		return this.mTime;
	}

	public void setTime(String time)
	{
		this.mTime = time;
	}	

	public String getContent()
	{
		return this.mContent;
	}

	public void setContent(String content)
	{
		this.mContent = content;
	}

	public int getReason()
	{
		return this.mReason;
	}

	public void setReason(int reason)
	{
		this.mReason = reason;
	}
}
