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
public class DocumentPositionsCriteriaModifier {

    public static final String LOCATION_FROM_ID = "locationFrom_id";

    public static final String LOCATION_TO_ID = "locationTo_id";

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
                scb.add(SearchRestrictions.or(SearchRestrictions.in(LOCATION_TO_ID, locationIds),
                        SearchRestrictions.in(LOCATION_FROM_ID, locationIds)));
            }
        }
    }

    private DataDefinition userDataDefinition() {
        return dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_USER);
    }
}

