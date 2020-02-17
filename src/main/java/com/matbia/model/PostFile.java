package com.matbia.model;

import com.matbia.enums.PostFileExtension;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PostFile {
     @Id
     @GeneratedValue
     @Column(name="post_file_id")
     private long id;

     @Column(name = "filename", unique = true)
     private String filename;

     @Column(name = "file_extension")
     private PostFileExtension fileExtension;

     public long getId() {
          return id;
     }

     public void setId(long id) {
          this.id = id;
     }

     public String getFilename() {
          return filename;
     }

     public void setFilename(String filename) {
          this.filename = filename;
     }

     public PostFileExtension getFileExtension() {
          return fileExtension;
     }

     public void setFileExtension(PostFileExtension fileExtension) {
          this.fileExtension = fileExtension;
     }
}
