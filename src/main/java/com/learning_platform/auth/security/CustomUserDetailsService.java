package com.learning_platform.auth.security;

import com.learning_platform.auth.models.User;
import com.learning_platform.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // ✅ Ensure ROLE_ prefix exists when creating authorities
        String roleName = user.getRole().name(); // Keep it as stored in DB (e.g., "ROLE_STUDENT")

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName)); // No need to modify

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword()) 
                .authorities(authorities)
                .build();
    }
}
