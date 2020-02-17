package com.matbia.repository;

import com.matbia.model.Comment;
import com.matbia.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByPost(Post post);
    List<Comment> findAllByPost(Post post);
    Long countByPost(Post post);
}
