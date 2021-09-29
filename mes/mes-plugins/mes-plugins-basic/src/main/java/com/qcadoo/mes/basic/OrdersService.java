package com.qcadoo.mes.basic;

import com.qcadoo.model.api.Entity;

import java.util.List;

public interface OrdersService {

    String totalOrder(); //订单总数

    String producedOrder();//已生产的订单

    String shippedOrder(); //已发货的订单

    String productionOrder();//生产中的订单

}
