package com.matbia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Post extends FeedObject {
    @OneToOne(targetEntity = PostFile.class, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "post_file_id")
    private PostFile postFile;

    @ElementCollection
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    private Set<Long> likesUserIds;

    @Column(name = "youtube_video")
    private String youtubeVideo;

    @Transient
    private int commentsCount;

    @Transient
    private MultipartFile multipartFile;

    public PostFile getPostFile() {
        return postFile;
    }

    public void setPostFile(PostFile postFile) {
        this.postFile = postFile;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public Set<Long> getLikesUserIds() {
        return likesUserIds;
    }

    public void setLikesUserIds(Set<Long> likesUserIds) {
        this.likesUserIds = likesUserIds;
    }

    public String getYoutubeVideo() {
        return youtubeVideo;
    }

    public void setYoutubeVideo(String youtubeVideo) {
        this.youtubeVideo = youtubeVideo;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
