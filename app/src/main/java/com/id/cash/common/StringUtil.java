package com.id.cash.common;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by linchen on 2018/5/22.
 */

public class StringUtil {
    // single digit
    public static String oneDigit(float value) {
        return String.format("%0$.1f", value);
    }

    public static String concat(List<String> lines, String separator) {
        if (lines.size() > 0) {
            StringBuilder sb = new StringBuilder(lines.get(0));
            for (int i = 1; i < lines.size(); i++) {
                sb.append(separator);
                sb.append(lines.get(i));
            }

            return sb.toString();
        }
        return "";
    }

    private final static DecimalFormat dfCurrency = new DecimalFormat(",###,###");
    public static String formatCurrency(BigDecimal val) {
        if (val == null) {
            return "";
        }

        String str = dfCurrency.format(val);
        return str.replaceAll(",", ".");
    }

    public static String formatResourceString(Context context, @StringRes int formatResId, String value) {
        if (context == null) {
            return "";
        }

        String format = context.getResources().getString(formatResId);

        Object[] args = new Object[1];
        if (TextUtils.isEmpty(value)) {
            value = "--";
        }
        args[0] = value;
        return String.format(format, args);
    }

    public static String formatInterestRate(BigDecimal val) {
        if (val == null) {
            return "";
        }
        NumberFormat formatter = NumberFormat.getPercentInstance();
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        return formatter.format(val);
    }

    private final static NumberFormat plusMinusNF = new DecimalFormat("+#;-#");

    public static String formatWithSign(int val) {
        return plusMinusNF.format(val);
    }

    private final static SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-M-yyyy HH:mm:ss", Locale.getDefault());
    public static String formatServerTimeStamp(long time) {
        sdfLocal.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.setTimeInMillis(time);

        return sdfLocal.format(cal.getTime());
    }
}
