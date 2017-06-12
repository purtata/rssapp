package com.tieto.incubator2017.notificationapp.newsprovider;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.tieto.incubator2017.notificationapp.activities.MainActivity;
import com.tieto.incubator2017.notificationapp.model.ItemDAO;
import com.tieto.incubator2017.notificationapp.model.RSSChannel;
import com.tieto.incubator2017.notificationapp.model.RSSItem;
import com.tieto.incubator2017.notificationapp.news.NewsFragment;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class NewsProvider extends AsyncTask<Void, Void, Boolean> {

    private MainActivity mActivity;
    private ItemDAO mItemDAO;
    private List<RSSChannel> mChannels;

    public NewsProvider(Activity activity, List <RSSChannel> channels) {
        mActivity = (MainActivity)activity;
        mItemDAO = mActivity.getItemDAO();
        mChannels = channels;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        RSSNewsDownloader rssNewsDownloader = new RSSNewsDownloader();
        for (RSSChannel channel : mChannels) {
            try {
                List<RSSItem> news = rssNewsDownloader.downloadNews(channel);
                performUpdateAndDeleteOnDatabase(news);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(NewsProvider.class.getName(), "Could not add item to Database", e);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void performUpdateAndDeleteOnDatabase(List<RSSItem> news) throws SQLException {
        List<RSSItem> itemListToDelete = mItemDAO.getIsReadItemList(RSSItem.TO_DELETE);
        for (RSSItem item : itemListToDelete) {
            if (!news.contains(item)) {
                mItemDAO.deleteItemFromDatabase(item);
            }
        }
        List<RSSItem> itemList = mItemDAO.getItemList();
        for (RSSItem itemNews : news) {
            if (!itemList.contains(itemNews)) {
                itemList.add(itemNews);
                mItemDAO.addItemToDatabase(itemNews);
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Intent intent = new Intent();
        intent.setAction(NewsFragment.REFRESH_FRAGMENT);
        mActivity.sendBroadcast(intent);
    }
}