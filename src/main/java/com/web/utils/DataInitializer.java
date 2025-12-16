package com.web.utils;

import com.web.entity.Authority;
import com.web.entity.User;
import com.web.repository.AuthorityRepository;
import com.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public void run(String... args) throws Exception {
        String password = "admin";
        String email = "admin@gmail.com";
        if(authorityRepository.findByName(Contains.ROLE_ADMIN) == null){
            Authority authority = new Authority();
            authority.setName(Contains.ROLE_ADMIN);
            authorityRepository.save(authority);
        }
        if(authorityRepository.findByName(Contains.ROLE_USER) == null){
            Authority authority = new Authority();
            authority.setName(Contains.ROLE_USER);
            authorityRepository.save(authority);
        }
        if (!userRepository.findByEmail(email).isPresent()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setActived(true);
            Authority authority = new Authority();
            authority.setName(Contains.ROLE_ADMIN);
            user.setAuthorities(authority);
            user.setFullname("ADMIN");
            userRepository.save(user);
        }
    }
}
