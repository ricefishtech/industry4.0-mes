package com.qcadoo.mes.productionCounting;


import com.qcadoo.mes.productionCounting.dtos.ProducedOrder;
import com.qcadoo.mes.productionCounting.dtos.ProductionOrder;
import com.qcadoo.mes.productionCounting.dtos.ShippedOrder;
import com.qcadoo.mes.productionCounting.dtos.TotalPlannedProductionOrder;
import com.qcadoo.model.api.DataDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class KanbanServiceImpl implements KanbanService {


    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public String totalProduct() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM basic_product WHERE globaltypeofmaterial is not null AND active=true ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }

    @Override
    public String totalTechnology() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM technologies_technology WHERE state='02accepted' AND active=true ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }

    @Override
    public String totalStaff() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM basic_staff WHERE active=true ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }

    @Override
    public String totalProductionLine() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM productionlines_productionline WHERE active=true ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }

    @Override
    public String totalWarehouse() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM materialflow_location ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }

    @Override
    public String totalDocument() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM materialflowresources_document WHERE state='02accepted' AND active=true ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }

    @Override
    public int totalMaterial() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT SUM(quantity) FROM materialflowresources_resourcestockdto as resource " +
                "LEFT JOIN basic_product as product ON resource.product_id = product.id " +
                "WHERE product.globaltypeofmaterial='01component' ");

        String quantity = jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);

        if("".equals(quantity) || quantity == null) {
            quantity = "0";
        }
        return Double.valueOf(quantity).intValue();
    }

    @Override
    public int totalMiddleware() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT SUM(quantity) FROM materialflowresources_resourcestockdto as resource " +
                "LEFT JOIN basic_product as product ON resource.product_id = product.id " +
                "WHERE product.globaltypeofmaterial='02intermediate' ");

        String quantity = jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);

        if("".equals(quantity) || quantity == null) {
            quantity = "0";
        }
        return Double.valueOf(quantity).intValue();
    }

    @Override
    public int totalFinalProduct() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT SUM(quantity) FROM materialflowresources_resourcestockdto as resource " +
                "LEFT JOIN basic_product as product ON resource.product_id = product.id " +
                "WHERE product.globaltypeofmaterial='03finalProduct' ");

        String quantity = jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);

        if("".equals(quantity) || quantity == null) {
            quantity = "0";
        }
        return Double.valueOf(quantity).intValue();
    }

    @Override
    public String totalMasterOrder() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM masterorders_masterorder WHERE active=true ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }

    @Override
    public String totalProductionOrder() {

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM orders_order WHERE active=true");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }

    @Override
    public String totalMachine() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM basic_product where globaltypeofmaterial is null AND active=true ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }

    @Override
    public String totalRepairTime() {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM cmmsmachineparts_maintenanceevent ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
    }




    @Override
    public TotalPlannedProductionOrder totalPlannedProductionOrder() {

        TotalPlannedProductionOrder totalPlannedProductionOrder = new TotalPlannedProductionOrder();
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT SUM(plannedquantity) FROM orders_order WHERE active=true ");

        String plannedquantity = jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);

        if("".equals(plannedquantity) || plannedquantity == null) {
            plannedquantity = "0";
        }

        totalPlannedProductionOrder.setQuantity(Double.valueOf(plannedquantity).intValue());


        return totalPlannedProductionOrder;

    }


    @Override
    public ProducedOrder producedOrder() {

        ProducedOrder producedOrder = new ProducedOrder();
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT SUM(donequantity) FROM orders_order WHERE active=true ");

        String donequantity = jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
        if("".equals(donequantity) || donequantity == null) {
            donequantity = "0";
        }

        int plannedquantity = totalPlannedProductionOrder().getQuantity();

        producedOrder.setQuantity(Double.valueOf(donequantity).intValue());
        producedOrder.setPercentage(Double.valueOf(Double.valueOf(donequantity) / (double)plannedquantity * 100).intValue());

        return producedOrder;

    }


    @Override
    public ShippedOrder shippedOrder() {
        ShippedOrder shippedOrder = new ShippedOrder();

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT SUM(givenquantity) FROM materialflowresources_document as document " +
                "LEFT JOIN materialflowresources_position as position ON position.document_id = document.id " +
                "WHERE active=true AND document.masterorder_id is not null ");

        String givenquantity = jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
        if("".equals(givenquantity) || givenquantity == null) {
            givenquantity = "0";
        }

        int plannedquantity = totalPlannedProductionOrder().getQuantity();

        shippedOrder.setQuantity(Double.valueOf(givenquantity).intValue());
        shippedOrder.setPercentage(Double.valueOf(Double.valueOf(givenquantity) / (double)plannedquantity * 100).intValue());

        return shippedOrder;

    }

    @Override
    public ProductionOrder productionOrder() {

        ProductionOrder productionOrder = new ProductionOrder();
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT SUM(plannedquantity) FROM orders_order WHERE state='03inProgress' AND active=true ");

        String quantity = jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);
        if("".equals(quantity) || quantity == null) {
            quantity = "0";
        }

        int plannedquantity = totalPlannedProductionOrder().getQuantity();

        productionOrder.setQuantity(Double.valueOf(quantity).intValue());
        productionOrder.setPercentage(Double.valueOf(Double.valueOf(quantity) / (double)plannedquantity * 100).intValue());

        return productionOrder;

    }

    @Override
    public List getProductionOrderList() {

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT ordernumber,productnumber,timerangefrom,timerangeto,usedquantity FROM productioncounting_productiontrackingforproductdto ");

        List list = jdbcTemplate.queryForList(queryBuilder.toString(), Collections.emptyMap());

        return list;
    }

}
