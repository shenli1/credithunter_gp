package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by linchen on 2018/5/21.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CashLoanDetailBean {
    @JsonProperty("loan")
    private CashLoanBean loan;
    @JsonProperty("loanExtension")
    private CashLoanExtensionBean loanExtension;
    @JsonProperty("platform")
    private PlatformBean platform;

    public CashLoanBean getLoan() {
        return loan;
    }

    public void setLoan(CashLoanBean loan) {
        this.loan = loan;
    }

    public CashLoanExtensionBean getLoanExtension() {
        return loanExtension;
    }

    public void setLoanExtension(CashLoanExtensionBean loanExtension) {
        this.loanExtension = loanExtension;
    }

    public PlatformBean getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformBean platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "CashLoanDetailBean{" +
                "loan=" + loan +
                ", loanExtension=" + loanExtension +
                ", platform=" + platform +
                '}';
    }
}
