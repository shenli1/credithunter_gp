package com.id.cash.module.starwin.sns;

import android.net.Uri;
import android.text.TextUtils;

import com.facebook.share.model.ShareLinkContent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.id.cash.common.LogUtil;

/**
 * Created by linchen on 2018/5/12.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareContentDTO {
    @JsonProperty("link")
    private String link;

    @JsonIgnore
    ShareLinkContent getFBShareLinkContent() {
        ShareLinkContent.Builder builder = new ShareLinkContent.Builder();

        if (!TextUtils.isEmpty(getLink())) {
            return builder.setContentUrl(Uri.parse(getLink()))
                    .build();
        } else {
            LogUtil.e("empty link parameter");
            return null;
        }
    }

    public String getLink() {
        return link;
    }

    public ShareContentDTO setLink(String link) {
        this.link = link;
        return this;
    }

    @Override
    public String toString() {
        return "ShareContentDTO{" +
                "link='" + link + '\'' +
                '}';
    }
}
