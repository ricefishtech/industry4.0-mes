package com.qcadoo.mes.materialFlowResources.criteriaModifiers;

/**
 * Created by alex on 2017/6/4.
 */
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.materialFlow.constants.UserFieldsMF;
import com.qcadoo.mes.materialFlow.constants.UserLocationFields;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.EntityList;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.security.api.SecurityService;
import com.qcadoo.security.constants.QcadooSecurityConstants;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;

@Service
public class WarehouseStocksCriteriaModifier {

    public static final String LOCATION_ID = "location_id";

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void restrictToUserLocations(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long currentUserId = securityService.getCurrentUserId();
        if (Objects.nonNull(currentUserId)) {
            EntityList userLocations = userDataDefinition().get(currentUserId).getHasManyField(UserFieldsMF.USER_LOCATIONS);
            if (!userLocations.isEmpty()) {
                Set<Integer> locationIds = userLocations.stream().map(ul -> ul.getBelongsToField(UserLocationFields.LOCATION))
                        .mapToInt(e -> e.getId().intValue()).boxed().collect(Collectors.toSet());
                scb.add(SearchRestrictions.in(LOCATION_ID, locationIds));
            }
        }
    }

    private DataDefinition userDataDefinition() {
        return dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_USER);
    }
}

