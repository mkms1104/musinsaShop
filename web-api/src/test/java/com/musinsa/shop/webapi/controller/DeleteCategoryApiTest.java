package com.musinsa.shop.webapi.controller;

import com.musinsa.shop.domain.category.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteCategoryApiTest extends CategoryApiTestAround {
    private final String URI = "/api/v1/mshop/categories/{category_id}";

    @Override
    protected void beforeEach() {
        Category category01 = categoryRepository.save(new Category("상의", 1));
        Category category01Sub01 = categoryRepository.save(new Category("반소매 티셔츠", 2, category01.getId()));
        categoryRepository.save(new Category("시원해", 3, category01Sub01.getId()));

        Category category02 = categoryRepository.save(new Category("바지", 1));
        categoryRepository.save(new Category("데님 팬츠", 2, category02.getId()));
        categoryRepository.save(new Category("코튼 팬츠", 2, category02.getId()));

        Category category03 = categoryRepository.save(new Category("아우터", 1));
        categoryRepository.save(new Category("카디건", 2, category03.getId()));
        categoryRepository.save(new Category("겨울 싱글 코트", 2, category03.getId()));
    }

    @DisplayName("자식 카테고리가 없는 카테고리 삭제는 본인 외에 다른 카테고리에 영향을 끼치지 않아야 한다.")
    @Test
    void deleteCategory01() throws Exception {
        //given
        Category category = categoryRepository.findByNameAndDepth("겨울 싱글 코트", 2).get();

        //when
        // 현재 등록된 카테고리 수는 9개 이다.
        int beforeSize = categoryRepository.findAll().size();
        assert beforeSize == 9;
        // 겨울 싱글 코트 카테고리를 부모로 갖는 카테고리가 없다. (자식 카테고리가 없는 카테고리)
        assert categoryRepository.findByParentId(category.getId()).isEmpty();

        //when & then
        mockMvc.perform(
                        delete(URI, category.getId()).contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(beforeSize - 1, categoryRepository.findAll().size());
    }

    @DisplayName("부모 카테고리를 삭제할 경우 자식 카테고리도 삭제되어야 한다.")
    @Test
    void deleteCategory02() throws Exception {
        //given
        Category category = categoryRepository.findByNameAndDepth("상의", 1).get();

        //when
        // 자식 카테고리를 가지고 있다. (부모 카테고리)
        assert !categoryRepository.findByParentId(category.getId()).isEmpty();

        //then
        mockMvc.perform(
                        delete(URI, category.getId()).contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertTrue(categoryRepository.findByNameAndDepth("반소매 티셔츠", 2).isEmpty());
        assertTrue(categoryRepository.findByNameAndDepth("시원해", 3).isEmpty());
    }

    @DisplayName("존재하지 않는 카테고리 id를 삭제 요청한 경우 400 응답을 리턴한다.")
    @Test
    void deleteCategory03() throws Exception {
        //given
        long categoryId = 123456789L;

        //when & then
        mockMvc.perform(
                        delete(URI, categoryId).contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("NO_DATA_FOUND")))
                .andExpect(jsonPath("$.msg", is("no data found exception with " + categoryId)));
    }
}
