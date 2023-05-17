package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.model.Category;
import com.abranlezama.ecommercestore.model.CategoryType;

public class CategoryMother {

    public static Category.CategoryBuilder technology() {
        return Category.builder()
                .category(CategoryType.ELECTRONICS);
    }

    public static Category.CategoryBuilder education() {
        return Category.builder()
                .category(CategoryType.EDUCATION);
    }
}
