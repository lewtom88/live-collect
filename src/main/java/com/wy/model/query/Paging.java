package com.wy.model.query;

import java.io.Serializable;

public class Paging implements Serializable {
    private Integer pageSize;
    private Integer current;

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
}
