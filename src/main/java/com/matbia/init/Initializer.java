package com.matbia.init;

import com.matbia.service.RoleService;
import com.matbia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;

@Component
public class Initializer implements ApplicationRunner {
    private UserService userService;
    private RoleService roleService;

    @Autowired
    public Initializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        //Initialize media directories
        File dirs = new File("files/thumbnails");
        if(!dirs.exists() || !dirs.isDirectory())
            dirs.mkdirs();

        //Initialize roles and admin account
        roleService.init();
        userService.init();
    }
}
