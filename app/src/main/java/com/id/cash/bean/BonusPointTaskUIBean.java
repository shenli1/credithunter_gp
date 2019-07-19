package com.id.cash.bean;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.util.HashMap;

import com.id.cash.R;

/**
 * Created by linchen on 2018/6/5.
 */

public class BonusPointTaskUIBean {
    private static final BonusPointTaskUIBean SIGN = new BonusPointTaskUIBean(BonusPointTaskType.SIGN,
            false,
            R.drawable.ic_bonuspoint_task_sign,
            R.drawable.bonuspoint_task_sign,
            R.string.bonuspoint_title_sign,
            R.string.bonuspoint_desc_sign);
    private static final BonusPointTaskUIBean SHARE = new BonusPointTaskUIBean(BonusPointTaskType.SHARE,
            false,
            R.drawable.ic_bonuspoint_task_share,
            R.drawable.bonuspoint_task_share,
            R.string.bonuspoint_title_share,
            R.string.bonuspoint_desc_share);
    private static final BonusPointTaskUIBean INVITE = new BonusPointTaskUIBean(BonusPointTaskType.INVITE,
            false,
            R.drawable.ic_bonuspoint_task_invite,
            R.drawable.bonuspoint_task_invite,
            R.string.bonuspoint_title_invite,
            R.string.bonuspoint_desc_invite);
    private static final BonusPointTaskUIBean BORROWED = new BonusPointTaskUIBean(BonusPointTaskType.BORROWED,
            false,
            R.drawable.ic_bonuspoint_task_borrowed,
            R.drawable.bonuspoint_task_borrowed,
            R.string.bonuspoint_title_borrowed,
            R.string.bonuspoint_desc_borrowed);
    private static final BonusPointTaskUIBean REGISTERED = new BonusPointTaskUIBean(BonusPointTaskType.REGISTERED,
            false,
            R.drawable.ic_bonuspoint_task_registered,
            R.drawable.bonuspoint_task_registered,
            R.string.bonuspoint_title_registered,
            R.string.bonuspoint_desc_registered);

    public static final HashMap<String, BonusPointTaskUIBean> bonusPointTaskTypes = new HashMap<String, BonusPointTaskUIBean>() {
        {
            put(BonusPointTaskType.SIGN, SIGN);
            put(BonusPointTaskType.SHARE, SHARE);
            put(BonusPointTaskType.INVITE, INVITE);
            put(BonusPointTaskType.REGISTERED, REGISTERED);
            put(BonusPointTaskType.BORROWED, BORROWED);
        }
    };
    private boolean status;
    private String templateId;
    private @DrawableRes
    int icon;
    private @DrawableRes
    int type;
    private @StringRes
    int title;
    private @StringRes
    int desc;

    public BonusPointTaskUIBean(String templateId, boolean status, @DrawableRes int icon, @DrawableRes int type, @StringRes int title, @StringRes int desc) {
        this.status = status;
        this.templateId = templateId;
        this.icon = icon;
        this.type = type;
        this.title = title;
        this.desc = desc;
    }

    public String getTemplateId() {
        return templateId;
    }

    public @DrawableRes
    int getIcon() {
        return icon;
    }

    public @DrawableRes
    int getType() {
        return type;
    }

    public @StringRes
    int getTitle() {
        return title;
    }

    public @StringRes
    int getDesc() {
        return desc;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BonusPointTaskUIBean{" +
                "status=" + status +
                ", templateId='" + templateId + '\'' +
                ", icon=" + icon +
                ", type=" + type +
                ", title=" + title +
                ", desc=" + desc +
                '}';
    }

    public interface BonusPointTaskType {
        String SIGN = "10000";
        String SHARE = "10001";
        String INVITE = "10002";
        String REGISTERED = "10003";
        String BORROWED = "10004";
    }
}
