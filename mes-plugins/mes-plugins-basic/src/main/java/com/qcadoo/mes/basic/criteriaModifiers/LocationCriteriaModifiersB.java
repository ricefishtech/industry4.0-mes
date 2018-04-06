package com.qcadoo.mes.basic.criteriaModifiers;

/**
 * Created by alex on 2017/6/4.
 */
import org.springframework.stereotype.Service;

import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;

@Service
public class LocationCriteriaModifiersB {

    public void showWarehousesOnly(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.eq("type", "02warehouse"));
    }

}
