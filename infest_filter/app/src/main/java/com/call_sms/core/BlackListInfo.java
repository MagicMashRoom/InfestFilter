package com.call_sms.core;


public class BlackListInfo 
{
	private int mId;
	private String mPhoneName;
	private String mPhoneNumber;

	public BlackListInfo() 
	{

		this.mId = -1;
		this.mPhoneName = "";
		this.mPhoneNumber = "";
	}  

	public BlackListInfo(BlackListInfo info) 
	{
		this.mId = info.getId();
		this.mPhoneName = info.getPhoneName();
		this.mPhoneNumber = info.getPhoneNumber();;

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

	public void setPhoneNumber(String name)
	{
		this.mPhoneNumber = name;
	}

	public int getId()
	{
		return this.mId;
	}

	public void setId(int id)
	{
		this.mId = id;
	}

	
}

