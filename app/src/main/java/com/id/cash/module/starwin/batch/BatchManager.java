package com.id.cash.module.starwin.batch;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import com.id.cash.BuildConfig;

/**
 * Created by linchen on 2018/4/18.
 */

public class BatchManager {
    private static final String CACHE_FILE_NAME = "star_batch";
    private BatchType[] batchTypes = new BatchType[]{BatchType.AppList, BatchType.ContactList, BatchType.SmsList};

    public BatchManager(Context context) {
    }

    public void resetBatch(Context context, String batchId) {
        SharedPreferences sharedPreferences = getPreferences(context);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putString("batch_id", batchId);
            editor.putLong("create_time", System.currentTimeMillis());
            editor.putInt("app_version", BuildConfig.VERSION_CODE);
            editor.apply();
        }
    }

    // old entries are replaced by the new content
    public void saveContent(Context context, BatchType batchType, JSONArray data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", data);
            jsonObject.put("create_time", System.currentTimeMillis());
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        SharedPreferences sharedPreferences = getPreferences(context);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // save time
            editor.putLong("create_time", System.currentTimeMillis());
            // save data
            editor.putString(batchType.toString(), data.toString());
            editor.apply();
        }
    }

    public JSONArray peekContent(Context context, BatchType batchType, int length) {
        SharedPreferences sharedPreferences = getPreferences(context);

        if (sharedPreferences != null) {
            String entireContent = sharedPreferences.getString(batchType.toString(), null);
            JSONArray all = null;
            try {
                all = new JSONArray(entireContent);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            try {
                // return the first n elements
                if (all.length() <= length) {
                    return all;
                } else {
                    JSONArray ret = new JSONArray();
                    try {
                        for (int i = 0; i < length; ++i) {
                            JSONObject obj = all.getJSONObject(i);
                            ret.put(i, obj);
                        }

                        return ret;
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public JSONArray popContent(Context context, BatchType batchType, int length) {
        SharedPreferences sharedPreferences = getPreferences(context);

        if (sharedPreferences != null) {
            String entireContent = sharedPreferences.getString(batchType.toString(), null);
            JSONArray all = null;
            try {
                all = new JSONArray(entireContent);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            try {
                int lengthToReturn = Math.min(length, all.length());
                JSONArray ret = new JSONArray();
                for (int i = 0; i < lengthToReturn; ++i) {
                    ret.put(i, all.get(i));
                }

                JSONArray toSave = new JSONArray();
                for (int i = lengthToReturn; i < all.length(); ++i) {
                    toSave.put(i - lengthToReturn, all.get(i));
                }
                saveContent(context, batchType, toSave);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private Boolean checkVersionCodeMatch(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt("app_version", -1) == BuildConfig.VERSION_CODE;
    }

    private SharedPreferences getPreferences(Context context) {
        String name = BatchManager.CACHE_FILE_NAME + "_" + BuildConfig.VERSION_CODE;
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
