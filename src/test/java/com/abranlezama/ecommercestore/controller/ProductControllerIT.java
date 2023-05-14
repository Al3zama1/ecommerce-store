package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.dto.product.ProductResponseDTO;
import com.abranlezama.ecommercestore.model.Category;
import com.abranlezama.ecommercestore.model.Product;
import com.abranlezama.ecommercestore.model.ProductCategory;
import com.abranlezama.ecommercestore.model.ProductCategoryId;
import com.abranlezama.ecommercestore.objectmother.CategoryMother;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.repository.CategoryRepository;
import com.abranlezama.ecommercestore.repository.ProductCategoryRepository;
import com.abranlezama.ecommercestore.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@Testcontainers
public class ProductControllerIT {

    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("ecommerce")
            .withPassword("test")
            .withUsername("tes");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.datasource.username", database::getUsername);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        productCategoryRepository.deleteAll();
    }

    @Test
    void shouldRetrieveProductsWhenNoCategoriesArePassed() throws Exception {
        // Given
        int page = 0;
        int pageSize = 20;

        Product product = productRepository.save(ProductMother.complete().build());
        Category technology = categoryRepository.save(CategoryMother.technology().build());

        ProductCategoryId productTechnology = new ProductCategoryId(product.getId(), technology.getId());
        ProductCategory productCategory = ProductCategory.builder()
                        .product(product)
                        .category(technology)
                        .id(productTechnology)
                        .build();

        productCategoryRepository.save(productCategory);

        // When, Then
        mockMvc.perform(get("/products")
                .param("page", String.valueOf(page))
                .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(1)));
    }

    @Test
    void shouldReturnNotReturnDuplicateProductsWhenFetchingByCategory() throws Exception {
        // Given
        int page = 0;
        int pageSize = 20;

        Product product = productRepository.save(ProductMother.complete().build());
        Category technology = categoryRepository.save(CategoryMother.technology().build());
        Category education = categoryRepository.save(CategoryMother.education().build());

        ProductCategoryId productTechnologyId = new ProductCategoryId(product.getId(), technology.getId());
        ProductCategory productTechnology = ProductCategory.builder()
                .product(product)
                .category(technology)
                .id(productTechnologyId)
                .build();

        ProductCategoryId productEducationId = new ProductCategoryId(product.getId(), technology.getId());
        ProductCategory productEducation = ProductCategory.builder()
                .product(product)
                .category(education)
                .id(productEducationId)
                .build();


        productCategoryRepository.save(productTechnology);
        productCategoryRepository.save(productEducation);


        // When
        MvcResult result = mockMvc.perform(get("/products")
                .param("page", String.valueOf(page))
                .param("pageSize", String.valueOf(pageSize))
                .param("categories", "technology")
                .param("categories", "education"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        ProductResponseDTO[] products = objectMapper
                .readValue(result.getResponse().getContentAsString(), ProductResponseDTO[].class);

        assertThat(products.length).isEqualTo(1);
        assertThat(Arrays.stream(products)
                .filter(p -> p.id() == 1).toList().size())
                .isEqualTo(1);
    }
}
