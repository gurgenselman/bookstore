package com.bezkoder.springjwt.service;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
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

    public void createUser(SignupRequest signUpRequest) {
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));
        Set<Role> roles = getRolesFromRequest(signUpRequest);
        user.setRoles(roles);

        userRepository.save(user);
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
