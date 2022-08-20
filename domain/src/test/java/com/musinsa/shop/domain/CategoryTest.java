package com.musinsa.shop.domain;

import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CategoryTest {
    @Autowired private CategoryRepository categoryRepository;

    @Test
    @DisplayName("기본적인 CRUD 동작을 확인한다.")
    public void basicCrudTest() {
        assertAll(
            () -> {
                Optional<Category> findCategoryOp = categoryRepository.findById(123123L);
                assertTrue(findCategoryOp.isEmpty());
            },
            () -> {
                categoryRepository.save(new Category("상의", 1, null));
                categoryRepository.save(new Category("하의", 1, null));
                assertEquals(2, categoryRepository.findAll().size());
                assertEquals("상의", categoryRepository.findAll().get(0).getName());
                assertEquals(1, categoryRepository.findAll().get(0).getDepth());
                assertEquals("하의", categoryRepository.findAll().get(1).getName());
                assertEquals(1, categoryRepository.findAll().get(1).getDepth());
            },
            () -> {
                Optional<Category> findCategoryOp = categoryRepository.findByName("상의");
                assertTrue(findCategoryOp.isPresent());
                assertEquals("상의", findCategoryOp.get().getName());
            },
            () -> {
                categoryRepository.deleteById(1L);
                assertEquals(1, categoryRepository.findAll().size());
                assertEquals("하의", categoryRepository.findAll().get(0).getName());
            }
        );
    }
}