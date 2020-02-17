package com.matbia.init;

import com.matbia.service.RoleService;
import com.matbia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class SecurityInitializer implements ApplicationRunner {
    private UserService userService;
    private RoleService roleService;

    @Autowired
    public SecurityInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        roleService.init();
        userService.init();
    }
}
