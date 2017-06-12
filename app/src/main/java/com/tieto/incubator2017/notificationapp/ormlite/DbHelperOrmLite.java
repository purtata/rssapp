package com.tieto.incubator2017.notificationapp.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tieto.incubator2017.notificationapp.model.RSSChannel;
import com.tieto.incubator2017.notificationapp.model.RSSItem;

import java.sql.SQLException;


public class DbHelperOrmLite extends OrmLiteSqliteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RSSDatabase.db";

    public static final String TABLE_CHANNELS = "channels";
    public static final String TABLE_ITEMS = "items";

    private Dao<RSSChannel, Integer> mChannelsDao;
    private Dao<RSSItem, Integer> mItemsDao;

    public DbHelperOrmLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, RSSChannel.class);
            TableUtils.createTable(connectionSource, RSSItem.class);
        } catch (SQLException e) {
            Log.e(DbHelperOrmLite.class.getName(), "Could not create a database", e);
        }

        try {
            mChannelsDao.create(new RSSChannel("RMF FM News", "http://www.rmf24.pl/fakty/feed"));
            mChannelsDao.create(new RSSChannel("Interia Fakty", "http://fakty.interia.pl/feed"));
            mChannelsDao.create(new RSSChannel("Polsat News", "http://www.polsatnews.pl/rss/wszystkie.xml"));
            mChannelsDao.create(new RSSChannel("Money.pl", "http://www.money.pl/rss/main.xml"));
        } catch (SQLException e) {
            Log.e(DbHelperOrmLite.class.getName(), "Could not write init data", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public Dao<RSSChannel, Integer> getChannelsDao() throws SQLException {
        if (mChannelsDao == null) {
            mChannelsDao = getDao(RSSChannel.class);
        }
        return mChannelsDao;
    }

    public Dao<RSSItem, Integer> getItemsDao() throws SQLException {
        if (mItemsDao == null) {
            mItemsDao = getDao(RSSItem.class);
        }
        return mItemsDao;
    }
}
