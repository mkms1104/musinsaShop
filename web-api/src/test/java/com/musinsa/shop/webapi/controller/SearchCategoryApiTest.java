package com.musinsa.shop.webapi.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.show_sql=true"
})
@AutoConfigureMockMvc
@SpringBootTest
public class SearchCategoryApiTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private CategoryRepository categoryRepository;

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
        categoryRepository.save(new Category("슈트 팬츠/슬랙스", 2, category02.getId()));
        categoryRepository.save(new Category("트레이닝/조거 팬츠", 2, category02.getId()));
        categoryRepository.save(new Category("숏 팬츠", 2, category02.getId()));
        categoryRepository.save(new Category("레깅스", 2, category02.getId()));
        categoryRepository.save(new Category("점프 슡/오버올", 2, category02.getId()));
        categoryRepository.save(new Category("기타 바지", 2, category02.getId()));

        // ID = 7
        Category category03 = categoryRepository.save(new Category("아우터", 1, null));
        categoryRepository.save(new Category("카디건", 2, category03.getId()));
        categoryRepository.save(new Category("겨울 싱글 코트", 2, category03.getId()));
    }

    @Test
    @DisplayName("페이징 파라미터에 따라 올바르게 조회힌다.")
    public void getCategories01() throws Exception {
        //given
        Long categoryId = 4L; // parentId

        //when & then
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/v1/mshop/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("category_id", categoryId.toString())
                                .queryParam("page", "0")
                                .queryParam("size", "5")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8"));
        JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        JsonArray content = jsonObject.getAsJsonArray("content");
        assertEquals(5, content.size());
    }

    @Test
    @DisplayName("페이징 파라미터를 넘기지 않을 경우 기본으로 1페이지 10개를 조회힌다.")
    public void getCategories02() throws Exception {
        //given
        Long categoryId = 4L; // parentId

        //when & then
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/v1/mshop/categories/{category_id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8"));
        JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        JsonArray content = jsonObject.getAsJsonArray("content");
        assertEquals(8, content.size());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 id를 넘길 경우 400 응답을 리턴한다.")
    public void getCategories03() throws Exception {
        //given
        Long categoryId = 123L; // parentId

        //when & then
        mockMvc.perform(
                        get("/api/v1/mshop/categories/{category_id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("NO_DATA_FOUND")))
                .andExpect(jsonPath("$.msg", is("no data found exception with " + categoryId)));
    }

    @Test
    @DisplayName("하위 카테고리가 없는 경우 빈 배열을 리턴한다.")
    public void getCategories04() throws Exception {
        //given
        Long categoryId = 2L; // parentId

        //when & then
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/v1/mshop/categories/{category_id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8"));
        JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        JsonArray content = jsonObject.getAsJsonArray("content");
        assertTrue(content.isEmpty());
    }

}
