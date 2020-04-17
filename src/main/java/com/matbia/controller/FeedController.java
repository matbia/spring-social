package com.matbia.controller;

import com.matbia.exception.ObjectNotFoundException;
import com.matbia.model.Comment;
import com.matbia.model.Post;
import com.matbia.model.User;
import com.matbia.service.CommentService;
import com.matbia.service.PostService;
import com.matbia.service.RoleService;
import com.matbia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("feed")
public class FeedController {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @GetMapping("dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("currUser", userService.getCurrent());
        return "feed/index";
    }

    /* Page count routes */

    @ResponseBody
    @GetMapping("pageCount/new")
    public int pageCountTotal() {
        return postService.getTotalPageCount();
    }

    @ResponseBody
    @GetMapping("pageCount/watched")
    public int pageCountWatched() {
        return postService.getPageCountByUserIds(userService.getCurrent().getWatchedUsersIds());
    }

    /* Read routes */

    @GetMapping("new/{page}")
    public String getPage(@PathVariable("page") int pageId, Model model) {
        List<Post> posts = postService.getPage(pageId);
        posts.forEach(post -> post.setCommentsCount((int) commentService.getCommentsCount(post)));
        model.addAttribute("posts", posts);
        model.addAttribute("currUser", userService.getCurrent());
        return "feed/posts";
    }

    @GetMapping("watched/{page}")
    public String getPostsFromWatchedUsers(@PathVariable("page") int page, Model model) {
        List<Post> posts = postService.getPageByUserIds(userService.getCurrent().getWatchedUsersIds(), page);
        posts.forEach(post -> post.setCommentsCount((int) commentService.getCommentsCount(post)));
        model.addAttribute("posts", posts);
        model.addAttribute("currUser", userService.getCurrent());
        return "feed/posts";
    }

    @GetMapping("search")
    public String searchPostsByTags(@RequestParam("tags") String[] tags, Model model) {
        model.addAttribute("posts", postService.findPostsContainingTags(new HashSet<>(Arrays.asList(tags))));
        model.addAttribute("currUser", userService.getCurrent());
        return "feed/posts";
    }

    @GetMapping("user/{id}")
    public String getPostsByUser(@PathVariable("id") long userId, Model model) {
        Optional<User> user = userService.getOne(userId);
        if(!user.isPresent()) throw new ObjectNotFoundException();

        List<Post> posts = postService.getByUser(user.get());
        posts.sort(Comparator.comparing(Post::getTimestamp).reversed());
        posts.forEach(post -> post.setCommentsCount((int) commentService.getCommentsCount(post)));
        model.addAttribute("posts", posts);
        model.addAttribute("currUser", userService.getCurrent());
        return "feed/posts";
    }

    @GetMapping("comments/{postId}")
    public String getCommentsForPost(@PathVariable("postId") long postId, Model model) {
        model.addAttribute("comments", commentService.getForPost(postService.getOne(postId)));
        model.addAttribute("currUser", userService.getCurrent());
        return "feed/comments";
    }

    /* Save routes */

    @PostMapping("post/save")
    public String savePost(@Valid Post post, @RequestParam(value = "tags") String tagsStr, BindingResult result) {
        if(result.hasErrors()) return "feed/index";

        //Parse tags
        Arrays.stream(tagsStr.split(","))
                .map(s -> s.trim().replaceAll("\\s{2,}|\\t", " "))
                .filter(s -> !s.isEmpty())
                .forEach(s -> post.getTags().add(s));

        post.setUser(userService.getCurrent());
        postService.save(post);
        return "redirect:/feed/dashboard";
    }


    @PostMapping("comment/save/{postId}")
    public ResponseEntity<HttpStatus> commentOnPost(@PathVariable("postId") long postId, @RequestParam("message") String message) {
        Post post = postService.getOne(postId);
        User currUser = userService.getCurrent();

        if(post == null) throw new ObjectNotFoundException();
        if(post.getUser().getBlockedUsersIds().contains(currUser.getId())) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        commentService.save(new Comment(currUser, post, message));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("like/{postId}")
    public ResponseEntity<HttpStatus> likePost(@PathVariable("postId") long postId) {
        Post post = postService.getOne(postId);
        User currUser = userService.getCurrent();

        //If user has already liked selected post
        if(post.getLikesUserIds().contains(currUser.getId()))
            post.getLikesUserIds().removeIf(userId -> userId == currUser.getId()); //Remove like from this user
        else
            post.getLikesUserIds().add(currUser.getId()); //Add a new like

        postService.update(post);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /* Update routes */

    @PostMapping("post/update/{id}")
    public ResponseEntity<HttpStatus> updatePost(@PathVariable("id") long id,
                                     @RequestParam(value = "msg", required = false) String msg,
                                     @RequestParam(value = "tags", required = false) String tags) {
        Post post = postService.getOne(id);
        if (post == null) throw new ObjectNotFoundException();
        if(userService.getCurrent().getId() != post.getUser().getId()) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Set<String> tagsSet = tags != null && !tags.isEmpty() ?
                Arrays.stream(tags.split(",")).map(t -> t.trim().toUpperCase().replace(" ", "_")).collect(Collectors.toSet()) : new HashSet<>();
        if (msg != null) post.setMessage(msg);
        post.setTags(tagsSet);
        postService.update(post);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* Delete routes */

    @GetMapping("post/delete/{id}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable("id") long postId) {
        Post post = postService.getOne(postId);
        User currUser = userService.getCurrent();
        if (post == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(post.getUser().getId() == currUser.getId() || roleService.isUserAdmin(currUser)) {
            commentService.deleteByPost(post);
            postService.deleteById(postId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("comment/delete/{id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable("id") long commentId) {
        Optional<Comment> comment = commentService.getOne(commentId);
        User currUser = userService.getCurrent();
        if(!comment.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(comment.get().getUser().getId() == currUser.getId() || //User is the comments author
                comment.get().getPost().getUser().getId() == currUser.getId() || //User is the posts author
                roleService.isUserAdmin(currUser)) {
            commentService.deleteById(commentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /* File routes */

    @ResponseBody
    @GetMapping("file/{filename}")
    public FileSystemResource getPostFile(@PathVariable("filename") String filename) throws FileNotFoundException {
        File file = new File("files/" +  filename);
        return file.exists() ? new FileSystemResource(file) : new FileSystemResource(ResourceUtils.getFile("classpath:static/img/missing-media.png"));
    }

    @ResponseBody
    @GetMapping("thumbnail/{filename}")
    public FileSystemResource getPostThumbnail(@PathVariable("filename") String filename) throws FileNotFoundException {
        File file = new File("files/thumbnails/thumb-" +  filename);
        return file.exists() ? new FileSystemResource(file) : new FileSystemResource(ResourceUtils.getFile("classpath:static/img/missing-media.png"));
    }

    @GetMapping("image/{filename}")
    public String viewImage(@PathVariable("filename") String filename, Model model) {
        model.addAttribute("filename", filename);
        return "feed/file";
    }

}
