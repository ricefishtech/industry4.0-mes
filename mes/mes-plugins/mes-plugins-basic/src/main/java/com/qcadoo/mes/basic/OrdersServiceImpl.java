package com.qcadoo.mes.basic;


import com.qcadoo.model.api.DataDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class OrdersServiceImpl implements OrdersService {


    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public String totalOrder() {

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM orders_order WHERE active=true ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);

    }


    @Override
    public String producedOrder() {

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM orders_order WHERE state='04completed' AND active=true ");

        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);

    }


    @Override
    public String shippedOrder() {

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM materialflowresources_document WHERE active=true AND masterorder_id is not null ");


        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);

    }

    @Override
    public String productionOrder() {

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM orders_order WHERE state='03inProgress' AND active=true ");


        return jdbcTemplate.queryForObject(queryBuilder.toString(), Collections.emptyMap(),String.class);

    }



}
