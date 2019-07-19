package com.id.cash.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * Created by linchen on 2018/5/27.
 */

public class DimenUtil {
    public static int dip2px(int dip) {
        return (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, (float) dip, Resources.getSystem().getDisplayMetrics());
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(float sp) {
        return (int) TypedValue.applyDimension(COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


}
