package com.musinsa.shop.webapi.controller;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CategoryApiControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    public void createCategory() throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "상의");
        jsonObject.addProperty("depth", 1);

        mockMvc.perform(
                post("/api/v1/mshop/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString())
        )
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
    }
}