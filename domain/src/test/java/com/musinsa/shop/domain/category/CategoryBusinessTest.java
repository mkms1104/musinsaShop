package com.musinsa.shop.domain.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryBusinessTest {

    @DisplayName("")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void isRoot(int depth) {
        Category category = new Category("아무개", depth);
        if (depth == 1) assertTrue(category.isRoot());
        else assertFalse(category.isRoot());
    }

    @DisplayName("루트 카테고리")
    @Test
    void validExistParentIdWithRoot() {
        // 루트 카테고리는 parentId 지정 불필요
        Category category01 = new Category("상의", 1);
        assertDoesNotThrow(category01::validExistParentIdWithChild);

        // 하위 카테고리 등록 시 parentId 등록 X
        Category category02 = new Category("카디건", 2);
        assertThrows(ValidationException.class, category02::validExistParentIdWithChild);

        // 하위 카테고리 등록 시 parentId 정상 지정
        Category category03 = new Category("카디건", 2, 1L);
        assertDoesNotThrow(category03::validExistParentIdWithChild);

        // 루트 카테고리에 parentId 값이 있어도 무시
        Category category04 = new Category("상의", 1, 1L);
        assertDoesNotThrow(category04::validExistParentIdWithChild);
    }
}
