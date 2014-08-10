package jp.tsur.twitwear.task;

import android.content.Context;
import android.os.AsyncTask;

import jp.tsur.twitwear.twitter.TwitterUtils;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

public class UpdateStatusTask extends AsyncTask<StatusUpdate, Void, TwitterException> {

    private Context mContext;

    public UpdateStatusTask(Context context) {
        mContext = context;
    }

    @Override
    protected TwitterException doInBackground(StatusUpdate... params) {
        StatusUpdate statusUpdate = params[0];
        try {
            TwitterUtils.getTwitterInstance(mContext).updateStatus(statusUpdate);
        } catch (TwitterException e) {
            return e;
        }
        return null;
    }
}