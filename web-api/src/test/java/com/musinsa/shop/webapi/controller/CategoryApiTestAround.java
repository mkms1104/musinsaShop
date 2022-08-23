package com.musinsa.shop.webapi.controller;

import com.musinsa.shop.domain.category.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import testSupport.MockMvcTestSupport;

public abstract class CategoryApiTestAround extends MockMvcTestSupport {
    @Autowired
    protected CategoryRepository categoryRepository;

    @BeforeEach
    void init() {
        categoryRepository.deleteAll();
        beforeEach();
    }

    protected void beforeEach() {
    }

    @AfterEach
    void clean() {
        categoryRepository.deleteAll();
        afterEach();
    }

    protected void afterEach() {
    }
}
