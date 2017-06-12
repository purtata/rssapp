package com.tieto.incubator2017.notificationapp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tieto.incubator2017.notificationapp.ormlite.DbHelperOrmLite;

@DatabaseTable(tableName = DbHelperOrmLite.TABLE_CHANNELS)
public class RSSChannel {

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String url;
    @DatabaseField
    private int isChecked;

    public RSSChannel() {
    }

    public RSSChannel(String name, String url){
        this.name = name;
        this.url = url;
        isChecked = FALSE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        String urlObj = ((RSSChannel)obj).getUrl();
        return url.equals(urlObj);
    }
}
