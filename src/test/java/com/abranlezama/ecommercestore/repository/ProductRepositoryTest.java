package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.CategoryType;
import com.abranlezama.ecommercestore.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = false)
@ActiveProfiles("dev")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    TestEntityManager testEntityManager;

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


    @Test
    @Sql(scripts = "/scripts/INIT_PRODUCT_WITH_CATEGORIES.sql")
    @Rollback(value = false)
    void shouldReturnProductsForGivenCategories() {
        // Given
        int page = 0;
        int pageSize = 20;

        // When
        Page<Product> products = productRepository
                .findProductByCategory(PageRequest.of(page,  pageSize),
                        List.of(CategoryType.EDUCATION));

        // Then
        assertThat(products.getTotalElements()).isEqualTo(10);

        testEntityManager.flush();
    }

    @Test
    void shouldNotReturnDuplicateProductsWhenTheyHaveMoreThanOneCategory() {
        // Given
        int page = 0;
        int pageSize = 20;

        // When
        Page<Product> products = productRepository
                .findProductByCategory(PageRequest.of(page,  pageSize),
                        List.of(CategoryType.EDUCATION, CategoryType.TECHNOLOGY));

        // Then
        assertThat(products.getTotalElements()).isEqualTo(19);
    }

}
