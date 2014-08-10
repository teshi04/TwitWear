package jp.tsur.twitwear.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;


public class Page implements Serializable {
    public long statusId;
    public String name;
    public String screenName;
    public String text;
    public String date;
    public transient Bitmap profileImage;

    public Page(long statusId, String name, String screenName, String textRes, String date, Bitmap profileImage) {
        this.statusId = statusId;
        this.name = name;
        this.screenName = screenName;
        this.text = textRes;
        this.date = date;
        this.profileImage = profileImage;
    }

    private byte[] mBitmapArray = null;

    public void SerializableBitmapWrapper(Bitmap bitmap) {
        profileImage = bitmap;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.PNG, 100, bout);
        mBitmapArray = bout.toByteArray();
    }

    public Bitmap getBitmap() {
        if (mBitmapArray == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(mBitmapArray, 0,
                mBitmapArray.length);
    }
}