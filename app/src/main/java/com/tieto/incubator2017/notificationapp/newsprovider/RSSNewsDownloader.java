package com.tieto.incubator2017.notificationapp.newsprovider;

import android.support.annotation.NonNull;
import android.util.Xml;

import com.tieto.incubator2017.notificationapp.model.RSSChannel;
import com.tieto.incubator2017.notificationapp.model.RSSItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RSSNewsDownloader {
    public List<RSSItem> downloadNews(RSSChannel channel) throws XmlPullParserException,
            IOException {
        String title = null;
        String url = null;
        String date = null;
        boolean isItem = false;

        List<RSSItem> items = new ArrayList<>();
        InputStream inputStream = getInputStream(channel);

        XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xmlPullParser.setInput(inputStream, null);

        try {
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();
                String name = xmlPullParser.getName();
                if (name == null) {
                    continue;
                }
                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }
                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }
                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    url = result;
                } else if (name.equalsIgnoreCase("pubdate")) {
                    date = result;
                }

                if (title != null && url != null && date != null) {
                    if (isItem) {
                        RSSItem item = new RSSItem(channel, title, url, date);
                        items.add(item);
                    }
                }
            }
            return items;
        } finally {
            inputStream.close();
        }
    }

    @NonNull
    private InputStream getInputStream(RSSChannel channel) throws IOException {
        String urlLink = channel.getUrl();
        if(!urlLink.startsWith("http://") && !urlLink.startsWith("https://")) {
            urlLink = "http://" + urlLink;
        }
        URL url = new URL(urlLink);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        return new BufferedInputStream(urlConnection.getInputStream());
    }
}