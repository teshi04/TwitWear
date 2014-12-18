package jp.tsur.twitwear.twitter;

import android.content.Context;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

/**
 * Created by teshi on 2014/08/08.
 */
public class TwitterApi {

    public static ResponseList<Status> getHomeTimeline(Context context, Paging paging) throws TwitterException {
        return TwitterUtils.getTwitterInstance(context).getHomeTimeline(paging);
    }

    public static void createFavorite(Context context, long statusId) throws TwitterException {
        TwitterUtils.getTwitterInstance(context).createFavorite(statusId);
    }

    public static void retweetStatus(Context context, long statusId) throws TwitterException {
        TwitterUtils.getTwitterInstance(context).retweetStatus(statusId);
    }

    public static void updateStatus(Context context, String statusText, long inReplyToStatusId) throws TwitterException {
        StatusUpdate statusUpdate = new StatusUpdate(statusText);
        statusUpdate.setInReplyToStatusId(inReplyToStatusId);
        TwitterUtils.getTwitterInstance(context).updateStatus(statusUpdate);
    }
}
