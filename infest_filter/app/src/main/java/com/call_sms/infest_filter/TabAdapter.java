/**
 * ��Ȩ����  					 
 *																		 
 * �ļ����ƣ�DesktopAdapter.java													 
 * �ļ���ʶ����������TAB��������
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
 * ��          ע�� ����ʹ��FragmentStatePagerAdapter������������̬��Ӻ�ɾ��ҳ��
 */
package com.call_sms.infest_filter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;

public class TabAdapter extends FragmentStatePagerAdapter 
{
	private ArrayList<Fragment> mTabFragmentsList = null;


    public TabAdapter(FragmentManager fm) 
    {
    	super(fm);
    }


    public TabAdapter(FragmentManager fm, ArrayList<Fragment> fragments) 
    {
        super(fm);
        this.mTabFragmentsList = fragments;
    }


    @Override
    public int getCount() 
    {
        return mTabFragmentsList.size();
    }


    @Override
    public Fragment getItem(int arg0) 
    {
        return mTabFragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) 
    {
    	return PagerAdapter.POSITION_NONE;
    }
}
