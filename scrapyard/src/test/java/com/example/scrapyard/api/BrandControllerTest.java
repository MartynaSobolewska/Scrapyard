package com.example.scrapyard.api;

import com.example.scrapyard.auth.JwtGenerator;
import com.example.scrapyard.domain.BrandDTO;
import com.example.scrapyard.domain.CarDTO;
import com.example.scrapyard.model.*;
import com.example.scrapyard.service.BrandService;
import com.example.scrapyard.service.CarService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Gson gson;

    @Autowired
    private JwtGenerator jwtGenerator;

    @MockBean
    private BrandService service;

    private BrandDTO goodBrandDTO;
    private Brand goodBrand;

    private String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImludmFsaWQgand0IHRva2VuIiwiaWF0IjoxNTE2MjM5MDIyfQ.EbNcDTfTgPJn5LsfLUlba_Tdzmhmd4L4b0tmqUnH3BM";

    @BeforeEach
    void setUp() {
        gson = new Gson();
        goodBrandDTO = BrandDTO.builder().name("The Car Brand").build();
        goodBrand = Brand.builder().id(UUID.randomUUID()).name(goodBrandDTO.getName()).build();
    }

    @Nested
    @DisplayName("POST /brand tests")
    class CreateCarTests {
        @DisplayName("ADMIN auth, valid brand -> 201")
        @Test
        void whenAdminAddsCorrectBrandReturns201() throws Exception{
            given(service.addBrand(any(BrandDTO.class))).willReturn(goodBrand);
            System.out.println(gson.toJson(goodBrandDTO));
            mockMvc.perform(
                            post("/brand")
                                    .header("Authorization", "Bearer " + jwtGenerator.createTestToken("adminuser", true))
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(goodBrandDTO))
                                    .characterEncoding("utf-8"))
                    .andExpect(status().isCreated());
        }

        @DisplayName("USER auth, valid brand -> 403")
        @Test
        void whenUserAddsCorrectBrandReturns403() throws Exception{
            given(service.addBrand(any(BrandDTO.class))).willReturn(goodBrand);
            System.out.println(gson.toJson(goodBrandDTO));
            mockMvc.perform(
                            post("/brand")
                                    .header("Authorization", "Bearer " + jwtGenerator.createTestToken("adminuser"))
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(goodBrandDTO))
                                    .characterEncoding("utf-8"))
                    .andExpect(status().isForbidden());
        }

        @DisplayName("No auth, valid brand -> 401")
        @Test
        void whenNoAuthAddsCorrectBrandReturns401() throws Exception{
            given(service.addBrand(any(BrandDTO.class))).willReturn(goodBrand);
            mockMvc.perform(
                            post("/brand")
                                    .header("Authorization", "Bearer " + "xyz")
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(goodBrandDTO))
                                    .characterEncoding("utf-8"))
                    .andExpect(status().isUnauthorized());
        }
        @DisplayName("Admin auth, no brand name -> 400")
        @Test
        void whenAdminAuthAddIncorrectBrandReturns400() throws Exception{
            given(service.addBrand(any(BrandDTO.class))).willReturn(goodBrand);
            mockMvc.perform(
                            post("/brand")
                                    .header("Authorization", "Bearer " + jwtGenerator.createTestToken("Admin", true))
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(BrandDTO.builder().build()))
                                    .characterEncoding("utf-8"))
//                    .andExpect(MockMvcResultMatchers.jsonPath("$").value("STH"))
                    .andExpect(status().isBadRequest());
        }
        @DisplayName("Admin auth, invalid brand name -> 400")
        @Test
        void whenAdminAuthAddBrandWithTooShortNameReturns400() throws Exception{
            given(service.addBrand(any(BrandDTO.class))).willReturn(goodBrand);
            mockMvc.perform(
                            post("/brand")
                                    .header("Authorization", "Bearer " + jwtGenerator.createTestToken("Admin", true))
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(BrandDTO.builder().name("A").build()))
                                    .characterEncoding("utf-8"))
//                    .andExpect(MockMvcResultMatchers.jsonPath("$").value("STH"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /brand/{id} tests")
    class GetBrandTests {
        @DisplayName("ADMIN auth, valid id -> 200")
        @Test
        void whenAdminGetsCorrectBrandReturns200() throws Exception{
            UUID uuid = UUID.randomUUID();
            given(service.getBrandById(uuid)).willReturn(goodBrand);
            mockMvc.perform(
                            get("/brand/" + uuid)
                                    .header("Authorization", "Bearer " + jwtGenerator.createTestToken("adminuser", true))
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(goodBrandDTO))
                                    .characterEncoding("utf-8"))
                    .andExpect(status().is2xxSuccessful());
        }

        @DisplayName("USER auth, valid brand -> 403")
        @Test
        void whenUserAddsCorrectBrandReturns403() throws Exception{
            UUID uuid = UUID.randomUUID();
            given(service.getBrandById(uuid)).willReturn(goodBrand);
            mockMvc.perform(
                            post("/brand/" + uuid)
                                    .header("Authorization", "Bearer " + jwtGenerator.createTestToken("nonadmin"))
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(goodBrandDTO))
                                    .characterEncoding("utf-8"))
                    .andExpect(status().isForbidden());
        }

        @DisplayName("No auth, valid brand -> 401")
        @Test
        void whenNoAuthAddsCorrectBrandReturns401() throws Exception{
            UUID uuid = UUID.randomUUID();
            given(service.getBrandById(uuid)).willReturn(goodBrand);
            mockMvc.perform(
                            post("/brand/"+ uuid)
                                    .header("Authorization", "Bearer " + invalidToken)
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(goodBrandDTO))
                                    .characterEncoding("utf-8"))
                    .andExpect(status().isUnauthorized());
        }
    }


}