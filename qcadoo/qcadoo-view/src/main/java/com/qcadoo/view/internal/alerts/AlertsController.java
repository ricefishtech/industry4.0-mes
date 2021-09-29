package com.qcadoo.view.internal.alerts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qcadoo.view.api.notifications.NotificationContainer;
import com.qcadoo.view.api.notifications.NotificationService;
import com.qcadoo.view.internal.alerts.model.AlertDto;
import com.qcadoo.view.internal.alerts.utils.AlertsDbHelper;
import com.qcadoo.view.internal.alerts.utils.NotificationFetcher;

@Controller
@RequestMapping("/alert")
public class AlertsController {

    @Autowired
    private AlertsDbHelper alertsDbHelper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationFetcher notificationFetcher;

    @ResponseBody
    @RequestMapping(value = "/systemNotifications", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public NotificationContainer getSystemNotifications() {
        return notificationService.getNotification();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AlertDto> getAlert() {
        notificationFetcher.fetch();
        List<AlertDto> alerts = alertsDbHelper.getAlerts();
        alertsDbHelper.createViewedAlerts(alerts);
        return alerts;
    }
}
