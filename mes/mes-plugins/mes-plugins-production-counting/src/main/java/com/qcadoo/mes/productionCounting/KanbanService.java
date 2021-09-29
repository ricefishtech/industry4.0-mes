package com.qcadoo.mes.productionCounting;

import com.qcadoo.mes.productionCounting.dtos.ProducedOrder;
import com.qcadoo.mes.productionCounting.dtos.ProductionOrder;
import com.qcadoo.mes.productionCounting.dtos.ShippedOrder;
import com.qcadoo.mes.productionCounting.dtos.TotalPlannedProductionOrder;

import java.util.List;

public interface KanbanService {

    String totalProduct(); //产品总数

    String totalTechnology(); //工艺总数

    String totalStaff(); //工人总数

    String totalProductionLine(); //生产线总数

    String totalWarehouse(); //仓库总数

    String totalDocument(); //凭证总数

    int totalMaterial(); //原材料总数

    int totalMiddleware(); //中间件总数

    int totalFinalProduct(); //成品总数

    String totalMasterOrder(); //主订单总数

    String totalProductionOrder(); //生产订单总数

    String totalMachine(); //机器机台总数

    String totalRepairTime();//维修次数总数



    TotalPlannedProductionOrder totalPlannedProductionOrder(); //生产订单计划数量

    ProducedOrder producedOrder(); //已生产订单数量

    ShippedOrder shippedOrder();//已发货订单数量

    ProductionOrder productionOrder();//正在生产订单数量

    List getProductionOrderList();//获取实时生产订单


}
