package com.tieto.incubator2017.notificationapp.ormlite;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;


public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static void main(String[] args) throws IOException, java.sql.SQLException {
        writeConfigFile("ormlite_config.txt");
    }
}
