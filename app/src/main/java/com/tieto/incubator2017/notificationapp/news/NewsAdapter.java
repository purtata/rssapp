package com.tieto.incubator2017.notificationapp.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tieto.incubator2017.notificationapp.R;
import com.tieto.incubator2017.notificationapp.activities.MainActivity;
import com.tieto.incubator2017.notificationapp.activities.NewsWebActivity;
import com.tieto.incubator2017.notificationapp.model.ItemDAO;
import com.tieto.incubator2017.notificationapp.model.RSSItem;

import java.sql.SQLException;
import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private static final int BEGIN_INDEX = 5;
    private static final int INDEX_TO_CUT = 9;

    private List<RSSItem> mItemList;
    private Context mContext;
    private ItemDAO mItemDAO;

    public NewsAdapter(Context context, List<RSSItem> list, ItemDAO itemDAO) {
        mContext = context;
        mItemList = list;
        mItemDAO = itemDAO;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_row_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RSSItem item = mItemList.get(position);
        holder.channelName.setText(item.getChannel().getName());
        holder.channelName.setTypeface(null, Typeface.BOLD);
        holder.title.setText(item.getTitle());
        holder.date.setText(getConvertPubDate(item));
        holder.date.setTypeface(null, Typeface.BOLD);
        Picasso.with(mContext).load(R.drawable.rss_icon).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public List<RSSItem> getItemList() {
        return mItemList;
    }

    private String getConvertPubDate(RSSItem item) {
        String date = item.getPubDate();
        return date.substring(BEGIN_INDEX, date.length() - INDEX_TO_CUT);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView channelName, title, date;
        public ImageView thumbnail;

        public MyViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RSSItem rssItem = mItemList.get(getAdapterPosition());
                    if (MainActivity.mCurrentIdFragment == R.id.navigation_news) {
                        rssItem.setRead(RSSItem.ARCHIVE);
                        mItemList.remove(getAdapterPosition());
                        try {
                            mItemDAO.updateItemInDatabase(rssItem);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.e(NewsAdapter.class.getName(), "Could not update item in database", e);
                        }
                        NewsAdapter.this.notifyDataSetChanged();
                    }
                    openNewsWebsite(rssItem);
                }
            });
            channelName = (TextView) itemView.findViewById(R.id.channel_name);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }

        private void openNewsWebsite(RSSItem rssItem) {
            Intent newsWebIntent = new Intent(itemView.getContext(), NewsWebActivity.class);
            newsWebIntent.putExtra(NewsWebActivity.URL, rssItem.getUrl());
            itemView.getContext().startActivity(newsWebIntent);
        }
    }
}