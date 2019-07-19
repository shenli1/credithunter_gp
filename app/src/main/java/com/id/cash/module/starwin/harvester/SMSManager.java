package com.id.cash.module.starwin.harvester;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import java.util.ArrayList;

import io.reactivex.Observable;
import com.id.cash.MainApplication;
import com.id.cash.bean.SMSBean;
import com.id.cash.common.LogUtil;
import com.id.cash.common.PrivacyPolicyUtil;

public class SMSManager {
    private final static int BATCH_SIZE = 100;

    public static Observable<ArrayList<SMSBean>> smsBatches() {
        boolean shouldCollectData = PrivacyPolicyUtil.shouldCollectPrivacyData();
        if (shouldCollectData) {
            return sms();
        } else {
            return Observable.just(new ArrayList<SMSBean>());
        }
    }

    static private Observable<ArrayList<SMSBean>> sms() {
        return Observable.create((emitter) -> {
            Cursor cursor = smsInboxCursor();
            try {
                if (cursor == null) {
                    // TODO handle error
                }
                // batched output
                ArrayList<SMSBean> arrayList = new ArrayList<>();
                while (cursor != null && cursor.moveToNext()) {
                    if (arrayList.size() == BATCH_SIZE) {
                        // upload batch
                        emitter.onNext(arrayList);
                        arrayList = new ArrayList<>();
                    }

                    SMSBean smsBean = new SMSBean();
                    String id = cursor.getString(0);
                    String threadId = cursor.getString(1);
                    String person = cursor.getString(2);
                    String address = cursor.getString(3);
                    int type = cursor.getInt(4);
                    long createTime = cursor.getLong(5);
                    String body = cursor.getString(6);
                    String subject = cursor.getString(7);

                    smsBean.setId(id);
                    smsBean.setThreadId(threadId);
                    smsBean.setPerson(person);
                    smsBean.setAddress(address);
                    smsBean.setType(type);
                    smsBean.setDateTimeStamp(createTime);
                    smsBean.setBody(body);
                    smsBean.setSubject(subject);

                    arrayList.add(smsBean);
                }
                if (arrayList.size() > 0) {
                    emitter.onNext(arrayList);
                }
                emitter.onComplete();
            } catch (Exception ex) {
                LogUtil.e(ex);
                // TODO on error
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }

    static private Cursor smsInboxCursor() {
        Context context = MainApplication.getContext();
        String sortOrder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
                Telephony.Sms.DEFAULT_SORT_ORDER : "date DESC";

        return context.getContentResolver()
                .query(Uri.parse("content://sms"),
                        smsFields(), "", null, sortOrder);
    }

    static private String[] smsFields() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return new String[]{
                    "_id",
                    Telephony.Sms.THREAD_ID,
                    Telephony.Sms.PERSON,
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.TYPE,
                    Telephony.Sms.DATE,
                    Telephony.Sms.BODY,
                    Telephony.Sms.SUBJECT
            };
        } else {
            return new String[]{
                    "_id",
                    "thread_id",
                    "person", //reference to item in {@code content://contacts/people}
                    "address", //The address of the other party.
                    "type",
                    "date",
                    "body",
                    "subject"
            };
        }
    }

    //        try{
//            Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"), null, "", null, null);
//            if (cursor != null) {
//
//            } else {
//                // no sms
//            }
//
//            JSONArray jsons = new JSONArray();
//            while (cursor.moveToNext()) {
//                boolean matchFilter = false;
//                if (fid > -1)
//                    matchFilter = fid == cursor.getInt(cursor.getColumnIndex("_id"));
//                else if (fread > -1)
//                    matchFilter = fread == cursor.getInt(cursor.getColumnIndex("read"));
//                else if (faddress.length() > 0)
//                    matchFilter = faddress.equals(cursor.getString(cursor.getColumnIndex("address")).trim());
//                else if (fcontent.length() > 0)
//                    matchFilter = fcontent.equals(cursor.getString(cursor.getColumnIndex("body")).trim());
//                else {
//                    matchFilter = true;
//                }
//                if (matchFilter)
//                {
//                    if (c >= indexFrom) {
//                        if (maxCount>0 && c >= indexFrom + maxCount) break;
//                        c++;
//                        // Long dateTime = Long.parseLong(cursor.getString(cursor.getColumnIndex("date")));
//                        // String message = cursor.getString(cursor.getColumnIndex("body"));
//                        JSONObject json;
//                        json = getJsonFromCursor(cursor);
//                        jsons.put(json);
//
//                    }
//                }
//
//            }
//            cursor.close();
//            try {
//                successCallback.invoke(c, jsons.toString());
//            } catch (Exception e) {
//                errorCallback.invoke(e.getMessage());
//            }
//        } catch (JSONException e) {
//            errorCallback.invoke(e.getMessage());
//        }

}
