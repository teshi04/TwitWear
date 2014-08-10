package jp.tsur.twitwear.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jp.tsur.twitwear.lib.Page;
import jp.tsur.twitwear.ui.TimelineActivity;


public class TimelinelService extends WearableListenerService {

    private static final String TAG = "twitwear";
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
        Log.d(TAG, "onDataChanged");
        ArrayList<Page> pages = new ArrayList<Page>();
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getDataItem().getUri().getPath().equals("/timeline")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                ArrayList<DataMap> dataMapList = dataMapItem.getDataMap().getDataMapArrayList("pages");
                for (DataMap pageDataMap : dataMapList) {
                    byte[] byteArray = pageDataMap.getByteArray("page");

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                    ObjectInputStream objectInputStream = null;
                    try {
                        objectInputStream = new ObjectInputStream(byteArrayInputStream);
                        objectInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    assert objectInputStream != null;
                    Page page = null;
                    try {
                        page = (Page) objectInputStream.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    page.SerializableBitmapWrapper(loadBitmapFromAsset(pageDataMap.getAsset("image")));
                    pages.add(page);

                }
                dataEvents.close();

                Log.d(TAG, "dataEvents.close()");
                Intent intent = new Intent(this, TimelineActivity.class);
                intent.putExtra("pages", pages);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(5000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }
}
