package com.matbia.service;

import com.matbia.misc.Utils;
import com.matbia.model.Post;
import com.matbia.model.User;
import com.matbia.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostFileService postFileService;

    /**
     * Fetches a single post by ID
     * @param postId primary key
     * @return post with given ID, or null if no record was found
     */
    public Post getOne(long postId) {
        return postRepository.getOne(postId);
    }

    /**
     * Fetches a page containing posts in descending order by timestamp made by any of the specified users
     * @param userIds user primary keys
     * @param page page number
     * @return collection containing max 10 posts
     */
    public List<Post> getPageByUserIds(Set<Long> userIds, int page) {
        if(userIds.isEmpty()) return new ArrayList<>(); //Prevent SQL error
        return postRepository.findByUserIdInOrderByTimestampDesc(userIds, PageRequest.of(page - 1, 10));
    }

    /**
     * Replaces youtubeVideo field with video ID extracted from current field value
     * If youtubeVideo field contants a valid video URL, sets the multipartFile field to null
     * Persists specified post
     * @See com.matbia.misc.Utils#extractYouTubeId(String)
     * @See com.matbia.service.PostFileService#save(PostFile)
     * @param post
     */
    public void save(Post post) {
        try {
            post.setYoutubeVideo(Utils.extractYouTubeId(post.getYoutubeVideo()));

            if (!post.getYoutubeVideo().isEmpty())
                post.setMultipartFile(null);

            post.setPostFile(postFileService.registerPostFile(post.getMultipartFile()));
            postRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Empties the internal SQL instructions cache, and executes it immediately to the database
     */
    public void flush() {
        postRepository.flush();
    }

    /**
     * Directly persists specified post to the database without any alteration
     * @param post instance with ID field same as any existing post
     */
    public void update(Post post) {
        try {
            postRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes single post with given ID
     * @param postId primary key
     */
    public void deleteByPostId(long postId) {
        try {
            postRepository.deleteById(postId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all posts made by the specified user
     * @param user specified user instance
     */
    public void deleteByUser(User user) {
        try {
            /*postRepository.findByUser(user).forEach(post -> {
                if (post.getPostFile() != null) postFileService.delete(post.getPostFile());
                postRepository.delete(post);
            });*/
            postRepository.deleteByUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Counts number of pages in total
     * @return number of currently available pages
     */
    public int getTotalPageCount() {
        return (int) Math.ceil(postRepository.count() / 10.d);
    }

    /**
     * Counts number of pages based on the number of posts made by all of the given users
     * @param userIds user's primary keys
     * @return number of available pages
     */
    public int getPageCountByUserIds(Set<Long> userIds) {
        long postCount = userIds.stream().mapToLong(postRepository::countByUserId).sum();
        if(userIds.isEmpty()) return 0; //Prevent SQL error
        return (int) Math.ceil(postCount / 10.d);
    }

    /**
     * Fetches a page of posts ordered descendingly by timestamp
     * @param page page number
     * @return collection containing no more than 10 posts
     */
    public List<Post> getPage(int page) {
        return postRepository.findByOrderByTimestampDesc(PageRequest.of(page - 1, 10));
    }

    /**
     * Finds all posts created by the given user
     * @param user specified user instance
     * @return posts found
     */
    public List<Post> getByUser(User user) {
        return postRepository.findByUser(user);
    }


    /**
     * Deletes a single post with given ID
     * @param postId primary key
     */
    public void deleteById(long postId) {
        postRepository.deleteById(postId);
    }

    /**
     * Finds posts containing at least one tag from the given set
     * @param tags set of tags
     * @return found posts
     */
    public List<Post> findPostsContainingTags(Set<String> tags) {
        return postRepository.findDistinctByTagsInOrderByTimestampDesc(tags);
    }
}
