package com.web.service;

import com.web.repository.UserRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private final UserRepository userRepository;

    public AuditorAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("SYSTEM"); // nếu chưa đăng nhập
        }
//        Long id = Long.valueOf(authentication.getName());
//        String username = userRepository.findUsernameById(id);
//        System.out.println("========= username "+ username);
        return Optional.of(authentication.getName()); // lấy username từ user login
    }
}
