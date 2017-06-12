package com.tieto.incubator2017.notificationapp.news;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tieto.incubator2017.notificationapp.R;
import com.tieto.incubator2017.notificationapp.activities.MainActivity;
import com.tieto.incubator2017.notificationapp.model.ChannelDAO;
import com.tieto.incubator2017.notificationapp.model.ItemDAO;
import com.tieto.incubator2017.notificationapp.model.RSSChannel;
import com.tieto.incubator2017.notificationapp.model.RSSItem;
import com.tieto.incubator2017.notificationapp.newsprovider.NewsProvider;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    private ItemDAO mItemDAO;
    private ChannelDAO mChannelDAO;
    private List<RSSItem> mItemList;
    private IntentFilter mIntentFilter;
    private NewsProvider mNewsProvider;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public static final String REFRESH_FRAGMENT = "com.tieto.incubator2017.notificationapp.news.REFRESH_FRAGMENT";

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (REFRESH_FRAGMENT.equals(intent.getAction())) {
                setupAdapterList();
                mNewsAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.news_recycler_view);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(REFRESH_FRAGMENT);
        getActivity().registerReceiver(this.mBroadcastReceiver, this.mIntentFilter);
        mItemDAO = ((MainActivity)getActivity()).getItemDAO();
        mChannelDAO = ((MainActivity)getActivity()).getChannelDAO();
        mItemList = new ArrayList<>();
        setupAdapterList();
        mNewsAdapter = new NewsAdapter(getContext(), mItemList, mItemDAO);
        initializeRecyclerView();
        setupSwipeRefresh(view);
        if (MainActivity.mCurrentIdFragment == R.id.navigation_news) {
            setupSwipe(RSSItem.ARCHIVE);
        }
        if (MainActivity.mCurrentIdFragment == R.id.navigation_archive) {
            setupSwipe(RSSItem.TO_DELETE);
        }
        return view;
    }

    private void setupSwipeRefresh(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadNews(mChannelDAO.getCheckedChannelList());
            }
        });
    }

    private void setupAdapterList() {
        List<RSSChannel> channelList = mChannelDAO.getCheckedChannelList();
        List<RSSItem> itemList;
        switch (MainActivity.mCurrentIdFragment) {
            case R.id.navigation_news:
                itemList = mItemDAO.getIsReadItemList(RSSItem.NEWS);
                break;
            case R.id.navigation_archive:
                itemList = mItemDAO.getIsReadItemList(RSSItem.ARCHIVE);
                break;
            default:
                itemList = new ArrayList<>();
        }

        for (RSSChannel channel : channelList) {
            for (RSSItem item : itemList) {
                if (!mItemList.contains(item) && channel.equals(item.getChannel())) {
                    mItemList.add(item);
                }
            }
        }

        Collections.sort(mItemList, new Comparator<RSSItem>() {
            @Override
            public int compare(RSSItem o1, RSSItem o2) {
                try {
                    Date o1Date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                            .parse(o1.getPubDate());
                    Date o2Date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                            .parse(o2.getPubDate());
                    return o2Date.compareTo(o1Date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(this.mBroadcastReceiver);
    }

    private void initializeRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mNewsAdapter);
    }

    private void setupSwipe(final int status) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                NewsAdapter.MyViewHolder myViewHolder = (NewsAdapter.MyViewHolder) viewHolder;
                int position = myViewHolder.getAdapterPosition();
                RSSItem rssItem = mNewsAdapter.getItemList().get(position);
                rssItem.setRead(status);
                try {
                    mItemDAO.updateItemInDatabase(rssItem);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(NewsFragment.class.getName(), "Could not update item in Database", e);
                }
                mNewsAdapter.getItemList().remove(position);
                mNewsAdapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void downloadNews(List<RSSChannel> channels) {
        mNewsProvider = new NewsProvider(getActivity(), channels);
        mNewsProvider.execute();
    }
}
