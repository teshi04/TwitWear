package jp.tsur.twitwear.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;


public class TwitterService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String DATA_MAP_PATH_TIMELINE = "/timeline";
    public static final String DATA_MAP_PATH_FAVORITE = "/favorite";
    public static final String DATA_MAP_PATH_RETWEET = "/retweet";
    public static final String DATA_MAP_PATH_REPLY = "/reply";
    public static final String EXTRA_ACTION = "action";


    private GoogleApiClient mGoogleApiClient;

    public TwitterService() {
        super(TwitterService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mGoogleApiClient.blockingConnect(100, TimeUnit.MILLISECONDS);
        if (mGoogleApiClient.isConnected()) {

            if (intent.getStringExtra(EXTRA_ACTION).equals(DATA_MAP_PATH_TIMELINE)) {
                NodeApi.GetConnectedNodesResult nodes =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), DATA_MAP_PATH_TIMELINE,
                            new byte[0]);
                }
                mGoogleApiClient.disconnect();
                return;
            }

            PutDataMapRequest dataMap;
            DataApi.DataItemResult result = null;
            switch (intent.getStringExtra(EXTRA_ACTION)) {
                case DATA_MAP_PATH_FAVORITE:
                    dataMap = PutDataMapRequest.create(DATA_MAP_PATH_FAVORITE);
                    dataMap.getDataMap().putLong("status_id", intent.getLongExtra("status_id", -1));
                    result = Wearable.DataApi.putDataItem(mGoogleApiClient, dataMap.asPutDataRequest()).await();
                    break;
                case DATA_MAP_PATH_RETWEET:
                    dataMap = PutDataMapRequest.create(DATA_MAP_PATH_RETWEET);
                    dataMap.getDataMap().putLong("status_id", intent.getLongExtra("status_id", -1));
                    result = Wearable.DataApi.putDataItem(mGoogleApiClient, dataMap.asPutDataRequest()).await();
                    break;
                case DATA_MAP_PATH_REPLY:
                    dataMap = PutDataMapRequest.create(DATA_MAP_PATH_REPLY);
                    dataMap.getDataMap().putString("status_text", intent.getStringExtra("status_text"));
                    result = Wearable.DataApi.putDataItem(mGoogleApiClient, dataMap.asPutDataRequest()).await();
                    break;
            }

            assert result != null;

//            if (result.getStatus().isSuccess()) {
//                ProgressUtils.startConfirmationActivity(TwitterService.this,
//                        ConfirmationActivity.SUCCESS_ANIMATION, getString(R.string.confirmation_animation_success));
//            } else {
//                ProgressUtils.startConfirmationActivity(TwitterService.this,
//                        ConfirmationActivity.FAILURE_ANIMATION, getString(R.string.confirmation_animation_failure));
//            }
        }

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
