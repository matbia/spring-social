package com.matbia.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ChatMessage {
     @Id
     @GeneratedValue
     private long id;

     @CreationTimestamp
     private Date timestamp;

     @Column(name = "message")
     private String message;

     @OneToOne(targetEntity = User.class)
     @JoinColumn(nullable = false, name = "user_from_id")
     private User userFrom;

     @OneToOne(targetEntity = User.class)
     @JoinColumn(nullable = false, name = "user_to_id")
     private User userTo;

     public ChatMessage() {}

     public ChatMessage(User userFrom, User userTo, String message) {
          this.userFrom = userFrom;
          this.userTo = userTo;
          this.message = message;
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

     public String getMessage() {
          return message;
     }

     public void setMessage(String message) {
          this.message = message;
     }

     public User getUserFrom() {
          return userFrom;
     }

     public void setUserFrom(User userFrom) {
          this.userFrom = userFrom;
     }

     public User getUserTo() {
          return userTo;
     }

     public void setUserTo(User userTo) {
          this.userTo = userTo;
     }
}
