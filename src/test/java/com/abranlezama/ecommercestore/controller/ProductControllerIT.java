package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.PostgresContainerConfig;
import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.product.AddProductRequestDTO;
import com.abranlezama.ecommercestore.dto.product.UpdateProductRequestDTO;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.objectmother.*;
import com.abranlezama.ecommercestore.repository.CategoryRepository;
import com.abranlezama.ecommercestore.repository.ProductRepository;
import com.abranlezama.ecommercestore.repository.RoleRepository;
import com.abranlezama.ecommercestore.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
@Import(PostgresContainerConfig.class)
@AutoConfigureMockMvc
public class ProductControllerIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }
    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldRetrieveProductsWhenNoCategoriesArePassed() throws Exception {
        // Given
        int page = 0;
        int pageSize = 20;

        Category electronics = categoryRepository.findByCategory(CategoryType.ELECTRONICS).orElseThrow();
        Product product = ProductMother.complete()
                .productCategories(Set.of(electronics))
                .build();

        productRepository.save(product);

        // When, Then
        mockMvc.perform(get("/products")
                .param("page", String.valueOf(page))
                .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void shouldNotReturnDuplicateProductsWhenFetchingByCategory() throws Exception {
        // Given
        int page = 0;
        int pageSize = 20;

        Set<Category> categories  = categoryRepository
                .findAllByCategoryIn(Set.of(CategoryType.EDUCATION, CategoryType.ELECTRONICS));

        Product product = ProductMother.complete()
                .productCategories(categories)
                .build();

        product = productRepository.save(product);

        // When
        MvcResult result = mockMvc.perform(get("/products")
                .param("page", String.valueOf(page))
                .param("pageSize", String.valueOf(pageSize))
                .param("categories", "technology")
                .param("categories", "education"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(product.getId().intValue())))
                .andReturn();
    }

    // Test creation of new products
    @Test
    void shouldCreateNewProduct() throws Exception {
        // Given
        registerEmployee();
        AddProductRequestDTO createRequest = AddProductRequestDTOMother
                .create()
                .categories(Set.of("electronics", "education"))
                .build();
        AuthenticationRequestDTO authRequest = AuthenticationRequestDTOMother.complete().build();

        String token = obtainToken(authRequest);

        // When
        MvcResult result = this.mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        // Then
        String[] location = Objects.requireNonNull(result.getResponse().getHeader("Location")).split("/");
        // retrieve product created from the id located in Location header
        Product product = productRepository
                .findById(Long.valueOf(location[location.length - 1]))
                .orElseThrow();
        // compare passed in categories with the ones added to product
        boolean categoriesMatch = product.getProductCategories().stream()
                .allMatch(category -> createRequest.categories()
                        .contains(category.getCategory().name().toLowerCase()));

        // make assertions to verify correctness
        assertThat(product.getProductCategories().size()).isEqualTo(2);
        assertThat(categoriesMatch).isTrue();
        assertThat(product.getName()).isEqualTo(createRequest.name());
        assertThat(product.getDescription()).isEqualTo(createRequest.description());
        assertThat(product.getStockQuantity()).isEqualTo(createRequest.stockQuantity());
        assertThat(product.getPrice()).isEqualTo(createRequest.price());
    }

    // test removal of products
    @Test
    void shouldRemoveProduct() throws Exception {
        // Given
        registerEmployee();
        Set<CategoryType> categoryTypes = Set.of(CategoryType.EDUCATION, CategoryType.ELECTRONICS);
        Set<Category> categories = categoryRepository.findAllByCategoryIn(categoryTypes);
        Product product = ProductMother.complete().productCategories(categories).build();
        product = productRepository.save(product);
        long productId = product.getId();

        AuthenticationRequestDTO authRequest = AuthenticationRequestDTOMother.complete().build();
        String token = obtainToken(authRequest);

        // When
        this.mockMvc.perform(delete("/products")
                .header("Authorization", "Bearer " + token)
                .param("productId", String.valueOf(productId)))
                .andExpect(status().isNoContent());

        // Then
        Optional<Product> productOptional = productRepository.findById(productId);
        assertThat(productOptional.isEmpty()).isTrue();
    }

    // test product updates
    @Test
    void shouldUpdateExistingProduct() throws Exception {
        // Given
        Set<CategoryType> categoryTypes = Set.of(CategoryType.EDUCATION);
        Set<Category> categories = categoryRepository.findAllByCategoryIn(categoryTypes);
        Product product = ProductMother.complete().productCategories(categories).build();
        product = productRepository.save(product);

        // register employee and obtain token
        registerEmployee();
        AuthenticationRequestDTO authRequest = AuthenticationRequestDTOMother.complete().build();
        String token = obtainToken(authRequest);

        // request with category of sports
        UpdateProductRequestDTO updateRequest = UpdateProductRequestDTOMOther.complete().build();

        // When
        this.mockMvc.perform(patch("/products")
                .header("Authorization", "Bearer " + token)
                .param("productId", String.valueOf(product.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updateRequest.name())))
                .andExpect(jsonPath("$.description", is(updateRequest.description())))
                .andExpect(jsonPath("$.price", is(updateRequest.price().doubleValue())));

        // Then
        // verify that the product categories were updated
        product = productRepository.findById(product.getId()).orElseThrow();
        boolean allMatch = product.getProductCategories()
                .stream()
                .allMatch(category -> updateRequest
                        .categories().contains(category.getCategory().name().toLowerCase()));

        assertThat(product.getProductCategories().size()).isEqualTo(1);
        assertThat(allMatch).isTrue();

    }


    private void registerEmployee() {
        Role role = roleRepository.findByRole(RoleType.EMPLOYEE).orElseThrow();
        User user = UserMother.complete().isEnabled(true).roles(Set.of(role)).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    private String obtainToken(AuthenticationRequestDTO authRequest) throws Exception {
        MvcResult result = this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }
}
