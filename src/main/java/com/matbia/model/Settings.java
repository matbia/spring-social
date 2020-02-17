package com.matbia.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Settings {
    @Id
    @GeneratedValue
    @Column(name = "settings_id")
    private long id;

    @Column(name = "watch_notifications")
    private boolean watchNotifications;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isWatchNotifications() {
        return watchNotifications;
    }

    public void setWatchNotifications(boolean watchNotifications) {
        this.watchNotifications = watchNotifications;
    }
}
