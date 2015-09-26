package jp.tsur.twitwear.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.tsur.twitwear.R;
import jp.tsur.twitwear.lib.Page;

public class TimelineActivity extends Activity {

    @InjectView(R.id.pager)
    GridViewPager mPager;

    @InjectView(R.id.indicator)
    DotsPageIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.inject(this);

        ArrayList<Page> pages = (ArrayList<Page>) getIntent().getSerializableExtra("pages");
        mPager.setAdapter(new SampleGridPagerAdapter(this, getFragmentManager(), pages));
        indicator.setPager(mPager);
    }
}
