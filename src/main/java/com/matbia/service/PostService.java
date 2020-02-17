package com.matbia.service;

import com.matbia.misc.Utils;
import com.matbia.model.Post;
import com.matbia.model.User;
import com.matbia.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Post getOne(long postId) {
        return postRepository.getOne(postId);
    }

    public List<Post> getPageByUserIds(Set<Long> userIds, int page) {
        if(userIds.isEmpty()) return new ArrayList<>(); //Prevent SQL error
        return postRepository.findByUserIdsLimitResults(userIds, page * 10 - 10);
    }

    public void save(Post post) {
        try {
            post.setYoutubeVideo(Utils.extractYouTubeId(post.getYoutubeVideo()));

            if (post.getYoutubeVideo() != null && !post.getYoutubeVideo().isEmpty())
                post.setMultipartFile(null);

            post.setPostFile(postFileService.regiserPostFile(post.getMultipartFile()));
            postRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        postRepository.flush();
    }

    public void update(Post post) {
        try {
            postRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteByPostId(long postId) {
        try {
            postRepository.deleteById(postId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public int getTotalPageCount() {
        return (int) Math.ceil(postRepository.count() / 10.d);
    }

    public int getPageCountByUserIds(Set<Long> userIds) {
        if(userIds.isEmpty()) return 0; //Prevent SQL error
        return (int) Math.ceil(postRepository.countByUserIds(userIds) / 10.d);
    }

    public List<Post> getPage(int page) {
        return postRepository.limit(page * 10 - 10);
    }

    public List<Post> getByUser(User user) {
        return postRepository.findByUser(user);
    }

    public void deleteById(long postId) {
        postRepository.deleteById(postId);
    }

    //TODO: Prevent duplicates
    public Set<Post> findPostsContainingTag(Set<String> tags) {
        return postRepository.findByTag(tags.stream().map(String::toLowerCase).collect(Collectors.toSet()));
    }
}
