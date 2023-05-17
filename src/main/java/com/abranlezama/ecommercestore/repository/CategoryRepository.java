package com.abranlezama.ecommercestore.repository;

import com.abranlezama.ecommercestore.model.Category;
import com.abranlezama.ecommercestore.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategory(CategoryType categoryType);

    Set<Category> findAllByCategoryIn(Set<CategoryType> categories);
}
