package com.matbia.controller;

import com.matbia.enums.ImageFileExtension;
import com.matbia.exception.ObjectNotFoundException;
import com.matbia.misc.Utils;
import com.matbia.model.*;
import com.matbia.service.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("user")
public class UserController {
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ConfirmationService confirmationService;
    @Autowired
    private PasswordRecoveryService passwordRecoveryService;
    @Autowired
    private ProfilePictureService profilePictureService;
    @Autowired
    private PostService postService;
    @Autowired
    private MailService mailService;

    @GetMapping("login")
    public String showLoginForm(Principal user) {
        if (user != null) return "redirect:/feed/dashboard";

        return "user/login";
    }

    @GetMapping("verificationCheck")
    public String verify(Model model, HttpSession session) {
        User user = userService.getCurrent();
        session.setAttribute("currUser", user);
        //Check if user has any role assigned
        if(roleService.getAll().stream().anyMatch(role -> role.getUsers().contains(user))) return "redirect:/feed/dashboard";
        model.addAttribute("email", user.getEmail());
        SecurityContextHolder.clearContext();
        return "user/unverified";
    }

    @GetMapping("register")
    public String showRegisterationForm(Principal user, Model model) {
        if (user != null) return "redirect:/feed/dashboard";

        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("register")
    public String register(@Valid User user, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return "user/register";
        }

        //Check if user with this email address already exists
        if(userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("userAlreadyRegistered" ,true);
            return "user/register";
        }

        userService.save(user);
        confirmationService.generate(user);
        LOGGER.log(Level.INFO, "New user registered: " + user.getEmail());
        return "user/success";
    }

    @GetMapping("confirm")
    public String confirm(@RequestParam("token") String token, final RedirectAttributes redirectAttributes) {
        ConfirmationToken confirmationToken = confirmationService.findByToken(token);
        if(confirmationToken == null) return "redirect:/";
        roleService.assign(confirmationToken.getUser(), "CONFIRMED");
        confirmationService.delete(confirmationToken.getId());
        redirectAttributes.addAttribute("confirmed", true);
        LOGGER.log(Level.INFO, "User confirmed: " + confirmationToken.getUser().getEmail());
        return "redirect:/user/login";
    }

    @GetMapping("resendConfirmation")
    public String retryConfirmation (@RequestParam("email") String email, final RedirectAttributes redirectAttributes){
        User user = userService.findByEmail(email);
        if (user == null) return "redirect:/";
        confirmationService.deleteByUser(user); //Delete old token
        confirmationService.generate(user);
        redirectAttributes.addAttribute("resendConfirmation", true);
        return "redirect:/user/login";
    }

    @GetMapping("requestRecovery")
    public String requestRecoveryForm() {
        return "user/request-recovery";
    }

    @PostMapping("requestRecovery")
    public String requestRecovery(@RequestParam("email") String email, final RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(email);
        if(user != null) {
            passwordRecoveryService.deleteByUser(user); //Delete old token
            passwordRecoveryService.generate(user);
            LOGGER.log(Level.INFO, "User requested recovery: " + user.getEmail());
        }
        redirectAttributes.addAttribute("sent", true);
        return "redirect:/user/requestRecovery";
    }

    @GetMapping("recover")
    public String passwordRecoveryForm(@RequestParam("token") String token) {
        if(passwordRecoveryService.findByToken(token) == null) return "redirect:/";
        return "user/recovery";
    }

    @PostMapping("recover")
    public String passwordRecovery(@RequestParam("token") String token, @RequestParam("passwd") String password,
                                   final RedirectAttributes redirectAttributes) {
        User user = passwordRecoveryService.findByToken(token).getUser();
        userService.updatePassword(user, password);
        passwordRecoveryService.deleteByUser(user); //Delete old token
        redirectAttributes.addAttribute("passwdChanged", true);
        LOGGER.log(Level.INFO, "User recovered: " + user.getEmail());
        return "redirect:/user/login";
    }

    @GetMapping("profile/{id}")
    public String showProfile(@PathVariable("id") long userId, Model model) {
        Optional<User> user = userService.getOne(userId);
        if (!user.isPresent()) throw new ObjectNotFoundException();
        model.addAttribute("currUser", userService.getCurrent());
        model.addAttribute("user", user.get());
        return "user/profile";
    }

    @GetMapping("edit")
    public String showEditPage(Model model) {
        User currUser = userService.getCurrent();
        model.addAttribute("user", currUser);
        model.addAttribute("settings", currUser.getSettings());
        return "user/edit";
    }

    @PostMapping("edit")
    public String editProfile(@Valid @ModelAttribute("person") Person person, BindingResult result, Model model, final RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            model.addAttribute("user", userService.getCurrent());
            return "user/edit";
        }
        User u = userService.getCurrent();
        u.setPerson(person);
        userService.update(u);
        redirectAttributes.addAttribute("saved", true);
        return "redirect:/user/edit";
    }

    @GetMapping("delete/{id}")
    @Transactional
    public String deleteUser(@PathVariable("id") long userId) {
        Optional<User> user = userService.getOne(userId);
        if(!user.isPresent()) throw new ObjectNotFoundException();
        profilePictureService.removeByUser(user.get());
        userService.delete(user.get());
        postService.deleteByUser(user.get());
        return "redirect:/feed/dashboard";
    }

    @PostMapping("settings")
    public String editSettings(@ModelAttribute Settings settings) {
        User user = userService.getCurrent();
        user.getSettings().setWatchNotifications(settings.isWatchNotifications());
        userService.update(user);
        return "redirect:/user/edit";
    }

    @PostMapping("passwd")
    public String changePassword(@RequestParam("password") String password, final RedirectAttributes redirectAttributes) {
        User user = userService.getCurrent();
        user.setPassword(password);
        userService.save(user);
        redirectAttributes.addAttribute("passwordChanged", true);
        LOGGER.log(Level.INFO, "User changed his password: " + user.getEmail());
        return "redirect:/user/edit";
    }

    @GetMapping("profpic/{id}")
    public ResponseEntity<byte[]> getProfilePictureThumbnail(@PathVariable("id") long userId) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        Optional<ProfilePicture> profilePicture = profilePictureService.getByUser(userService.getOne(userId).get());

        //If profile image is not set
        if(!profilePicture.isPresent()) {
            byte[] placeholderPicture = FileUtils.readFileToByteArray(new File("profile-placeholder.jpg"));
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(placeholderPicture, headers, HttpStatus.CREATED);
        }

        //Set proper media type
        if(profilePicture.get().getExtension().equals(ImageFileExtension.PNG)) headers.setContentType(MediaType.IMAGE_PNG);
        else headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(profilePicture.get().getThumbnail(), headers, HttpStatus.CREATED);
    }

    @PostMapping("setprofpic")
    public String uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) profilePictureService.save(userService.getCurrent(), file);
        return "redirect:/user/edit";
    }

    @PostMapping("watch/{id}")
    public ResponseEntity<HttpStatus> watchUser(@PathVariable("id") long userId) {
        User currUser = userService.getCurrent();
        Optional<User> user = userService.getOne(userId);
        if(!user.isPresent()) throw new ObjectNotFoundException();
        if(currUser.getId() == userId || user.get().getBlockedUsersIds().contains(currUser.getId())) return new ResponseEntity<>(HttpStatus.FORBIDDEN); //Prevent blocked users from watching

        //Watch notification
        if(user.get().getSettings().isWatchNotifications()) {
            String subject = currUser.getFirstName() + " " + currUser.getLastName() + " dodał/a cię do obserwowanych";
            String message = "Wyświetl profil: http://" + Utils.getExternalIP() + "/user/profile/" + currUser.getId();
            mailService.send(user.get().getContactEmail(), subject, message);
        }

        //Toggle watch
        if(currUser.getWatchedUsersIds().contains(userId))
            currUser.getWatchedUsersIds().removeIf(id -> id == userId); //Remove watched user ID
        else
            currUser.getWatchedUsersIds().add(userId); //Add a new user ID
        userService.update(currUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("block/{id}")
    public ResponseEntity<HttpStatus> blockUser(@PathVariable("id") long userId) {
        User currUser = userService.getCurrent();
        Optional<User> user = userService.getOne(userId);
        if(!user.isPresent()) throw new ObjectNotFoundException();

        if(!currUser.getBlockedUsersIds().contains(userId)) {
            currUser.getBlockedUsersIds().add(userId); //Add a new blocked user ID
            user.get().getWatchedUsersIds().removeIf(id -> id == currUser.getId()); //Remove current user from blocked user's watched list
            userService.update(user.get());
        } else currUser.getBlockedUsersIds().removeIf(id -> id == userId); //Remove blocked user ID

        userService.update(currUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
