package com.matbia.init;

import com.matbia.service.RoleService;
import com.matbia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;

/**
 * Responsible for providing conditions required for the proper functioning of the application.
 */
@Component
public class Initializer implements ApplicationRunner {
    private UserService userService;
    private RoleService roleService;

    @Autowired
    public Initializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * Is ran every time the application starts.
     * Initializes the authentication system and ensures that a proper folder structure is present.
     * @param args arguments that were used to run the application
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        //Initialize media directories
        File dirs = new File("files/thumbnails");
        if(!dirs.exists() || !dirs.isDirectory())
            dirs.mkdirs();

        //Initialize roles and admin account
        roleService.init();
        userService.init();
    }
}
