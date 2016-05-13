package com.call_sms.core;

/**
 * Created by konka on 2016-3-30.
 */
public class KeywordInfo {

    private int mId;
    private String mContent;

    public KeywordInfo()
    {
        this.mId = -1;
        this.mContent = "";
    }

    public KeywordInfo(KeywordInfo info)
    {
        this.mId = info.getId();
        this.mContent = info.getContent();
    }

    public String getContent()
    {
        return this.mContent;
    }

    public void setContent(String content)
    {
        this.mContent = content;
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
