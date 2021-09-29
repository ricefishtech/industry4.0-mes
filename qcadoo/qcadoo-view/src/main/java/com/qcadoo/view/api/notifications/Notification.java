package com.qcadoo.view.api.notifications;

public class Notification {

    private final String message;

    private final NotificationType type;

    private final boolean sound;

    private final boolean autoClose;

    public Notification(final NotificationType type, final String message) {
        this.type = type;
        this.message = message;
        this.sound = false;
        this.autoClose = true;
    }

    public Notification(final NotificationType type, final String message, boolean autoClose) {
        this.type = type;
        this.message = message;
        this.sound = false;
        this.autoClose = autoClose;
    }

    public Notification(final NotificationType type, final String message, boolean sound, boolean autoClose) {
        this.type = type;
        this.message = message;
        this.sound = sound;
        this.autoClose = autoClose;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isSound() {
        return sound;
    }

    public boolean isAutoClose() {
        return autoClose;
    }
}
