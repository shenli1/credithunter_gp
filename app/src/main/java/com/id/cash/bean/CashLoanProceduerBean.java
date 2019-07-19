package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by linchen on 2018/5/22.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CashLoanProceduerBean {
    @JsonProperty("APPLY")
    ArrayList<CashLoanProcedureStepBean> apply;

    @JsonProperty("AUDIT")
    ArrayList<CashLoanProcedureStepBean> audit;

    public ArrayList<CashLoanProcedureStepBean> getApply() {
        return apply;
    }

    public void setApply(ArrayList<CashLoanProcedureStepBean> apply) {
        this.apply = apply;
    }

    public ArrayList<CashLoanProcedureStepBean> getAudit() {
        return audit;
    }

    public void setAudit(ArrayList<CashLoanProcedureStepBean> audit) {
        this.audit = audit;
    }

    @Override
    public String toString() {
        return "CashLoanProceduerBean{" +
                "apply=" + apply +
                ", audit=" + audit +
                '}';
    }
}
