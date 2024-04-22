package com.example.scrapyard.api;

import com.example.scrapyard.api.exceptions.BrandExistsException;
import com.example.scrapyard.api.exceptions.BrandNotFoundException;
import com.example.scrapyard.api.helpers.OperationState;
import com.example.scrapyard.api.helpers.TokenType;
import com.example.scrapyard.auth.JwtGenerator;
import com.example.scrapyard.domain.BrandDTO;
import com.example.scrapyard.model.*;
import com.example.scrapyard.service.BrandService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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

    @BeforeEach
    void setUp() {
        gson = new Gson();
    }

    @Nested
    @DisplayName("POST /brand tests")
    class PostTests{
        @ParameterizedTest(name = "{index}: given {0} auth token, \"{1}\" brand name -> {2}")
        @MethodSource("provideAddBrandArguments")
        void postTests(TokenType tokenType, String brandName, int expectedStatus) throws Exception {
            BrandDTO brandDTO = BrandDTO.builder().name(brandName).build();
            Brand brand = Brand.builder().id(UUID.randomUUID()).name(brandName).build();
            given(service.addBrand(any(BrandDTO.class))).willReturn(brand);
            mockMvc.perform(
                            post("/brand")
                                    .header("Authorization", "Bearer " + getToken(tokenType))
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(brandDTO))
                                    .characterEncoding("utf-8"))
                    .andExpect(status().is(expectedStatus));
        }

        @Test
        @DisplayName("given ADMIN auth token, preexisting brand name -> 400")
        void givenBrandNameThatExistsReturns400() throws Exception {
            BrandDTO brandDTO = BrandDTO.builder().name("brandName").build();
            given(service.addBrand(any(BrandDTO.class))).willThrow(BrandExistsException.class);
            mockMvc.perform(
                            post("/brand")
                                    .header("Authorization", "Bearer " + getToken(TokenType.ADMIN))
                                    .header("guid", UUID.randomUUID())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(brandDTO))
                                    .characterEncoding("utf-8"))
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAddBrandArguments() {
            return Stream.of(
                    Arguments.of(TokenType.NONE, "Fiat", 401),
                    Arguments.of(TokenType.INVALID, "Fiat", 401),
                    Arguments.of(TokenType.USER, "Fiat", 403),
                    Arguments.of(TokenType.ADMIN, "Fiat", 201),
                    Arguments.of(TokenType.ADMIN, null, 400),
                    Arguments.of(TokenType.ADMIN, "", 400),
                    Arguments.of(TokenType.ADMIN, "  ", 400),
                    Arguments.of(TokenType.ADMIN, "A", 400)
            );
        }
    }

    @DisplayName("GET /brand/{id} tests")
    @ParameterizedTest(name = "{index}: given {0} auth token, when brand id: {1} -> {2}")
    @MethodSource("provideGetAndDeleteBrandArguments")
    void getTests(TokenType tokenType, OperationState operationState, int expectedStatus) throws Exception {
        String id = operationState == OperationState.INVALID ? "invalidUUID" : UUID.randomUUID().toString();
        switch (operationState){
            case NOT_FOUND -> given(service.getBrandById(UUID.fromString(id))).willThrow(BrandNotFoundException.class);
            case INVALID -> {}
            case FOUND -> given(service.getBrandById(UUID.fromString(id))).willReturn(Brand.builder().id(UUID.fromString(id)).name("Ford").build());
        }
        ResultActions resultActions = mockMvc.perform(
                        get("/brand/" + id)
                                .header("Authorization", "Bearer " + getToken(tokenType))
                                .header("guid", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8"))
                .andExpect(status().is(expectedStatus));

        if (expectedStatus == 200){
            resultActions
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Ford"));
        }
    }
    @DisplayName("DELETE /brand/{id} tests")
    @ParameterizedTest(name = "{index}: given {0} auth token, when brand id: {1} -> {2}")
    @MethodSource("provideGetAndDeleteBrandArguments")
    void deleteTests(TokenType tokenType, OperationState operationState, int expectedStatus) throws Exception {
        String id = operationState == OperationState.INVALID ? "invalidUUID" : UUID.randomUUID().toString();
        if (operationState == OperationState.NOT_FOUND){
            doThrow(BrandNotFoundException.createWith(id)).when(service).deleteBrandById(UUID.fromString(id));
        }
        mockMvc.perform(
                        get("/brand/" + id)
                                .header("Authorization", "Bearer " + getToken(tokenType))
                                .header("guid", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8"))
                .andExpect(status().is(expectedStatus));
    }

    private static Stream<Arguments> provideGetAndDeleteBrandArguments() {
        return Stream.of(
                Arguments.of(TokenType.NONE, OperationState.FOUND, 401),
                Arguments.of(TokenType.INVALID, OperationState.FOUND, 401),
                Arguments.of(TokenType.USER, OperationState.FOUND, 403),
                Arguments.of(TokenType.ADMIN, OperationState.FOUND, 200),
// TODO: fix the mock that doesn't throw 404 ^
//                Arguments.of(TokenType.ADMIN, OperationState.NOT_FOUND, 404),
                Arguments.of(TokenType.ADMIN, OperationState.INVALID, 400)
        );
    }

    @DisplayName("GET /brand tests")
    @ParameterizedTest(name = "{index}: given {0} auth token -> {2}")
    @MethodSource("provideGetAllBrandsArguments")
    void getAllBrandTests(TokenType tokenType, List<Brand> brandsInDB, int expectedStatus) throws Exception {

        given(service.getAllBrands()).willReturn(brandsInDB);

        ResultActions resultActions = mockMvc.perform(
                        get("/brand")
                                .header("Authorization", "Bearer " + getToken(tokenType))
                                .header("guid", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8"))
                .andExpect(status().is(expectedStatus));
        if (expectedStatus == 200){
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(brandsInDB.size())));
            for (int i = 0; i < brandsInDB.size(); i++) {
                resultActions
                        .andExpect(MockMvcResultMatchers.jsonPath("$.[" + i + "].name").value(brandsInDB.get(i).getName()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.[" + i + "].id").value(brandsInDB.get(i).getId().toString()));
            }
        }
    }

    private static Stream<Arguments> provideGetAllBrandsArguments() {
        List<Brand> brandList = List.of(
                Brand.builder().id(UUID.randomUUID()).name("Ford").build(),
                Brand.builder().id(UUID.randomUUID()).name("Kia").build(),
                Brand.builder().id(UUID.randomUUID()).name("Nissan").build());
        return Stream.of(
                Arguments.of(TokenType.NONE, Collections.EMPTY_LIST, 401),
                Arguments.of(TokenType.INVALID, Collections.EMPTY_LIST, 401),
                Arguments.of(TokenType.USER, Collections.EMPTY_LIST, 403),
                Arguments.of(TokenType.ADMIN, Collections.EMPTY_LIST, 200),
                Arguments.of(TokenType.ADMIN, brandList, 200)
        );
    }

    private String getToken(TokenType tokenType) {
        final String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImludmFsaWQgand0IHRva2VuIiwiaWF0IjoxNTE2MjM5MDIyfQ.EbNcDTfTgPJn5LsfLUlba_Tdzmhmd4L4b0tmqUnH3BM";
        return switch (tokenType) {
            case NONE -> null;
            case INVALID -> invalidToken;
            case USER -> jwtGenerator.createTestToken("testUser", false);
            case ADMIN -> jwtGenerator.createTestToken("testAdmin", true);
        };
    }

}