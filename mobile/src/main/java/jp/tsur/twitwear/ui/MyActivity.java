package jp.tsur.twitwear.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.tsur.twitwear.R;
import jp.tsur.twitwear.task.VerifyCredentialsTask;
import jp.tsur.twitwear.twitter.TwitterUtils;
import twitter4j.User;


public class MyActivity extends AppCompatActivity {

    @InjectView(R.id.connected_twitter_id_label)
    TextView mConnectedTwitterId;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        VerifyCredentialsTask task = new VerifyCredentialsTask(this) {
            @Override
            protected void onPostExecute(User user) {
                if (user == null) {
                    TwitterUtils.resetAccessToken(MyActivity.this);
                    Intent intent = new Intent(MyActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                mConnectedTwitterId.setText(getString(R.string.label_connected_twitter_id, user.getScreenName()));
            }
        };
        task.execute();
    }

    private void moveToMain() {
        Intent intent = new Intent(this, MyActivity.class);
        TaskStackBuilder builder = TaskStackBuilder.create(this);
        builder.addNextIntent(intent);
        builder.startActivities();
        finish();
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
            TwitterUtils.resetAccessToken(this);
            moveToMain();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
