package com.qcadoo.view.internal.alerts.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.security.api.UserService;
import com.qcadoo.view.constants.AlertFields;
import com.qcadoo.view.constants.QcadooViewConstants;
import com.qcadoo.view.constants.ViewedAlertFields;
import com.qcadoo.view.internal.alerts.model.AlertDto;

@Service
public class AlertsDbHelper {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private UserService userService;

    public void createViewedAlerts(final List<AlertDto> alerts) {
        Entity user = userService.getCurrentUserEntity();
        alerts.forEach(a -> createViewedAlert(a.getId(), user));
    }

    private void createViewedAlert(final Long id, final Entity user) {
        Entity viewedAlert = getViewdAlertDD().create();
        Entity alert = dataDefinitionService.get(QcadooViewConstants.PLUGIN_IDENTIFIER, QcadooViewConstants.MODEL_ALERT).get(id);
        viewedAlert.setField(ViewedAlertFields.USER, user);
        viewedAlert.setField(ViewedAlertFields.ALERT, alert);
        viewedAlert.getDataDefinition().save(viewedAlert);
    }

    private List<Entity> getAlertsForUser() {
        Entity user = userService.getCurrentUserEntity();

        List<Entity> result = Lists.newArrayList();
        DateTime currentDate = DateTime.now();
        if (user != null) {
            List<Entity> alerts = getAlertDD().find()
                    .add(SearchRestrictions.ge(AlertFields.EXPIRATION_DATE, currentDate.toDate())).list().getEntities();
            List<Entity> viewedAlerts = getViewdAlertDD().find()
                    .add(SearchRestrictions.belongsTo(ViewedAlertFields.USER, user)).list().getEntities();
            result.addAll(alerts.stream().filter(alert -> viewedAlerts.stream()
                    .noneMatch(va -> va.getBelongsToField(ViewedAlertFields.ALERT).getId() == alert.getId()))
                    .collect(Collectors.toList()));
        }
        return result;
    }

    public List<AlertDto> getAlerts() {
        List<AlertDto> alerts = Lists.newArrayList();
        getAlertsForUser().forEach(a -> mapToDTO(a, alerts));
        return alerts;
    }

    private void mapToDTO(Entity a, List<AlertDto> alerts) {
        AlertDto alert = new AlertDto();
        alert.setType(a.getStringField(AlertFields.TYPE));
        alert.setMessage(a.getStringField(AlertFields.MESSAGE));
        alert.setSound(a.getBooleanField(AlertFields.SOUND));
        alert.setId(a.getId());
        alerts.add(alert);
    }

    void registerAlert(final AlertDto alert) {
        Entity alertEntity = getAlertDD().create();
        alertEntity.setField(AlertFields.TYPE, alert.getType());
        alertEntity.setField(AlertFields.MESSAGE, alert.getMessage());
        alertEntity.setField(AlertFields.EXPIRATION_DATE, alert.getExpirationDate());
        alertEntity.setField(AlertFields.SOUND,alert.isSound());
        alertEntity.getDataDefinition().save(alertEntity);
    }

    private DataDefinition getAlertDD() {
        return dataDefinitionService.get(QcadooViewConstants.PLUGIN_IDENTIFIER, QcadooViewConstants.MODEL_ALERT);
    }

    private DataDefinition getViewdAlertDD() {
        return dataDefinitionService.get(QcadooViewConstants.PLUGIN_IDENTIFIER, QcadooViewConstants.MODEL_VIEWED_ALERT);
    }
}
