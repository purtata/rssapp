package com.tieto.incubator2017.notificationapp.model;


import android.util.Log;

import com.tieto.incubator2017.notificationapp.ormlite.DbHelperOrmLite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChannelDAO {
    private static List<RSSChannel> mChannelList;

    private DbHelperOrmLite mDatabaseHelper;

    public ChannelDAO (DbHelperOrmLite databaseHelper) {
        mDatabaseHelper = databaseHelper;
        if (mChannelList == null) {
            mChannelList = getData();
        }
    }

    private List<RSSChannel> getData() {
        List<RSSChannel> result = new ArrayList<>();
        try {
            result = mDatabaseHelper.getChannelsDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DbHelperOrmLite.class.getName(), "Could not get channels from database", e);
        }
        return result;
    }

    public synchronized void addChannelToDatabase(RSSChannel channel) throws SQLException {
        mDatabaseHelper.getChannelsDao().create(channel);
    }

    public synchronized void deleteChannelFromDatabase (RSSChannel channel) throws SQLException {
        mDatabaseHelper.getChannelsDao().delete(channel);
    }

    public synchronized void updateChannelInDataBase(RSSChannel channel) throws SQLException {
        mDatabaseHelper.getChannelsDao().update(channel);
    }

    public synchronized List<RSSChannel> getCheckedChannelList() {
        List<RSSChannel> result = new ArrayList<>();
        for (RSSChannel channel : mChannelList) {
            if (channel.getIsChecked()==RSSChannel.TRUE) {
                result.add(channel);
            }
        }
        return result;
    }

    public synchronized List<RSSChannel> getChannelList() {
        return mChannelList;
    }
}
