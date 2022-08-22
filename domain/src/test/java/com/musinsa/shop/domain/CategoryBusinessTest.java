package com.musinsa.shop.domain;

import com.musinsa.shop.domain.category.Category;
import org.junit.jupiter.api.Test;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryBusinessTest {
    @Test
    void isRoot() {
        Category category01 = new Category("상의", 1);
        assertTrue(category01.isRoot());

        Category category02 = new Category("카디건", 2);
        assertFalse(category02.isRoot());

        Category category03 = new Category("좋은 카디건", 3);
        assertFalse(category03.isRoot());
    }

    @Test
    void validExistParentIdWithRoot() {
        // 루트 카테고리는 parentId 지정 불필요
        Category category01 = new Category("상의", 1);
        assertDoesNotThrow(category01::validExistParentIdWithRoot);

        // 하위 카테고리 등록 시 parentId 등록 X
        Category category02 = new Category("카디건", 2);
        assertThrows(ValidationException.class, category02::validExistParentIdWithRoot);

        // 하위 카테고리 등록 시 parentId 정상 지정
        Category category03 = new Category("카디건", 2, 1L);
        assertDoesNotThrow(category03::validExistParentIdWithRoot);

        // 루트 카테고리에 parentId 값이 있어도 무시
        Category category04 = new Category("상의", 1, 1L);
        assertDoesNotThrow(category04::validExistParentIdWithRoot);
    }
}
