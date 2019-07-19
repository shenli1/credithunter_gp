package com.id.cash.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by linchen on 2018/5/28.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListResultBean<T> {
    int current;
    int pages;
    int size;
    int total;
    T[] records;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public T[] getRecords() {
        return records;
    }

    public void setRecords(T[] records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "ListResultBean{" +
                "current=" + current +
                ", pages=" + pages +
                ", size=" + size +
                ", total=" + total +
                ", records=" + Arrays.toString(records) +
                '}';
    }
}
