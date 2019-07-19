package com.id.cash.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by linchen on 2018/5/22.
 */

@GlideExtension
public class SWGlideExtension {
    private SWGlideExtension() {}

    @NonNull
    @GlideOption
    public static RequestOptions roundedCorners(RequestOptions options, @NonNull Context context, int cornerRadius) {
        if (cornerRadius > 0) {
            int px = Math.round(cornerRadius * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
            return options.transforms(new RoundedCorners(px));
        }

        return options;
    }
}
