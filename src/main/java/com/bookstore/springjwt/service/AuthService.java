package com.bookstore.springjwt.service;

import com.bookstore.springjwt.models.ERole;
import com.bookstore.springjwt.models.Role;
import com.bookstore.springjwt.models.User;
import com.bookstore.springjwt.payload.request.LoginRequest;
import com.bookstore.springjwt.payload.request.SignupRequest;
import com.bookstore.springjwt.repository.RoleRepository;
import com.bookstore.springjwt.repository.UserRepository;
import com.bookstore.springjwt.security.jwt.JwtUtils;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailInUse(String email) {
        return userRepository.existsByEmail(email);
    }

    public User createUser(SignupRequest signUpRequest) {
        User user = User.builder().username(signUpRequest.getUsername()).email( signUpRequest.getEmail())
                        .password(encoder.encode(signUpRequest.getPassword())).build();
        Set<Role> roles = getRolesFromRequest(signUpRequest);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    private Set<Role> getRolesFromRequest(SignupRequest signUpRequest) {
        Set<String> strRoles = Optional.ofNullable(signUpRequest.getRole()).orElse(Collections.emptySet());
        return strRoles.stream()
                .map(this::mapRole)
                .collect(Collectors.toSet());
    }

    private Role mapRole(String roleStr) {
        if ("admin".equals(roleStr)) {
            return getRole(ERole.ROLE_ADMIN);
        }
        if ("mod".equals(roleStr)) {
            return getRole(ERole.ROLE_MODERATOR);
        }
        return getRole(ERole.ROLE_USER);
    }

    private Role getRole(ERole role) {
        return roleRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }

    public Authentication getAuthentication(final LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    }

    public String getJwtToken(final Authentication authentication) {
        return jwtUtils.generateJwtToken(authentication);
    }
}
