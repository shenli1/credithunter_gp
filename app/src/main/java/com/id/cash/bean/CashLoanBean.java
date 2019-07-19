package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Created by linchen on 2018/5/21.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CashLoanBean {
    private String name;
    private String packageName;
    private String uuid;
    private String notice;
    private String tags;
    private String icon;
    private Integer score;
    private int termStep;
    private int minTerm;
    private int maxTerm;
    private BigDecimal quotaStep;
    private BigDecimal minQuota;
    private BigDecimal maxQuota;
    private BigDecimal interestRate;
    private PeriodUnit interestRateUnit;
    private String loanTimeStr;
    private String reviewDescription;
    private Integer passRate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public int getTermStep() {
        return termStep;
    }

    public void setTermStep(int termStep) {
        this.termStep = termStep;
    }

    public int getMinTerm() {
        return minTerm;
    }

    public void setMinTerm(int minTerm) {
        this.minTerm = minTerm;
    }

    public int getMaxTerm() {
        return maxTerm;
    }

    public void setMaxTerm(int maxTerm) {
        this.maxTerm = maxTerm;
    }

    public BigDecimal getQuotaStep() {
        return quotaStep;
    }

    public void setQuotaStep(BigDecimal quotaStep) {
        this.quotaStep = quotaStep;
    }

    public BigDecimal getMinQuota() {
        return minQuota;
    }

    public void setMinQuota(BigDecimal minQuota) {
        this.minQuota = minQuota;
    }

    public BigDecimal getMaxQuota() {
        return maxQuota;
    }

    public void setMaxQuota(BigDecimal maxQuota) {
        this.maxQuota = maxQuota;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public PeriodUnit getInterestRateUnit() {
        return interestRateUnit;
    }

    public void setInterestRateUnit(PeriodUnit interestRateUnit) {
        this.interestRateUnit = interestRateUnit;
    }

    public String getLoanTimeStr() {
        return loanTimeStr;
    }

    public void setLoanTimeStr(String loanTimeStr) {
        this.loanTimeStr = loanTimeStr;
    }

    public String getReviewDescription() {
        return reviewDescription;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }

    public Integer getPassRate() {
        return passRate;
    }

    public void setPassRate(Integer passRate) {
        this.passRate = passRate;
    }

    @Override
    public String toString() {
        return "CashLoanBean{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", uuid='" + uuid + '\'' +
                ", notice='" + notice + '\'' +
                ", tags='" + tags + '\'' +
                ", icon='" + icon + '\'' +
                ", score=" + score +
                ", termStep=" + termStep +
                ", minTerm=" + minTerm +
                ", maxTerm=" + maxTerm +
                ", quotaStep=" + quotaStep +
                ", minQuota=" + minQuota +
                ", maxQuota=" + maxQuota +
                ", interestRate=" + interestRate +
                ", interestRateUnit=" + interestRateUnit +
                ", loanTimeStr='" + loanTimeStr + '\'' +
                ", reviewDescription='" + reviewDescription + '\'' +
                ", passRate=" + passRate +
                '}';
    }
}
