package com.bookstore.springjwt.controllers;

import com.bookstore.springjwt.exception.TokenRefreshException;
import com.bookstore.springjwt.models.User;
import com.bookstore.springjwt.payload.request.TokenRefreshRequest;
import com.bookstore.springjwt.payload.response.TokenRefreshResponse;
import com.bookstore.springjwt.security.jwt.JwtUtils;
import com.bookstore.springjwt.service.AuthService;
import com.bookstore.springjwt.service.RefreshTokenService;
import java.util.List;
import jakarta.validation.Valid;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.springjwt.payload.request.LoginRequest;
import com.bookstore.springjwt.payload.request.SignupRequest;
import com.bookstore.springjwt.payload.response.JwtResponse;
import com.bookstore.springjwt.payload.response.MessageResponse;
import com.bookstore.springjwt.security.services.UserDetailsImpl;
import com.bookstore.springjwt.models.RefreshToken;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authService.getAuthentication(loginRequest);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = authService.getJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        final Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByUser(User.builder().id(userDetails.getId()).build());
        final RefreshToken refreshToken = optionalRefreshToken.orElse(refreshTokenService.createRefreshToken(userDetails.getId()));

        return ResponseEntity.ok(new JwtResponse(jwtToken,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                refreshToken.getToken()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (authService.isUsernameTaken(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (authService.isEmailInUse(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        authService.createUser(signUpRequest);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }
}
