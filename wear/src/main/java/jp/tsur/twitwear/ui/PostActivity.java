package jp.tsur.twitwear.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.tsur.twitwear.R;


public class PostActivity extends Activity implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String EXTRA_STATUS_TEXT = "status_text";
    public static final String EXTRA_IN_REPLY_STATUS_ID = "in_reply_to_status_id";

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private String mStatusText;
    private long mInReplyStatusId;

    @InjectView(R.id.timer)
    DelayedConfirmationView mDelayedConfirmationView;

    public static Intent createIntent(Context context, String spokenText) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra(PostActivity.EXTRA_STATUS_TEXT, spokenText);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        ButterKnife.inject(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Intent intent = getIntent();
        mStatusText = intent.getStringExtra(EXTRA_STATUS_TEXT);
        mInReplyStatusId = getIntent().getLongExtra(EXTRA_IN_REPLY_STATUS_ID, 0L);
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(mStatusText);
        startConfirmationTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (!mResolvingError) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void startConfirmationTimer() {
        mDelayedConfirmationView.setTotalTimeMs(5 * 1000);
        mDelayedConfirmationView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                // テキストをモバイルの PostActivity に投げる
                PutDataMapRequest dataMap = PutDataMapRequest.create("/update_status");
                dataMap.getDataMap().putString(EXTRA_STATUS_TEXT, mStatusText);
                dataMap.getDataMap().putLong(EXTRA_IN_REPLY_STATUS_ID, mInReplyStatusId);
                PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                        .putDataItem(mGoogleApiClient, dataMap.asPutDataRequest());

                // TODO: ツイートができたかできてないか、電話から時計に結果を伝えたい
                pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(final DataApi.DataItemResult result) {
                        if (result.getStatus().isSuccess()) {
                            startConfirmationActivity(PostActivity.this,
                                    ConfirmationActivity.SUCCESS_ANIMATION, getString(R.string.confirmation_animation_success));
                        } else {
                            startConfirmationActivity(PostActivity.this,
                                    ConfirmationActivity.FAILURE_ANIMATION, getString(R.string.confirmation_animation_failure));
                        }
                        finish();
                    }
                });
            }

            @Override
            public void onTimerSelected(View view) {
                // キャンセル
                finish();
            }
        });
        mDelayedConfirmationView.start();
    }

    public static void startConfirmationActivity(Context context, int animationType, String message) {
        Intent confirmationActivity = new Intent(context, ConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION)
                .putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, animationType)
                .putExtra(ConfirmationActivity.EXTRA_MESSAGE, message);
        context.startActivity(confirmationActivity);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mResolvingError = false;
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}