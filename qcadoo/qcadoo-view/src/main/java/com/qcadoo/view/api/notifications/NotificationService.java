package com.qcadoo.view.api.notifications;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class NotificationService {

    @Autowired
    private List<NotificationDataComponent> notificationDataComponents;

    public NotificationContainer getNotification() {
        NotificationContainer notificationContainer = new NotificationContainer();
        List<Notification> notifications = Lists.newArrayList();
        for (NotificationDataComponent notificationDataComponent : notificationDataComponents) {
            Optional<Notification> maybeNotification = notificationDataComponent.registerNotification();
            maybeNotification.ifPresent(notifications::add);
        }
        boolean playSound = notifications.stream().filter(Notification::isSound).count() > 0;
        notificationContainer.setSound(playSound);
        notificationContainer.setNotifications(notifications);
        return notificationContainer;
    }
}
