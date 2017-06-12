package com.tieto.incubator2017.notificationapp.channels;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.tieto.incubator2017.notificationapp.R;
import com.tieto.incubator2017.notificationapp.model.ChannelDAO;
import com.tieto.incubator2017.notificationapp.model.RSSChannel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ChannelsAdapter extends RecyclerView.Adapter <ChannelsAdapter.MyViewHolder>{

    public static final String UNKNOWN = "Unknown";
    private List<RSSChannel> mItemList;
    private ChannelDAO mChannelDAO;

    public ChannelsAdapter(List<RSSChannel> itemList, ChannelDAO dao) {
        mItemList = ((itemList != null) ? itemList : (new ArrayList<RSSChannel>()));
        mChannelDAO = dao;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_channels, parent, false);
        return new ChannelsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RSSChannel item = mItemList.get(position);
        if (item == null) {
            holder.mTitle.setText(UNKNOWN);
            holder.mUrl.setText(UNKNOWN);
            holder.mSwitch.setChecked(false);
        } else {
            holder.mTitle.setText(item.getName());
            holder.mUrl.setText(item.getUrl());
            if (item.getIsChecked() == RSSChannel.TRUE) {
                holder.mSwitch.setChecked(true);
            } else {
                holder.mSwitch.setChecked(false);
            }
        }
        holder.mTitle.setTypeface(null, Typeface.BOLD);
    }

    public List<RSSChannel> getItemList() {
        return mItemList;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle, mUrl;
        public Switch mSwitch;

        public MyViewHolder(final View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.channel_name_on_list);
            mUrl = (TextView) itemView.findViewById(R.id.channel_url_on_list);
            mSwitch = (Switch) itemView.findViewById(R.id.channel_switch_on_list);
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    RSSChannel channel = mItemList.get(getAdapterPosition());
                    if (isChecked) {
                        channel.setIsChecked(RSSChannel.TRUE);
                    } else {
                        channel.setIsChecked(RSSChannel.FALSE);
                    }
                    try {
                        mChannelDAO.updateChannelInDataBase(channel);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Log.e(ChannelsAdapter.class.getName(), "Could not update channel to Database", e);
                    }
                }
            });
        }
    }
}
