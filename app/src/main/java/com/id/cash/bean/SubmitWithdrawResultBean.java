package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Created by linchen on 2018/6/6.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmitWithdrawResultBean {
    private String id;
    private BigDecimal amount;
    private String bankCardId;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBankCardId() {
        return bankCardId;
    }

    public void setBankCardId(String bankCardId) {
        this.bankCardId = bankCardId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SubmitWithdrawResultBean{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", bankCardId='" + bankCardId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
