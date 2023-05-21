package com.abranlezama.ecommercestore.controller;

import com.abranlezama.ecommercestore.config.PostgresContainerConfig;
import com.abranlezama.ecommercestore.dto.authentication.AuthenticationRequestDTO;
import com.abranlezama.ecommercestore.dto.product.AddProductRequestDTO;
import com.abranlezama.ecommercestore.dto.product.ProductResponseDTO;
import com.abranlezama.ecommercestore.dto.product.UpdateProductRequestDTO;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.objectmother.*;
import com.abranlezama.ecommercestore.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Import(PostgresContainerConfig.class)
public class ProductControllerIT {

    @Autowired
    private WebTestClient webTestClient;
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
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
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
        this.webTestClient
                .get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponseDTO.class)
                .hasSize(1);
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
        this.webTestClient
                .get()
                .uri("/products?page={page}&pageSize={pageSize}&categories={categories}&categories={categories}",
                        page, pageSize, "technology", "education")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponseDTO.class)
                .hasSize(1)
                .value(products -> products.get(0).id(), Matchers.is(product.getId()));
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
        EntityExchangeResult<String> response = this.webTestClient
                .post()
                .uri("/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(createRequest))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody(String.class)
                .returnResult();

        // Then
        String[] location = Objects.
                requireNonNull(response.getResponseHeaders().get("Location")).get(0).split("/");

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
        this.webTestClient
                .delete()
                .uri("/products?productId={productId}", productId)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNoContent();

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
        this.webTestClient
                .patch()
                .uri("/products?productId={productId}", product.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(updateRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponseDTO.class)
                .value(ProductResponseDTO::id, Matchers.is(product.getId()))
                .value(ProductResponseDTO::name, Matchers.is(updateRequest.name()))
                .value(ProductResponseDTO::description, Matchers.is(updateRequest.description()))
                .value(ProductResponseDTO::price, Matchers.is(updateRequest.price()));

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
        return this.webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(authRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
    }
}
