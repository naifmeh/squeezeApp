package com.squeeze.squeezeadmin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputEditText;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.beans.ImageBean;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayUtils {
    private static final String TAG = DisplayUtils.class.getSimpleName();

    public final static int THUMBNAIL_SIZE = 75;

    public static boolean isEditTextEmpty(TextInputEditText editText) {
        if(editText.getText().toString().trim().length() == 0)
            return true;
        return false;
    }

    public static boolean areEditTextsEmpty(TextInputEditText editTexts[]) {
        for(int i=0;i<editTexts.length;i++) {
            boolean result = isEditTextEmpty(editTexts[i]);
            if(result == true) return true;
        }
        return false;
    }

    public static long getEquivalentTimestamp(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateParsed = simpleDateFormat.parse(date);
            return dateParsed.getTime();
        } catch(ParseException e) {
            Log.e(TAG,e.getMessage());
        }

        return 0;
    }

    public static void setLockIcon(ImageView view, boolean state, Context context) {
        if(state) {
            view.setImageDrawable(context.getDrawable(R.drawable.ic_lock_open_24px));
        } else view.setImageDrawable(context.getDrawable(R.drawable.ic_lock_closed_24px));
    }

    public static String getTimestampDate(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date(timestamp);
        return simpleDateFormat.format(date);
    }

    public static Bitmap getThumbnail(InputStream inputStream) {
        Bitmap imgThumb = null;
        imgThumb = BitmapFactory.decodeStream(inputStream);
        imgThumb = Bitmap.createScaledBitmap(imgThumb, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgThumb.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        return imgThumb;

    }

    public static ImageBean getImageBeanFromImage(InputStream inputStream,String fileName) {
        Bitmap img;
        img = BitmapFactory.decodeStream(inputStream);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] byteArray = bos.toByteArray();

        String encoded = Base64.encodeToString(byteArray,Base64.DEFAULT);
        long timeStamp = new Date().getTime();
        ImageBean imageBean = new ImageBean(timeStamp,fileName,encoded);

        return imageBean;
    }


}
