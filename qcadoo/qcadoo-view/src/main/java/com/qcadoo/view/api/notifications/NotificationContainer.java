package com.qcadoo.view.api.notifications;

import java.util.List;

public class NotificationContainer {

    private boolean sound;

    private List<Notification> notifications;

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
