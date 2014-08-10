package jp.tsur.twitwear.task;

import android.content.Context;
import android.os.AsyncTask;

import jp.tsur.twitwear.twitter.TwitterUtils;
import twitter4j.TwitterException;

public class RetweetStatusTask extends AsyncTask<Long, Void, TwitterException> {

    private Context mContext;

    public RetweetStatusTask(Context context) {
        mContext = context;
    }

    @Override
    protected TwitterException doInBackground(Long... params) {
        try {
            TwitterUtils.getTwitterInstance(mContext).retweetStatus(params[0]);
        } catch (TwitterException e) {
            return e;
        }
        return null;
    }
}