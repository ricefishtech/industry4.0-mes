
package com.qcadoo.mes.technologies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.qcadoo.mes.technologies.constants.BarcodeOperationComponentFields;
import com.qcadoo.mes.technologies.constants.TechnologiesConstants;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchRestrictions;

import java.util.Collections;
import java.util.List;

@Service
public class BarcodeOperationComponentService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void createBarcodeOperationComponent(Entity order, final Entity operationComponent) {

        if (!checkIfBarcodeExist(order, operationComponent)) {
            Entity barcodeOCEntity = getBarcodeOperationComponentDD().create();
            barcodeOCEntity.setField(BarcodeOperationComponentFields.OPERATION_COMPONENT, operationComponent);
            barcodeOCEntity.setField("order", order);
            Long number = jdbcTemplate.queryForObject("select nextval('technologies_barcodeoperationcomponent_number_seq')",
                    Collections.emptyMap(), Long.class);
            barcodeOCEntity.setField(BarcodeOperationComponentFields.CODE, number.toString());
            barcodeOCEntity.getDataDefinition().save(barcodeOCEntity);
        }
    }

    private boolean checkIfBarcodeExist(final Entity order, final Entity operationComponent) {
        if (getBarcodeOperationComponentDD().find()
                .add(SearchRestrictions.belongsTo(BarcodeOperationComponentFields.OPERATION_COMPONENT, operationComponent))
                .add(SearchRestrictions.belongsTo(BarcodeOperationComponentFields.ORDER, order)).list()
                .getEntities().isEmpty()) {
            return false;
        }
        return true;
    }

    private DataDefinition getBarcodeOperationComponentDD() {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER,
                TechnologiesConstants.MODEL_BARCODE_OPERATION_COMPONENT);
    }

    public String getCodeFromBarcode(final Entity order, final Entity operationComponent) {
        Entity barcode = getBarcodeOperationComponentDD().find()
                .add(SearchRestrictions.belongsTo(BarcodeOperationComponentFields.OPERATION_COMPONENT, operationComponent))
                .add(SearchRestrictions.belongsTo(BarcodeOperationComponentFields.ORDER, order))
                .setMaxResults(1).uniqueResult();
        return barcode.getStringField(BarcodeOperationComponentFields.CODE);
    }

    public Optional<String> findBarcode(final Entity order, final Entity operationComponent) {
        Entity barcode = getBarcodeOperationComponentDD().find()
                .add(SearchRestrictions.belongsTo(BarcodeOperationComponentFields.OPERATION_COMPONENT, operationComponent))
                .add(SearchRestrictions.belongsTo(BarcodeOperationComponentFields.ORDER, order))
                .setMaxResults(1).uniqueResult();
        if(barcode == null) {
            return Optional.absent();
        }
        return Optional.of(barcode.getStringField(BarcodeOperationComponentFields.CODE));
    }

    public Optional<Entity> getOperationComponentForBarcode(final String code) {
        Entity barcode = getBarcodeOperationComponentDD().find()
                .add(SearchRestrictions.eq(BarcodeOperationComponentFields.CODE, code)).setMaxResults(1).uniqueResult();
        if (barcode == null) {
            return Optional.absent();
        }
        return Optional.fromNullable(barcode.getBelongsToField(BarcodeOperationComponentFields.OPERATION_COMPONENT));
    }

    public void removeBarcode(final Entity order) {
        List<Entity> barcodes = getBarcodeOperationComponentDD().find()
                .add(SearchRestrictions.belongsTo(BarcodeOperationComponentFields.ORDER, order)).list()
                .getEntities();
        for(Entity code : barcodes) {
            getBarcodeOperationComponentDD().delete(code.getId());
        }
    }
}
