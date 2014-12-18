package jp.tsur.twitwear.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import jp.tsur.twitwear.R;
import jp.tsur.twitwear.service.TwitterService;


public class RequestTlActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        Intent intent = new Intent(this, TwitterService.class);
        intent.putExtra(TwitterService.EXTRA_ACTION, TwitterService.DATA_MAP_PATH_TIMELINE);
        startService(intent);
    }
}
