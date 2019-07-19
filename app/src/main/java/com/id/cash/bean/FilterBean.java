package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by linchen on 2018/6/3.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterBean {
    private String name;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FilterBean{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
