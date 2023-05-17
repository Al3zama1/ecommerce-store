package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.Category;
import com.abranlezama.ecommercestore.model.CategoryType;
import com.abranlezama.ecommercestore.model.Product;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

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
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    void shouldReturnProductsBasedOnCategory() {
        // Given
        Set<CategoryType> productCategories = Set.of(CategoryType.EDUCATION, CategoryType.ELECTRONICS);
        Product product = ProductMother.complete().build();
        Category electronics = categoryRepository.findByCategory(CategoryType.ELECTRONICS).orElseThrow();
        Category education = categoryRepository.findByCategory(CategoryType.EDUCATION).orElseThrow();

        product.setProductCategories(Set.of(electronics, education));
        product = productRepository.save(product);


        // When
        List<Product> products = productRepository
                .findProductByCategory(PageRequest.of(0, 20), productCategories)
                .getContent();

        // Then
        assertThat(products.size()).isEqualTo(1);
    }

}
