package com.id.cash.common;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.TextView;

import com.id.cash.MainApplication;

/**
 * Created by linchen on 2018/6/8.
 */

public class FontUtil {
    private static AssetManager assetManager;
    private static Typeface boldItalic;
    private static Typeface medium;
    private static Typeface regular;
    private static Typeface light;
    private static Typeface bold;
    private static FontUtil instance;

    private FontUtil() {
    }

    public static FontUtil getInstance() {
        if (instance == null) {
            instance = new FontUtil();
        }
        if (assetManager == null) {
            Context context = MainApplication.getContext();
            assetManager = context.getAssets();
        }
        if (boldItalic == null) {
            boldItalic = Typeface.createFromAsset(assetManager, "fonts/Roboto-BoldItalic.ttf");
        }
        if (medium == null) {
            medium = Typeface.createFromAsset(assetManager, "fonts/Roboto-Medium.ttf");
        }
        if (regular == null) {
            regular = Typeface.createFromAsset(assetManager, "fonts/Roboto-Regular.ttf");
        }
        if (bold == null) {
            bold = Typeface.createFromAsset(assetManager, "fonts/Roboto-Bold.ttf");
        }
        if (light == null) {
            light = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");
        }
        return instance;
    }

    public void boldItalic(TextView textView) {
        textView.setTypeface(boldItalic);
    }

    public void medium(TextView textView) {
        textView.setTypeface(medium);
    }

    public void regular(TextView textView) {
        textView.setTypeface(regular);
    }

    public void bold(TextView textView) {
        textView.setTypeface(bold);
    }

    public void light(TextView textView) {
        textView.setTypeface(light);
    }
}
