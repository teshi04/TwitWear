package jp.tsur.twitwear.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.WindowInsets;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.tsur.twitwear.R;
import jp.tsur.twitwear.lib.Page;
import jp.tsur.twitwear.utils.ProgressUtils;

public class TimelineActivity extends Activity {

    @InjectView(R.id.pager)
    GridViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.inject(this);

        ProgressUtils.dismissProgressDialog();
        final Resources res = getResources();
        mPager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                mPager.setPageMargins(rowMargin, colMargin);
                return insets;
            }
        });

        ArrayList<Page> pages = (ArrayList<Page>) getIntent().getSerializableExtra("pages");
        mPager.setAdapter(new SampleGridPagerAdapter(this, getFragmentManager(), pages));
    }
}
