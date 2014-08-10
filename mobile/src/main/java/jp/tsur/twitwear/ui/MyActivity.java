package jp.tsur.twitwear.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.tsur.twitwear.R;
import jp.tsur.twitwear.twitter.TwitterUtils;
import jp.tsur.twitwear.task.VerifyCredentialsTask;
import twitter4j.User;


public class MyActivity extends Activity {

    @InjectView(R.id.connected_twitter_id_label)
    TextView mConnectedTwitterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);

        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        VerifyCredentialsTask task = new VerifyCredentialsTask(this) {
            @Override
            protected void onPostExecute(User user) {
                mConnectedTwitterId.setText(getString(R.string.label_connected_twitter_id, user.getScreenName()));

            }
        };
        task.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
