package com.example.Jewelry.config;

import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = this.userService.getUserByEmailAndStatus(email, Constant.ActiveStatus.ACTIVE.value());

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        return customUserDetails;

    }
}

