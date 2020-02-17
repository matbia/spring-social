package com.matbia.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends Person {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private long id;

    @Column(name="timestamp", nullable = false, updatable = false)
    @CreationTimestamp
    private Date timestamp;

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "Field must contain a valid e-mail address")
    @NotEmpty(message = "Field cannot be empty")
    private String email;

    @Column(name = "password", nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @ElementCollection
    @Column(name = "watched_users_ids")
    private Set<Long> watchedUsersIds = new HashSet<>();

    @ElementCollection
    @Column(name = "blocked_users_ids")
    private Set<Long> blockedUsersIds = new HashSet<>();

    @OneToOne(targetEntity = Settings.class, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, unique = true, name = "settings_id")
    private Settings settings;

    @Transient
    private boolean online;

    public void setPerson(Person person) {
        this.setFirstName(person.getFirstName());
        this.setLastName(person.getLastName());
        this.setGender(person.getGender());
        this.setBirthday(person.getBirthday());
        this.setContactEmail(person.getContactEmail());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
        this.setContactEmail(email.toLowerCase());
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Long> getWatchedUsersIds() {
        return watchedUsersIds;
    }

    public void setWatchedUsersIds(Set<Long> watchedUsersIds) {
        this.watchedUsersIds = watchedUsersIds;
    }

    public Set<Long> getBlockedUsersIds() {
        return blockedUsersIds;
    }

    public void setBlockedUsersIds(Set<Long> blockedUsersIds) {
        this.blockedUsersIds = blockedUsersIds;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
