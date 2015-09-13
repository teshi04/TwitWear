package jp.tsur.twitwear.task;

import android.content.Context;
import android.os.AsyncTask;

import jp.tsur.twitwear.twitter.TwitterUtils;
import twitter4j.TwitterException;
import twitter4j.User;


public class VerifyCredentialsTask extends AsyncTask<Void, Void, User> {

    Context context;

    public VerifyCredentialsTask(Context context) {
        this.context = context;
    }

    @Override
    protected User doInBackground(Void... params) {
        try {
            return TwitterUtils.getTwitterInstance(context).verifyCredentials();
        } catch (TwitterException e) {
            e.printStackTrace();
            return null;
        }
    }
}