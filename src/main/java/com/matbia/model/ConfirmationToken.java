package com.matbia.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class ConfirmationToken {
     @Id
     @GeneratedValue
     @Column(name="token_id")
     private long id;

     @Column(name="token", unique = true)
     private String token;

     @CreationTimestamp
     private Date timestamp;

     @OneToOne(targetEntity = User.class)
     @JoinColumn(nullable = false, unique = true, name = "user_id")
     private User user;

     @PrePersist
     public void generateToken() {
          token = UUID.randomUUID().toString();
     }

     public long getId() {
          return id;
     }

     public void setId(long id) {
          this.id = id;
     }

     public String getToken() {
          return token;
     }

     public void setToken(String token) {
          this.token = token;
     }

     public Date getTimestamp() {
          return timestamp;
     }

     public void setTimestamp(Date timestamp) {
          this.timestamp = timestamp;
     }

     public User getUser() {
          return user;
     }

     public void setUser(User user) {
          this.user = user;
     }
}
