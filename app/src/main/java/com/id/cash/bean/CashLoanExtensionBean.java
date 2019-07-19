package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/5/21.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CashLoanExtensionBean {
    private Long loanId;
    private String description;
    private String briefIntroduction;
    private String reviewDescription;
    private String overdueStrategy;
    private String applicationCondition;
    private String areas;
    private String icon;

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBriefIntroduction() {
        return briefIntroduction;
    }

    public void setBriefIntroduction(String briefIntroduction) {
        this.briefIntroduction = briefIntroduction;
    }

    public String getReviewDescription() {
        return reviewDescription;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }

    public String getOverdueStrategy() {
        return overdueStrategy;
    }

    public void setOverdueStrategy(String overdueStrategy) {
        this.overdueStrategy = overdueStrategy;
    }

    public String getApplicationCondition() {
        return applicationCondition;
    }

    public void setApplicationCondition(String applicationCondition) {
        this.applicationCondition = applicationCondition;
    }

    public String getAreas() {
        return areas;
    }

    public void setAreas(String areas) {
        this.areas = areas;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "CashLoanExtensionBean{" +
                "loanId=" + loanId +
                ", description='" + description + '\'' +
                ", briefIntroduction='" + briefIntroduction + '\'' +
                ", reviewDescription='" + reviewDescription + '\'' +
                ", overdueStrategy='" + overdueStrategy + '\'' +
                ", applicationCondition='" + applicationCondition + '\'' +
                ", areas='" + areas + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
