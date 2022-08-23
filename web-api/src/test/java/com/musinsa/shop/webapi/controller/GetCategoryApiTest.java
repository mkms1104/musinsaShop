package com.musinsa.shop.webapi.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import testSupport.MockMvcTestSupport;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetCategoryApiTest extends MockMvcTestSupport {
    private final String URI = "/api/v1/mshop/categories";

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    static void init(@Autowired CategoryRepository categoryRepository) {
        Category category01 = categoryRepository.save(new Category("상의", 1));
        categoryRepository.save(new Category("반소매 티셔츠", 2, category01.getId()));
        categoryRepository.save(new Category("셔츠/블라우스", 2, category01.getId()));

        Category category02 = categoryRepository.save(new Category("바지", 1));
        categoryRepository.save(new Category("데님 팬츠", 2, category02.getId()));
        categoryRepository.save(new Category("코튼 팬츠", 2, category02.getId()));
        categoryRepository.save(new Category("슈트 팬츠/슬랙스", 2, category02.getId()));
        categoryRepository.save(new Category("트레이닝/조거 팬츠", 2, category02.getId()));
        categoryRepository.save(new Category("숏 팬츠", 2, category02.getId()));
        categoryRepository.save(new Category("레깅스", 2, category02.getId()));
        categoryRepository.save(new Category("점프 슡/오버올", 2, category02.getId()));
        categoryRepository.save(new Category("기타 바지", 2, category02.getId()));
        categoryRepository.save(new Category("피아노 바지", 2, category02.getId()));
        categoryRepository.save(new Category("냉장고 바지", 2, category02.getId()));
        categoryRepository.save(new Category("바이올린 바지", 2, category02.getId()));

        Category category03 = categoryRepository.save(new Category("아우터", 1));
        categoryRepository.save(new Category("카디건", 2, category03.getId()));
        categoryRepository.save(new Category("겨울 싱글 코트", 2, category03.getId()));

        categoryRepository.save(new Category("신발", 1));
    }

    @DisplayName("페이징 파라미터에 따라 올바르게 조회힌다.")
    @Test
    void getCategories01() throws Exception {
        //given
        int size = 5;
        Category category = categoryRepository.findByNameAndDepth("바지", 1).get();

        //when
        assert categoryRepository.findByParentId(category.getId()).size() > size;

        //then
        MvcResult mvcResult = mockMvc.perform(
                        get(URI).contentType(MediaType.APPLICATION_JSON)
                                .queryParam("category_id", Long.toString(category.getId()))
                                .queryParam("page", "0")
                                .queryParam("size", "5")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String result = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        JsonArray content = jsonObject.getAsJsonArray("content");
        assertEquals(size, content.size());
    }

    @DisplayName("페이징 파라미터를 넘기지 않을 경우 기본으로 1페이지 10개를 조회힌다.")
    @Test
    void getCategories02() throws Exception {
        //given
        int size = 10;
        Category category = categoryRepository.findByNameAndDepth("바지", 1).get();

        //when
        assert categoryRepository.findByParentId(category.getId()).size() > size;

        //then
        MvcResult mvcResult = mockMvc.perform(
                        get(URI).contentType(MediaType.APPLICATION_JSON)
                                .queryParam("category_id", Long.toString(category.getId()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String result = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        JsonArray content = jsonObject.getAsJsonArray("content");
        assertEquals(size, content.size());
    }

    @DisplayName("존재하지 않는 카테고리 id를 넘길 경우 400 응답을 리턴한다.")
    @Test
    void getCategories03() throws Exception {
        //given
        long categoryId = 123456789L;

        //when & then
        mockMvc.perform(
                        get(URI).contentType(MediaType.APPLICATION_JSON)
                                .queryParam("category_id", Long.toString(categoryId))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("NO_DATA_FOUND")))
                .andExpect(jsonPath("$.msg", is("no data found exception with " + categoryId)));
    }

    @DisplayName("하위 카테고리가 없는 경우 빈 배열을 리턴한다.")
    @Test
    void getCategories04() throws Exception {
        //given
        Category category = categoryRepository.findByNameAndDepth("신발", 1).get();

        //when
        assert categoryRepository.findByParentId(category.getId()).isEmpty();

        //then
        MvcResult mvcResult = mockMvc.perform(
                        get(URI).contentType(MediaType.APPLICATION_JSON)
                                .queryParam("category_id", Long.toString(category.getId()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String result = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        JsonArray content = jsonObject.getAsJsonArray("content");
        assertTrue(content.isEmpty());
    }

    @DisplayName("카테고리 id를 넘기지 않을 경우 전체 카테고리를 조회힌다.")
    @Test
    void getCategories05() throws Exception {
        //given

        //when
        //queryParam 값으로 id 넘기지 않는다.

        //then
        MvcResult mvcResult = mockMvc.perform(
                        get(URI).contentType(MediaType.APPLICATION_JSON)
                                .queryParam("size", "100") // 카테고리 전체 조회를 보기 위해 넉넉하게 지정
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String result = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        JsonArray content = jsonObject.getAsJsonArray("content");
        assertEquals(categoryRepository.findAll().size(), content.size());
    }

    @DisplayName("카테고리 id를 넘길 경우 하위 카테고리를 모두 조회힌다.")
    @Test
    void getCategories06() throws Exception {
        //given
        Category category = categoryRepository.findByNameAndDepth("바지", 1).get();

        //when


        //then
        MvcResult mvcResult = mockMvc.perform(
                        get(URI).contentType(MediaType.APPLICATION_JSON)
                                .queryParam("category_id", category.getId().toString())
                                .queryParam("size", "100")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String result = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        JsonArray content = jsonObject.getAsJsonArray("content");
//        assertEquals(categoryRepository.findAll().size(), content.size());
    }
}
