package jp.tsur.twitwear.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;

import jp.tsur.twitwear.lib.Page;
import jp.tsur.twitwear.twitter.TwitterApi;
import jp.tsur.twitwear.utils.Utils;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;


public class ListenerService extends WearableListenerService {

    private static final String TAG = "twitwear";
    public static final String DATA_MAP_PATH_TIMELINE = "/timeline";
    public static final String DATA_MAP_PATH_FAVORITE = "/favorite";
    public static final String DATA_MAP_PATH_RETWEET = "/retweet";
    public static final String DATA_MAP_PATH_UPDATE_STATUS = "/update_status";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            String path = dataEvent.getDataItem().getUri().getPath();
            if (DATA_MAP_PATH_TIMELINE.equals(path)) {
                return;
            }
            DataMap dataMap = DataMap.fromByteArray(dataEvent.getDataItem().getData());
            long statusId = dataMap.getLong("status_id");
            switch (path) {
                case DATA_MAP_PATH_UPDATE_STATUS:
                    try {
                        TwitterApi.updateStatus(this,
                                dataMap.getString("status_text"), dataMap.getLong("in_reply_to_status_id"));
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }

                    break;
                case DATA_MAP_PATH_FAVORITE:
                    try {
                        TwitterApi.createFavorite(this, statusId);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    break;
                case DATA_MAP_PATH_RETWEET:
                    try {
                        TwitterApi.retweetStatus(this, statusId);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getPath().equals(DATA_MAP_PATH_TIMELINE)) {
            try {
                Paging paging = new Paging();
                paging.setCount(10);
                ResponseList<Status> statuses = TwitterApi.getHomeTimeline(this, paging);

                PutDataMapRequest dataMap = PutDataMapRequest.create(DATA_MAP_PATH_TIMELINE);
                dataMap.getDataMap().putDataMapArrayList("pages", createPages(statuses));
                Wearable.DataApi.putDataItem(mGoogleApiClient, dataMap.asPutDataRequest());
                Log.d(TAG, "asPutDataRequest");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<DataMap> createPages(ResponseList<Status> statuses) throws Exception {
        ArrayList<DataMap> pages = new ArrayList<DataMap>();
        for (twitter4j.Status status : statuses) {
            User user = status.getUser();

            InputStream is = new URL(user.getOriginalProfileImageURL()).openStream();
            Bitmap profileImage = BitmapFactory.decodeStream(is);
            is.close();
            Asset asset = createAssetFromBitmap(profileImage);

            Page page = new Page(status.getId(),
                    user.getName(), "@" + user.getScreenName(),
                    status.getText(),
                    Utils.getRelativeTime(status.getCreatedAt()), null);

            // byte配列にして渡す
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert objectOutputStream != null;
            try {
                objectOutputStream.writeObject(page);
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final byte[] serialized = byteArrayOutputStream.toByteArray();

            DataMap dataMap = new DataMap();
            dataMap.putAsset("image", asset);
            dataMap.putByteArray("page", serialized);
            pages.add(dataMap);
        }
        return pages;
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

}