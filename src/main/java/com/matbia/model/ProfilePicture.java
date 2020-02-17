package com.matbia.model;

import com.matbia.enums.ImageFileExtension;

import javax.persistence.*;

@Entity
public class ProfilePicture {
    @Id
    @GeneratedValue
    @Column(name = "profile_picture_id")
    private long id;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "original")
    private byte[] original;

    @Column(name = "thumbnail")
    private byte[] thumbnail;

    @Column(name = "extension")
    private ImageFileExtension extension;

    public ProfilePicture() {}

    public ProfilePicture(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public byte[] getOriginal() {
        return original;
    }

    public void setOriginal(byte[] original) {
        this.original = original;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ImageFileExtension getExtension() {
        return extension;
    }

    public void setExtension(ImageFileExtension extension) {
        this.extension = extension;
    }
}
