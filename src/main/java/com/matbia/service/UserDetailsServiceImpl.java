package com.matbia.service;

import com.matbia.model.User;
import com.matbia.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private HttpServletRequest request;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        String clientIP = getClientIP();
        if (loginAttemptService.isBlocked(clientIP))
            throw new DisabledException("Exceeded login attempts limit for address: " + clientIP);

        User user = userService.findByEmail(email);
        if(user == null) throw new UsernameNotFoundException("User not found");

        Set<GrantedAuthority> grantedAuthorities = roleRepository.findAll().stream()
                .filter(r -> r.getUsers().contains(user))
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
    }

    /**
     * Returns IP address from current servlet request.
     * @return client's IP address
     */
    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) return request.getRemoteAddr();
        return xfHeader.split(",")[0];
    }
}
