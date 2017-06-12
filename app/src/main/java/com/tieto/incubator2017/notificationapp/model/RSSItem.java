package com.tieto.incubator2017.notificationapp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tieto.incubator2017.notificationapp.ormlite.DbHelperOrmLite;

@DatabaseTable(tableName = DbHelperOrmLite.TABLE_ITEMS)
public class RSSItem {
    public static final int NEWS = 0;
    public static final int ARCHIVE = 1;
    public static final int TO_DELETE = -1;

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String title;
    @DatabaseField
    private String url;
    @DatabaseField
    private String pubDate;
    @DatabaseField
    private int isRead;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private RSSChannel channel;

    public RSSItem() {
    }

    public RSSItem(RSSChannel channel, String title, String url, String pubDate) {
        this.channel = channel;
        this.title = title;
        this.url = url;
        this.pubDate = pubDate;
        isRead = NEWS;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        this.title = mTitle;
    }

    public void setUrl(String mUrl) {
        this.url = mUrl;
    }

    public void setPubDate(String mPubDate) {
        this.pubDate = mPubDate;
    }

    public String getUrl() {
        return url;
    }

    public String getPubDate() {
        return pubDate;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setRead(int read){
        isRead = read;
    }

    public RSSChannel getChannel() {
        return channel;
    }

    public void setChannel(RSSChannel channel) {
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        RSSItem item = (RSSItem)obj;
        return (item.getUrl().equals(url));
    }
}
