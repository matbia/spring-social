package com.matbia.service;

import com.matbia.enums.Gender;
import com.matbia.enums.PostFileExtension;
import com.matbia.model.Post;
import com.matbia.model.PostFile;
import com.matbia.model.Settings;
import com.matbia.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private PostService postService;

    private User user;
    private Post post;

    @Before
    public void setup() {
        user = new User();
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setGender(Gender.MALE);
        user.setPassword("a1b2c3d4");
        user.setEmail("user@example.com");
        user.setSettings(new Settings());

        PostFile postFile = new PostFile();
        postFile.setFileExtension(PostFileExtension.PNG);
        postFile.setFilename("file");

        post = new Post();
        post.setUser(user);
        post.setPostFile(postFile);
        post.setTags(new HashSet<>(Arrays.asList("test tag1", "test tag2", "testtag3")));
        post.setYoutubeVideo("");
        post.setMessage("Example message");

        postService.save(post);
        postService.flush();
    }

    @Test
    @Order(1)
    public void getOne() {
        Post post = postService.getOne(this.post.getId());
        assertNotNull(post);
        assertEquals(this.post.getUser().getEmail(), post.getUser().getEmail());
        assertEquals(this.post.getMessage(), post.getMessage());
        assertEquals(this.post.getPostFile().getFilename(), post.getPostFile().getFilename());
        assertTrue(post.getTags().contains("test tag1"));
        assertTrue(post.getTags().contains("testtag3"));
    }

    @Test
    @Order(2)
    public void getTotalPageCount() {
        long pagesCount = postService.getPageCountByUserIds(new HashSet<>(Collections.singletonList(user.getId())));
        assertEquals(1, pagesCount);
    }

    @Test
    @Order(3)
    public void findPostsContainingTag() {
        Set<String> tags = new HashSet<>(Arrays.asList("testtag3", "test tag1"));
        Set<Post> posts = postService.findPostsContainingTag(tags);
        assertEquals(1, posts.size());

        tags = new HashSet<>(Collections.singletonList("test tag2"));
        posts = postService.findPostsContainingTag(tags);
        assertEquals(1, posts.size());
    }

    @Test
    @Order(4)
    public void getPage() {
        List<Post> page = postService.getPage(1);
        assertTrue(page.size() > 0);
        assertTrue(page.size() <= 10);
    }

    @Test
    @Order(5)
    public void getByUser() {
        List<Post> posts = postService.getByUser(this.user);
        assertTrue(posts.size() > 0);
        Post post = posts.get(0);

        assertNotNull(post);
        assertEquals(this.post.getUser().getEmail(), post.getUser().getEmail());
        assertEquals(this.post.getMessage(), post.getMessage());
        assertTrue(post.getTags().contains("test tag1"));
        assertTrue(post.getTags().contains("testtag3"));
    }

    @Test
    @Order(6)
    public void getPageCountByUserIds() {
        long pageCount = postService.getPageCountByUserIds(new HashSet<>(Collections.singletonList(this.user.getId())));
        assertEquals(pageCount, 1);
    }

    @Test
    @Order(7)
    public void update() {
        String newTag = "new tag";
        post.setMessage("Updated message.");
        post.getTags().add(newTag);
        postService.update(post);

        Post found = postService.getOne(this.post.getId());

        assertEquals(post.getMessage(), found.getMessage());
        assertTrue(found.getTags().contains(newTag));
    }

    @Test
    @Order(8)
    public void deleteByUser() {
        postService.deleteByUser(this.user);
    }
}