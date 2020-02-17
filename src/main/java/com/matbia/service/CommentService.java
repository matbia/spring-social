package com.matbia.service;

import com.matbia.model.Comment;
import com.matbia.model.Post;
import com.matbia.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public Optional<Comment> getOne(long commentId) {
        return commentRepository.findById(commentId);
    }

    public void save(Comment comment) {
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteById(long commentId) {
        try {
            commentRepository.deleteById(commentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void deleteByPost(Post post) {
        try {
            commentRepository.deleteByPost(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Comment> getForPost(Post post) {
        List<Comment> comments = commentRepository.findAllByPost(post);
        comments.sort(Comparator.comparing(Comment::getTimestamp).reversed());
        return comments;
    }

    public long getCommentsCount(Post post) {
        return commentRepository.countByPost(post);
    }

}
