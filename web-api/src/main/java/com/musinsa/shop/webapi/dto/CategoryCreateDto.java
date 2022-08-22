package com.musinsa.shop.webapi.dto;

import com.musinsa.shop.domain.category.Category;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
public class CategoryCreateDto {
    @NotBlank(message = "name param is null")
    private String name;

    @NotNull(message = "depth param is null")
    @Min(value = 1, message = "depth must be a value between 1 and 3")
    @Max(value = 3, message = "depth must be a value between 1 and 3")
    private Integer depth;

    private Long parentId;

    public Category toEntity() {
        return new Category(name, depth, parentId);
    }
}
