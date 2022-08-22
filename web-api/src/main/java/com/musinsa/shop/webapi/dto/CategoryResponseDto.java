package com.musinsa.shop.webapi.dto;

import com.musinsa.shop.domain.category.Category;
import lombok.Getter;

@Getter
public class CategoryResponseDto {
    private Long id;
    private String name;
    private Integer depth;
    private Long parentId;

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.depth = category.getDepth();
        this.parentId = category.getParentId();
    }
}
