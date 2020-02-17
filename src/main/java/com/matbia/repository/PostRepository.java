package com.matbia.repository;

import com.matbia.model.Post;
import com.matbia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    void deleteByUser(User user);
    @Query(value = "SELECT * FROM post ORDER BY timestamp DESC LIMIT 10 OFFSET ?1", nativeQuery = true)
    List<Post> limit(int offset);
    @Query(value = "SELECT * FROM post WHERE user_id IN ?1 ORDER BY timestamp DESC LIMIT 10 OFFSET ?2", nativeQuery = true)
    List<Post> findByUserIdsLimitResults(Set<Long> userIds, int offset);
    @Query(value = "SELECT COUNT(*) FROM post WHERE user_id IN ?1", nativeQuery = true)
    long countByUserIds(Set<Long> userIds);
    @Query(value = "SELECT post_tags.tags FROM post_tags INNER JOIN post ON post_tags.post_id = post.id WHERE post.user_id = ?1", nativeQuery = true)
    List<String> getTagsByUserId(long userId);

    @Query(value = "SELECT * FROM post INNER JOIN post_tags ON post.id = post_tags.post_id WHERE LOWER(post_tags.tags) IN ?1", nativeQuery = true)
    Set<Post> findByTag(Set<String> tags);
}
