package jp.tsur.twitwear.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.Gravity;

import java.util.ArrayList;

import jp.tsur.twitwear.lib.Page;

/**
 * Constructs fragments as requested by the GridViewPager. For each row a
 * different background is provided.
 */
public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    public static final int ACTION_STATUS = 0;
    public static final int ACTION_FAVORITE = 1;
    public static final int ACTION_REPLY = 2;
    public static final int ACTION_RETWEET = 3;

    private final ArrayList<Page> mPages;
    private Context mContext;

    public SampleGridPagerAdapter(Context context, FragmentManager fm, ArrayList<Page> pages) {
        super(fm);
        mPages = pages;
        mContext = context;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = mPages.get(row);

        switch (col) {
            case ACTION_STATUS:
                StatusCardFragment fragment = StatusCardFragment.newInstance(page.name, page.screenName, page.text, page.date);
                fragment.setCardGravity(Gravity.TOP);
                fragment.setExpansionEnabled(true);
                fragment.setExpansionDirection(CardFragment.EXPAND_DOWN);
                fragment.setExpansionFactor(10);
                return fragment;
            case ACTION_FAVORITE:
                return StatusActionFragment.newInstance(ACTION_FAVORITE, page.statusId, null);
            case ACTION_REPLY:
                return StatusActionFragment.newInstance(ACTION_REPLY, page.statusId, page.screenName);
            case ACTION_RETWEET:
                return StatusActionFragment.newInstance(ACTION_RETWEET, page.statusId, null);
            default:
                return null;
        }
    }

    @Override
    public Drawable getBackgroundForPage(int row, int column) {
        return new BitmapDrawable(mContext.getResources(), mPages.get(row).getBitmap());
    }

    @Override
    public int getRowCount() {
        return mPages.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return 4;
    }
}
