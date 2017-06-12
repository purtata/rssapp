package com.tieto.incubator2017.notificationapp.activities;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.tieto.incubator2017.notificationapp.channels.ChannelsFragment;
import com.tieto.incubator2017.notificationapp.model.ChannelDAO;
import com.tieto.incubator2017.notificationapp.model.RSSChannel;
import com.tieto.incubator2017.notificationapp.ormlite.DbHelperOrmLite;
import com.tieto.incubator2017.notificationapp.model.ItemDAO;
import com.tieto.incubator2017.notificationapp.news.NewsFragment;
import com.tieto.incubator2017.notificationapp.R;
import com.tieto.incubator2017.notificationapp.newsprovider.NewsProvider;
import com.tieto.incubator2017.notificationapp.screentextsender.SendTextFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MANUAL_REFRESH_ONLY = 0;
    private static final int AUTO_REFRESH_10_MIN = 1;
    private static int mCurrentRefreshSetting = 0;
    public static int mCurrentIdFragment = 0;

    private FloatingActionButton mFloatingActionButton;
    private Toolbar mToolbar;
    private NewsProvider mNewsProvider;
    private DbHelperOrmLite mDatabaseHelper;
    private ChannelDAO mChannelDAO;
    private ItemDAO mItemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.news);
        setSupportActionBar(mToolbar);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mDatabaseHelper = new DbHelperOrmLite(this);
        mChannelDAO = new ChannelDAO(mDatabaseHelper);
        mItemDAO = new ItemDAO(mDatabaseHelper);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (mCurrentIdFragment == 0) {
            mCurrentIdFragment = R.id.navigation_news;
        }
        navigationView.setCheckedItem(mCurrentIdFragment);
        onNavigationItemSelected(navigationView.getMenu().findItem(mCurrentIdFragment));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            final CharSequence[] items = getResources().getTextArray(R.array.string_array_settings);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.action_settings)
                    .setSingleChoiceItems(items, mCurrentRefreshSetting, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which) {
                                case MANUAL_REFRESH_ONLY:
                                    mCurrentRefreshSetting = MANUAL_REFRESH_ONLY;
                                    //TODO some action
                                    break;
                                case AUTO_REFRESH_10_MIN:
                                    mCurrentRefreshSetting = AUTO_REFRESH_10_MIN;
                                    //TODO some action
                                    break;
                            }
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        } else if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.about)
                    .setMessage(R.string.about_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        if (id == R.id.navigation_news) {
            mCurrentIdFragment = R.id.navigation_news;
            downloadNews(mChannelDAO.getCheckedChannelList());
            mToolbar.setTitle(R.string.news);
            mFloatingActionButton.hide();
            goToFragment(new NewsFragment());
        } else if (id == R.id.navigation_channels) {
            mCurrentIdFragment = R.id.navigation_channels;
            mToolbar.setTitle(R.string.channels);
            mFloatingActionButton.show();
            goToFragment(new ChannelsFragment());
        } else if (id == R.id.navigation_archive) {
            mCurrentIdFragment = R.id.navigation_archive;
            mToolbar.setTitle(R.string.archive);
            mFloatingActionButton.hide();
            goToFragment(new NewsFragment());
        } else if (id == R.id.navigation_user_text) {
            mCurrentIdFragment = R.id.navigation_user_text;
            mToolbar.setTitle(R.string.user_text);
            mFloatingActionButton.hide();
            goToFragment(new SendTextFragment());
        }
        return true;
    }

    private void downloadNews(List<RSSChannel> channels) {
        mNewsProvider = new NewsProvider(this, channels);
        mNewsProvider.execute();
    }

    public FloatingActionButton getFloatingActionButton() {
        return mFloatingActionButton;
    }

    public ChannelDAO getChannelDAO() {
        return mChannelDAO;
    }

    public ItemDAO getItemDAO() {
        return mItemDAO;
    }

    public void goToFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_layout, fragment);
        fragmentTransaction.commit();
    }
}
