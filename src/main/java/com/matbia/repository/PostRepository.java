package com.matbia.repository;

import com.matbia.model.Post;
import com.matbia.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findByOrderByTimestampDesc(Pageable pageable);
    void deleteByUser(User user);
    List<Post> findByUserIdInOrderByTimestampDesc(Set<Long> userIds, Pageable pageable);
    long countByUserId(long userId);
    @Query(value = "SELECT post_tags.tags FROM post_tags INNER JOIN post ON post_tags.post_id = post.id WHERE post.user_id = ?1", nativeQuery = true)
    List<String> getTagsByUserId(long userId);
    List<Post> findDistinctByTagsInOrderByTimestampDesc(Set<String> tags);
}
