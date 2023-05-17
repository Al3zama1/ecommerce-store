package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.SecurityConfiguration;
import com.abranlezama.ecommercestore.dto.product.AddProductRequestDTO;
import com.abranlezama.ecommercestore.dto.product.UpdateProductRequestDTO;
import com.abranlezama.ecommercestore.objectmother.AddProductRequestDTOMother;
import com.abranlezama.ecommercestore.objectmother.UpdateProductRequestDTOMOther;
import com.abranlezama.ecommercestore.service.AuthenticationService;
import com.abranlezama.ecommercestore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfiguration.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService productService;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private JwtAuthenticationConverter authenticationConverter;
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldCallProductServiceToFetchProducts() throws Exception{
        // Given
        int page = 0;
        int pageSize = 20;

        // When
        mockMvc.perform(get("/products")
                .param("page", String.valueOf(page))
                .param("pageSize", String.valueOf(pageSize))
                .param("categories", ""))
                .andExpect(status().isOk());

        // Then
        then(productService).should().getProducts(page, pageSize, Set.of());
    }

    @Test
    void shouldReturn422WhenInvalidPageParametersAreProvided() throws Exception{
        // Given
        int page = -1;
        int pageSize = 20;

        // When
        mockMvc.perform(get("/products")
                        .param("page", String.valueOf(page))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("categories", ""))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "EMPLOYEE")
    void shouldCallProductServiceToAddNewProduct() throws Exception {
        // Given
        String userEmail = "duke.last@gmail.com";
        AddProductRequestDTO requestDto = new AddProductRequestDTO(
                "Xbox",
                "Next gen console",
                600f,
                200,
                Set.of("electronics"));

        given(productService.createProduct(userEmail, requestDto)).willReturn(1L);

        // When
        this.mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", String.valueOf("/products/1")));

        // Then
        then(productService).should().createProduct(userEmail, requestDto);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldBlockUnauthorizedUsersFromAccessingCreateProductEndpoint() throws Exception {
        // GIVEN
        AddProductRequestDTO requestDto = AddProductRequestDTOMother.create().build();

        // When
        this.mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    // test product removal
    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "EMPLOYEE")
    void shouldCallProductServiceToRemoveProduct() throws Exception {
        // Given
        String userEmail = "duke.last@gmail.com";
        long productId = 1;

        // When
        this.mockMvc.perform(delete("/products")
                .param("productId", String.valueOf(productId)))
                .andExpect(status().isNoContent());

        // Then
        then(productService).should().removeProduct(userEmail, productId);
    }

    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "EMPLOYEE")
    void shouldThrow422WhenMalformedProductIdIsGiven() throws Exception {
        // Given
        long productId = -1;

        // When
        this.mockMvc.perform(delete("/products")
                        .param("productId", String.valueOf(productId)))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockUser(username = "duke.last@gmail.com", roles = "EMPLOYEE")
    void shouldCallProductServiceToUpdateProduct() throws Exception {
        // Given
        String userEmail = "duke.last@gmail.com";
        long productId = 1;
        UpdateProductRequestDTO requestDto = UpdateProductRequestDTOMOther.complete().build();

        // When
        this.mockMvc.perform(patch("/products")
                .param("productId", String.valueOf(productId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        // Then
        then(productService).should().updateProduct(userEmail, productId, requestDto);
    }

}
