package com.musinsa.shop.webapi.controller;

import com.google.gson.JsonObject;
import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateCategoryApiTest extends MockMvcTestSupport {
    @Autowired private CategoryRepository categoryRepository;

    private final String URI = "/api/v1/mshop/categories/{category_id}";

    @BeforeEach
    public void init() {
        // ID = 1
        Category category01 = categoryRepository.save(new Category("상의", 1, null));
        categoryRepository.save(new Category("반소매 티셔츠", 2, category01.getId()));
        categoryRepository.save(new Category("셔츠/블라우스", 2, category01.getId()));

        // ID = 4
        Category category02 = categoryRepository.save(new Category("바지", 1, null));
        categoryRepository.save(new Category("데님 팬츠", 2, category02.getId()));
        categoryRepository.save(new Category("코튼 팬츠", 2, category02.getId()));

        // ID = 7
        Category category03 = categoryRepository.save(new Category("아우터", 1, null));
        categoryRepository.save(new Category("카디건", 2, category03.getId()));
        categoryRepository.save(new Category("겨울 싱글 코트", 2, category03.getId()));
    }

    @Test
    @DisplayName("카테고리명 정상 업데이트")
    public void updateCategory01() throws Exception {
        //given
        Long categoryId = 1L; // 1뎁스 카테고리 ID
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "가방");

        //when & then
        mockMvc.perform(
                    patch(URI, categoryId).contentType(MediaType.APPLICATION_JSON)
                    .content(jsonObject.toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("수정하려는 카테고리명을 입력하지 않았을 경우 400 응답을 리턴한다.")
    public void updateCategory02() throws Exception {
        //given
        Long categoryId = 1L; // 1뎁스 카테고리 ID
        JsonObject jsonObject = new JsonObject();

        //when & then
        mockMvc.perform(
                    patch(URI, categoryId).contentType(MediaType.APPLICATION_JSON)
                    .content(jsonObject.toString())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("INVALID_PARAMETER")))
                .andExpect(jsonPath("$.msg", is("name param is null")))
        ;
    }

    @Test
    @DisplayName("수정하려는 카테고리명이 동일한 뎁스에 이미 존재할 경우 400 응답을 리턴한다.")
    public void updateCategory03() throws Exception {
        //given
        Long categoryId = 2L; // 1뎁스 카테고리 ID
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "바지"); // 1뎁스 카테고리에 바지라는 명칭이 이미 존재한다.

        //when & then
        mockMvc.perform(
                    patch(URI, 1).contentType(MediaType.APPLICATION_JSON)
                    .content(jsonObject.toString())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("NOT_VALID")))
                .andExpect(jsonPath("$.msg", is("category name is duplicated")))
        ;
    }

    @Test
    @DisplayName("수정하려는 카테고리명이 다른 뎁스에 존재할 경우 정상 업데이트한다.")
    public void updateCategory04() throws Exception {
        //given
        Long categoryId = 2L; // 2뎁스 카테고리 ID
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "바지"); // 2뎁스 카테고리에 바지라는 명칭은 없다.

        //when & then
        mockMvc.perform(
                    patch(URI, categoryId).contentType(MediaType.APPLICATION_JSON)
                    .content(jsonObject.toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 id를 수정 요청한 경우 400 응답을 리턴한다.")
    public void updateCategory05() throws Exception {
        //given
        Long categoryId = 123L;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "바지");

        //when & then
        mockMvc.perform(
                    patch(URI, categoryId).contentType(MediaType.APPLICATION_JSON)
                    .content(jsonObject.toString())

                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("NO_DATA_FOUND")))
                .andExpect(jsonPath("$.msg", is("no data found exception with " + categoryId)))
        ;
    }
}
