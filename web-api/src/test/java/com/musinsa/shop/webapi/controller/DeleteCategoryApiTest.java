package com.musinsa.shop.webapi.controller;

import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.show_sql=true"
})
@AutoConfigureMockMvc
@SpringBootTest
public class DeleteCategoryApiTest {
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

        // ID = 7
        Category category03 = categoryRepository.save(new Category("아우터", 1, null));
        categoryRepository.save(new Category("카디건", 2, category03.getId()));
        categoryRepository.save(new Category("겨울 싱글 코트", 2, category03.getId()));
    }

    @Test
    @DisplayName("카테고리 정상 삭제")
    public void deleteCategory01() throws Exception {
        //given
        Long categoryId = 2L;

        //when & then
        mockMvc.perform(
                        delete("/api/v1/mshop/categories/{category_id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;

        // 이상한거 안지워졌나 확인
        assertEquals(8, categoryRepository.findAll().size());
    }

    @Test
    @DisplayName("부모 카테고리를 삭제할 경우 자식 카테고리도 삭제되어야 한다.")
    public void deleteCategory02() throws Exception {
        //given
        Long categoryId = 1L;

        //when
        mockMvc.perform(
                        delete("/api/v1/mshop/categories/{category_id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;

        //then
        Page<Category> findChildCategories = categoryRepository.findByParentId(categoryId, Pageable.ofSize(10));
        assertTrue(findChildCategories.isEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 id를 삭제 요청한 경우 400 응답을 리턴한다.")
    public void deleteCategory03() throws Exception {
        //given
        Long categoryId = 123L;

        //when & then
        mockMvc.perform(
                        delete("/api/v1/mshop/categories/{category_id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("NO_DATA_FOUND")))
                .andExpect(jsonPath("$.msg", is("no data found exception with " + categoryId)));
        ;
    }
}
