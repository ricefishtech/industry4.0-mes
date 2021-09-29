package com.qcadoo.view.api.notifications;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ViewNotificationDataComponent implements NotificationDataComponent {

    public Optional<Notification> registerNotification() {
        return Optional.empty();
    }

}
