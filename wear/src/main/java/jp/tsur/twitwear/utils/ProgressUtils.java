package jp.tsur.twitwear.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.wearable.activity.ConfirmationActivity;


public class ProgressUtils {

    private static ProgressDialog sProgressDialog;

    public static void showProgressDialog(Context context) {
        sProgressDialog = new ProgressDialog(context);
        sProgressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (sProgressDialog != null)
            try {
                sProgressDialog.dismiss();
            } finally {
                sProgressDialog = null;
            }
    }

    public static void startConfirmationActivity(Context context, int animationType, String message) {
        Intent confirmationActivity = new Intent(context, ConfirmationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION)
                .putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, animationType)
                .putExtra(ConfirmationActivity.EXTRA_MESSAGE, message);
        context.startActivity(confirmationActivity);
    }

}
