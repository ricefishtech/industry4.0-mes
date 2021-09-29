package com.qcadoo.mes.productionCounting.dtos;

public class TotalPlannedProductionOrder {

    private int quantity; //数量

    private int percentage; //百分比


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
