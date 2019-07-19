package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSBean {
    @JsonProperty("smsId")
    private String id;
    private String threadId;
    private String person;
    private String address;
    private String subject;
    private int type;
    private long dateTimeStamp;
    private String body;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(long dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "SMSBean{" +
                "id='" + id + '\'' +
                ", threadId='" + threadId + '\'' +
                ", person='" + person + '\'' +
                ", address='" + address + '\'' +
                ", subject='" + subject + '\'' +
                ", type=" + type +
                ", dateTimeStamp=" + dateTimeStamp +
                ", body='" + body + '\'' +
                '}';
    }
}
