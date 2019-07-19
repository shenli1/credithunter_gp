package com.id.cash.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import com.id.cash.R;

/**
 * Created by linchen on 2018/5/19.
 */

public class ImageLoader {
    public static void loadWithDefaultIcon(Context context, String link, ImageView imageView) {
        loadWithPlaceHolder(context, R.drawable.ic_default_icon, link, imageView, 0);
    }

    public static void loadWithFeedsIcon(Context context, String link, ImageView imageView) {
        loadWithPlaceHolder(context, R.drawable.ic_feeds_placeholder, link, imageView, 0);
    }

    public static void loadWithDefaultIcon(Context context, String link, ImageView imageView, int cornerRadius) {
        loadWithPlaceHolder(context, R.drawable.ic_default_icon, link, imageView, cornerRadius);
    }

    public static void loadWithBannerPlaceHolder(Context context, String link, ImageView imageView) {
        loadWithPlaceHolder(context, R.drawable.banner_placeholder, link, imageView, 0);
    }

    private static void loadWithPlaceHolder(Context context, @DrawableRes int placeholder, String link, ImageView imageView, int cornerRadius) {
        try {
            if (!TextUtils.isEmpty(link)) {
                GlideApp.with(context)
                        .load(link)
                        .placeholder(placeholder)
                        .roundedCorners(context, cornerRadius)
                        .into(imageView);
            }
        } catch (Exception ex) {
            LogUtil.e(ex);
        }
    }
}
