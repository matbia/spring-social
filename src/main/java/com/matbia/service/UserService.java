package com.matbia.service;

import com.matbia.model.Settings;
import com.matbia.model.User;
import com.matbia.repository.SettingsRepository;
import com.matbia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private SettingsRepository settingsRepository;

    public Optional<User> getOne(long id) {
        return userRepository.findById(id);
    }

    public void delete(User user) {
        try {
            userRepository.delete(user);
            roleService.getAll().forEach(r -> {
                if(r.getUsers().contains(user)) {
                    r.getUsers().removeIf(u -> u.getId() == user.getId());
                    roleService.save(r);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline(String email) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(org.springframework.security.core.userdetails.User.class::cast)
                .map(org.springframework.security.core.userdetails.User::getUsername)
                .collect(Collectors.toList())
                .contains(email);
    }

    public User getCurrent() {
        //return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public Set<User> idsToUsers(Set<Long> ids) {
        return ids.stream().map(id -> userRepository.getOne(id)).collect(Collectors.toSet());
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public long getSize() {
        return userRepository.count();
    }


    public void save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Settings settings = new Settings();
            settingsRepository.save(settings);
            user.setSettings(settings);
            userRepository.save(user);
            userRepository.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        userRepository.flush();
    }

    public void update(User user) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(User user, String password) {
        try {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        if(userRepository.count() == 0) {
            User admin = new User();
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setPassword("12345678");
            admin.setEmail("admin@example");
            Settings settings = new Settings();
            settingsRepository.save(settings);
            admin.setSettings(settings);
            save(admin);
            roleService.assign(admin, "ADMIN");
        }
    }
}
