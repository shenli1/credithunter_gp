package com.id.cash.module.starwin.common.bus;

/**
 * Created by linchen on 2018/5/28.
 */

public class ShowFilterEvent {
    private String filterId;
    private String filterName;

    public String getFilterId() {
        return filterId;
    }

    public ShowFilterEvent setFilterId(String filterId) {
        this.filterId = filterId;
        return this;
    }

    public String getFilterName() {
        return filterName;
    }

    public ShowFilterEvent setFilterName(String filterName) {
        this.filterName = filterName;
        return this;
    }

    @Override
    public String toString() {
        return "ShowFilterEvent{" +
                "filterId='" + filterId + '\'' +
                ", filterName='" + filterName + '\'' +
                '}';
    }
}
