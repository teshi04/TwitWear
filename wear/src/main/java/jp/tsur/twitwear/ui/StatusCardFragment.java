package jp.tsur.twitwear.ui;

import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.tsur.twitwear.R;


public class StatusCardFragment extends CardFragment {

    @InjectView(R.id.name_label)
    TextView mNameLabel;
    @InjectView(R.id.screen_name_label)
    TextView mScreenNameLabel;
    @InjectView(R.id.text_label)
    TextView mTextLabel;
    @InjectView(R.id.date_label)
    TextView mDateLabel;

    public static StatusCardFragment newInstance(String name, String screenName, String text, String date) {
        StatusCardFragment fragment = new StatusCardFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("screen_name", screenName);
        args.putString("text", text);
        args.putString("date", date);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.card_status, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        mNameLabel.setText(args.getString("name"));
        mScreenNameLabel.setText(args.getString("screen_name"));
        mTextLabel.setText(args.getString("text"));
        mDateLabel.setText(args.getString("date"));
    }
}
