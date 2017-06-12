package com.tieto.incubator2017.notificationapp.channels;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tieto.incubator2017.notificationapp.R;
import com.tieto.incubator2017.notificationapp.activities.MainActivity;
import com.tieto.incubator2017.notificationapp.model.ChannelDAO;
import com.tieto.incubator2017.notificationapp.model.RSSChannel;

import java.sql.SQLException;
import java.util.List;


public class ChannelsFragment extends Fragment {

    private List<RSSChannel> mChannelList;
    private RecyclerView mRecyclerView;
    private ChannelsAdapter mChannelsAdapter;
    private ChannelDAO mChannelDAO;
    private FloatingActionButton mFloatingActionButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channels, container, false);
        mChannelDAO = ((MainActivity)getActivity()).getChannelDAO();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.channel_recycler_view);
        mChannelList = mChannelDAO.getChannelList();
        mFloatingActionButton = ((MainActivity)getActivity()).getFloatingActionButton();
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.add_channel_dialog_text)
                        .setView(R.layout.add_channel_dialog_view)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Dialog dialogObject = Dialog.class.cast(dialog);
                                addChannel(dialogObject);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeChannel(viewHolder);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChannelsAdapter = new ChannelsAdapter(mChannelList, mChannelDAO);
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mChannelsAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void addChannel(Dialog dialogObject) {
        EditText setChannelName = ((EditText)dialogObject.findViewById(R.id.add_channel_name));
        String channelName = setChannelName.getText().toString();
        EditText setUrl = (EditText)dialogObject.findViewById(R.id.add_channel_url);
        String url = setUrl.getText().toString();
        RSSChannel channel = new RSSChannel(channelName, url);
        mChannelDAO.getChannelList().add(channel);
        try {
            mChannelDAO.addChannelToDatabase(channel);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(ChannelsFragment.class.getName(), "Could not add channel to Database", e);
        }
        mChannelsAdapter.notifyDataSetChanged();
    }

    private void removeChannel(RecyclerView.ViewHolder viewHolder) {
        ChannelsAdapter.MyViewHolder myViewHolder = (ChannelsAdapter.MyViewHolder) viewHolder;
        int position = myViewHolder.getAdapterPosition();
        RSSChannel channel = mChannelsAdapter.getItemList().get(position);
        try {
            mChannelDAO.deleteChannelFromDatabase(channel);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(ChannelsFragment.class.getName(), "Could not delete channel from Database", e);
        }
        mChannelsAdapter.getItemList().remove(position);
        mChannelsAdapter.notifyDataSetChanged();
    }
}
