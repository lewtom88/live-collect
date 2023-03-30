package com.wy.model.query;

import java.io.Serializable;
import java.util.Map;

public class SortPaging implements Serializable {
    private Integer pageSize;
    private Integer current;
    private Map<String, String> sorter;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Map<String, String> getSorter() {
        return sorter;
    }

    public void setSorter(Map<String, String> sorter) {
        this.sorter = sorter;
    }

}
