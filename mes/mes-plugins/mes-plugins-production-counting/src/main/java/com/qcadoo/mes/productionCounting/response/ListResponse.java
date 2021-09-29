package com.qcadoo.mes.productionCounting.response;


import java.util.List;

public class ListResponse {

    private List data;

    public ListResponse() {
        super();
    }

    public ListResponse(final List data) {
        super();
        this.data = data;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }
}
