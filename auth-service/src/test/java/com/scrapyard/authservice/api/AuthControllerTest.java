package com.scrapyard.authservice.api;

import com.google.gson.Gson;
import com.scrapyard.authservice.api.DTOs.RegisterDTO;
import com.scrapyard.authservice.api.exceptions.UsernameExistsException;
import com.scrapyard.authservice.service.auth.AuthOpsService;
import com.scrapyard.authservice.service.auth.TokenService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
class AuthControllerTest {
    @MockBean
    AuthOpsService authOpsService;
    @MockBean TokenService tokenService;
    @Autowired
    private MockMvc mockMvc;
    Gson gson = new Gson();

    @Nested
    @DisplayName("Register tests")
    class RegisterTests {
        RegisterDTO registerDTO = RegisterDTO.builder().username("test_username").password("PASSWORD").build();

        @Test
        void givenNullRegisterDTOThrowError() throws Exception {
            mockMvc.perform(post("/auth/register")).andExpect(status().isBadRequest());
        }
        @Test
        void givenValidRegisterDTOReturn200() throws Exception {
            when(authOpsService.register(registerDTO)).thenReturn("test_token");
            mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(registerDTO))
                        .characterEncoding("utf-8")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("test_token"));
        }
        @Test
        void givenUsernameExistsThrow409() throws Exception {
            when(authOpsService.register(registerDTO)).thenThrow(UsernameExistsException.createWith("test_username"));
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(registerDTO))
                            .characterEncoding("utf-8")
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.errors.[0]").value("User with username \"test_username\" already exists"));
        }
    }
}