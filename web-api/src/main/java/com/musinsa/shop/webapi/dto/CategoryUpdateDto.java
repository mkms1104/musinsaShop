package com.musinsa.shop.webapi.dto;

import com.musinsa.shop.domain.category.Category;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@ToString
public class CategoryUpdateDto {
    @NotBlank(message = "name param is null")
    private String name;

    public Category toEntity() {
        return new Category(name);
    }
}
