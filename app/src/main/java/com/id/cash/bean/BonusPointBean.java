package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/5/28.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BonusPointBean {
    private String id;
    private int point;
    private String source;
    private String note;
    private long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "BonusPointBean{" +
                "id='" + id + '\'' +
                ", point=" + point +
                ", source='" + source + '\'' +
                ", note='" + note + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
