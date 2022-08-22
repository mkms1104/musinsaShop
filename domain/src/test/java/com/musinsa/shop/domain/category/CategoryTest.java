package com.musinsa.shop.domain.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CategoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("categoryRepository save 메서드 동작 확인")
    @Test
    void insert() {
        //given & when
        categoryRepository.save(new Category("상의", 1));
        categoryRepository.save(new Category("긴소매 티셔츠", 2, 1L));

        //then
        assertEquals(2, categoryRepository.findAll().size());
        assertEquals("상의", categoryRepository.findAll().get(0).getName());
        assertEquals(1, categoryRepository.findAll().get(0).getDepth());
        assertEquals("긴소매 티셔츠", categoryRepository.findAll().get(1).getName());
        assertEquals(2, categoryRepository.findAll().get(1).getDepth());
    }

    @DisplayName("categoryRepository 조회 메서드 동작 확인")
    @Test
    void get() {
        //given
        categoryRepository.save(new Category("상의", 1)); // id = 1
        categoryRepository.save(new Category("긴소매 티셔츠", 2, 1L)); // id = 2
        categoryRepository.save(new Category("반소매 티셔츠", 2, 1L)); // id = 3

        //when & then
        Optional<Category> findCategoryOp01 = categoryRepository.findById(123123L);
        assertTrue(findCategoryOp01.isEmpty());

        Optional<Category> findCategoryOp02 = categoryRepository.findById(1L);
        assertTrue(findCategoryOp02.isPresent());
        assertEquals("상의", findCategoryOp02.get().getName());
        assertEquals(1, findCategoryOp02.get().getDepth());

        Optional<Category> findCategoryOp03 = categoryRepository.findByName("상의");
        assertTrue(findCategoryOp03.isPresent());

        Optional<Category> findCategoryOp04 = categoryRepository.findByNameAndDepth("상의", 1);
        assertTrue(findCategoryOp04.isPresent());

        Optional<Category> findCategoryOp05 = categoryRepository.findByNameAndDepth("상의", 2);
        assertTrue(findCategoryOp05.isEmpty());

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Category> page = categoryRepository.findByParentId(1L, pageRequest);
        assertEquals("긴소매 티셔츠", page.getContent().get(0).getName());
        assertEquals("반소매 티셔츠", page.getContent().get(1).getName());
    }

    @DisplayName("변경 감지로 인한 update 동작 확인")
    @Test
    void update() {
        //given
        Category category = new Category("상의", 1);
        categoryRepository.save(category);

        // when
        category.updateCategoryName("하의");

        //then
        Optional<Category> findCategoryOp = categoryRepository.findById(1L);
        assertEquals("하의", findCategoryOp.get().getName());
    }

    @DisplayName("categoryRepository 삭제 메서드 동작 확인")
    @Test
    void delete() {
        //given
        Category category = categoryRepository.save(new Category("상의", 1));
        categoryRepository.save(new Category("반소매 티셔츠", 2, category.getId()));
        categoryRepository.save(new Category("셔츠/블라우스", 2, category.getId()));

        //when
        categoryRepository.deleteById(category.getId());
        categoryRepository.deleteByParentId(category.getId());

        //then
        assertTrue(categoryRepository.findAll().isEmpty());
    }
}