package com.bookstore.springjwt.it;

import com.bookstore.springjwt.payload.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAuthenticateUser_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("selman1");
        loginRequest.setPassword("selman123");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testAuthenticateUser_Fail_WrongPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("existingUser");
        loginRequest.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAuthenticateUser_Fail_NonExistingUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonExistingUser");
        loginRequest.setPassword("anyPassword");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAuthenticateUser_Fail_NullUsername() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);
        loginRequest.setPassword("anyPassword");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateUser_Fail_NullPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("existingUser");
        loginRequest.setPassword(null);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateUser_Fail_NullUsernameAndPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateUser_Fail_EmptyUsername() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("anyPassword");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateUser_Fail_EmptyPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("existingUser");
        loginRequest.setPassword("");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateUser_Fail_EmptyUsernameAndPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}
