package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by liubin on 2018/6/5.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankCardBean {
    private String id;
    private String bankName;
    private String bankAccount;
    private String userId;
    private String userName;

    public String getId() {
        return id;
    }

    public BankCardBean setId(String id) {
        this.id = id;
        return this;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "BankCardBean{" +
                "id='" + id + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}


