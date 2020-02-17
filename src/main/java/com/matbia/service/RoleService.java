package com.matbia.service;

import com.matbia.model.Role;
import com.matbia.model.User;
import com.matbia.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RoleService {
    private final static Logger LOGGER = Logger.getLogger(RoleService.class.getName());

    @Autowired
    private RoleRepository roleRepository;

    public void save(Role role) {
        try {
            roleRepository.save(role);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        if(roleRepository.count() == 0) {
            save(new Role("ROLE_ADMIN"));
            save(new Role("ROLE_CONFIRMED"));
        }
    }

    public Role find(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public void assign(User user, String roleName) {
        Role role = roleRepository.findByName("ROLE_" + roleName);
        if(role != null) {
            role.getUsers().add(user);
            roleRepository.save(role);
        } else {
            LOGGER.log(Level.SEVERE, "Role " + roleName + " could't have been found.");
        }
    }

    public boolean isUserAdmin(User user) {
        return find("ROLE_ADMIN").getUsers().contains(user);
    }
}
