package jp.tsur.twitwear.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import jp.tsur.twitwear.R;
import jp.tsur.twitwear.service.TwitterService;


public class StatusActionFragment extends Fragment {

    private static final int SPEECH_REQUEST_CODE = 0;

    @InjectView(R.id.action_name_label)
    TextView mActionNameLabel;
    @InjectView(R.id.circle_image_view)
    CircledImageView mCircledImageView;

    private int mAction;
    private long mStatusId;
    private String mScreenName;

    public static StatusActionFragment newInstance(int action, long statusId, String screenName) {
        StatusActionFragment fragment = new StatusActionFragment();
        Bundle args = new Bundle();
        args.putInt(TwitterService.EXTRA_ACTION, action); // お気に入り or 返信
        args.putLong("status_id", statusId);
        args.putString("screen_name", screenName);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.card_action, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arg = getArguments();
        mAction = arg.getInt(TwitterService.EXTRA_ACTION);
        mStatusId = arg.getLong("status_id");
        mScreenName = arg.getString("screen_name");
        switch (mAction) {
            case SampleGridPagerAdapter.ACTION_FAVORITE:
                mActionNameLabel.setText(getString(R.string.label_favorite));
                mCircledImageView.setImageDrawable(getResources().getDrawable(R.drawable.action_favorite));
                break;
            case SampleGridPagerAdapter.ACTION_REPLY:
                mActionNameLabel.setText(getString(R.string.label_reply));
                mCircledImageView.setImageDrawable(getResources().getDrawable(R.drawable.action_reply));
                break;
            case SampleGridPagerAdapter.ACTION_RETWEET:
                mActionNameLabel.setText(getString(R.string.label_retweet));
                mCircledImageView.setImageDrawable(getResources().getDrawable(R.drawable.action_rt));
                break;
        }
    }

    @OnClick(R.id.circle_image_view)
    void action() {
        Intent intent;
        switch (mAction) {
            case SampleGridPagerAdapter.ACTION_FAVORITE:
                intent = new Intent(getActivity(), TwitterService.class);
                intent.putExtra("status_id", mStatusId);
                intent.putExtra(TwitterService.EXTRA_ACTION, TwitterService.DATA_MAP_PATH_FAVORITE);
                getActivity().startService(intent);
                break;
            case SampleGridPagerAdapter.ACTION_REPLY:
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
                break;
            case SampleGridPagerAdapter.ACTION_RETWEET:
                intent = new Intent(getActivity(), TwitterService.class);
                intent.putExtra("status_id", mStatusId);
                intent.putExtra(TwitterService.EXTRA_ACTION, TwitterService.DATA_MAP_PATH_RETWEET);
                getActivity().startService(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            Intent intent = new Intent(getActivity(), PostActivity.class);
            intent.putExtra(PostActivity.EXTRA_IN_REPLY_STATUS_ID, mStatusId);
            intent.putExtra(PostActivity.EXTRA_STATUS_TEXT, mScreenName + " " + spokenText);
            startActivity(intent);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
