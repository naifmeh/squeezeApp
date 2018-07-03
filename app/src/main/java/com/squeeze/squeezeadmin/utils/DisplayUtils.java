package com.squeeze.squeezeadmin.utils;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.widget.ImageView;

import com.squeeze.squeezeadmin.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayUtils {
    private static final String TAG = DisplayUtils.class.getSimpleName();

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


}
