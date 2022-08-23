package com.musinsa.shop.webapi.controller;

import com.google.gson.JsonObject;
import com.musinsa.shop.domain.category.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CreateCategoryApiTest extends CategoryApiTestAround {
    private final String URI = "/api/v1/mshop/categories";

    @DisplayName("카테고리 정상 등록")
    @Test
    void createCategory01() throws Exception {
        //given
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "상의");
        jsonObject.addProperty("depth", 1);

        //when & then
        mockMvc.perform(
                        post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObject.toString())
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @DisplayName("필수 파라미터가 없을 경우 400 응답을 리턴한다.")
    @Test
    void createCategory02() {
        assertAll(
                // name 파라미터가 없다.
                () -> {
                    //given
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("depth", 1);

                    //when
                    assert !jsonObject.has("name");

                    //then
                    mockMvc.perform(
                                    post(URI).contentType(MediaType.APPLICATION_JSON)
                                            .content(jsonObject.toString())
                            )
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.errorType", is("INVALID_PARAMETER")))
                            .andExpect(jsonPath("$.msg", is("name param is null")));
                },

                // depth 파라미터가 없다.
                () -> {
                    //given
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("name", "상의");

                    //when
                    assert !jsonObject.has("depth");

                    //then
                    mockMvc.perform(
                                    post(URI).contentType(MediaType.APPLICATION_JSON)
                                            .content(jsonObject.toString())
                            )
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.errorType", is("INVALID_PARAMETER")))
                            .andExpect(jsonPath("$.msg", is("depth param is null")));
                }
        );
    }

    @DisplayName("등록하려는 카테고리명이 동일한 뎁스에 이미 존재할 경우 400 응답을 리턴한다.")
    @Test
    void createCategory03() throws Exception {
        //given
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "상의");
        jsonObject.addProperty("depth", 1);

        //when
        categoryRepository.save(new Category("상의", 1));
        assert categoryRepository.findByNameAndDepth("상의", 1).isPresent();

        //then
        mockMvc.perform(
                        post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObject.toString())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("NOT_VALID")))
                .andExpect(jsonPath("$.msg", is("category name is duplicated")));
    }

    @DisplayName("1뎁스가 아닌 카테고리를 부모 카테고리 선택 없이 등록한 경우 400 응답을 리턴한다.")
    @Test
    void createCategory04() throws Exception {
        //given
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("depth", 2);
        jsonObject.addProperty("name", "상의");

        //when
        assert jsonObject.get("depth").getAsInt() != 1;
        assert !jsonObject.has("parentId");

        //then
        mockMvc.perform(
                        post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObject.toString())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("NOT_VALID")))
                .andExpect(jsonPath("$.msg", is("parentId is null")));
    }

    @DisplayName("존재하지 않는 부모 카테고리 id 값을 입력했을 경우 400 응답을 리턴한다.")
    @Test
    void createCategory05() throws Exception {
        //given
        long parentId = 1L;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("depth", 2);
        jsonObject.addProperty("name", "상의");
        jsonObject.addProperty("parentId", parentId);

        //when
        assert categoryRepository.findById(parentId).isEmpty();

        //then
        mockMvc.perform(
                        post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObject.toString())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("NOT_VALID")))
                .andExpect(jsonPath("$.msg", is("parentId is not exist")));
    }

    @DisplayName("뎁스 값이 1~3 사이가 아닐 경우 400 응답을 리턴한다.")
    @Test
    void createCategory06() throws Exception {
        //given
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("depth", 5);
        jsonObject.addProperty("name", "상의");
        jsonObject.addProperty("parentId", 1);

        //when
        int paramWithDepth = jsonObject.get("depth").getAsInt();
        assert !(paramWithDepth >= 1 && 3 >= paramWithDepth);

        //then
        mockMvc.perform(
                        post(URI).contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObject.toString())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("INVALID_PARAMETER")))
                .andExpect(jsonPath("$.msg", is("depth must be a value between 1 and 3")));
    }
}
