package com.tieto.incubator2017.notificationapp.model;


import android.util.Log;

import com.tieto.incubator2017.notificationapp.ormlite.DbHelperOrmLite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private static List<RSSItem> mItemList;
    private DbHelperOrmLite mDatabaseHelper;

    public ItemDAO (DbHelperOrmLite databaseHelper) {
        mDatabaseHelper = databaseHelper;
        if (mItemList == null) {
            mItemList = getData();
        }
    }

    private List<RSSItem> getData() {
        List<RSSItem> result = new ArrayList<>();
        try {
            result = mDatabaseHelper.getItemsDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DbHelperOrmLite.class.getName(), "Could not get items from Database", e);
        }
        return result;
    }

    public synchronized void addItemToDatabase(RSSItem item) throws SQLException {
        mDatabaseHelper.getItemsDao().create(item);
    }

    public synchronized void updateItemInDatabase(RSSItem item) throws SQLException {
        mDatabaseHelper.getItemsDao().update(item);
    }

    public synchronized void deleteItemFromDatabase(RSSItem item) throws SQLException {
        mDatabaseHelper.getItemsDao().delete(item);
    }

    public synchronized List<RSSItem> getItemList() {
        return mItemList;
    }

    public synchronized List<RSSItem> getIsReadItemList(int filter) {
        List<RSSItem> result = new ArrayList<>();
        for (RSSItem item : mItemList) {
            if (item.getIsRead() == filter) {
                result.add(item);
            }
        }
        return result;
    }
}
