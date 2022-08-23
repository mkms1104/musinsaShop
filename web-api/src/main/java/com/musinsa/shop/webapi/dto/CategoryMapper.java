package com.musinsa.shop.webapi.dto;

import com.musinsa.shop.domain.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toEntity(CategoryCreateDto categoryCreateDto);

    Category toEntity(CategoryUpdateDto categoryUpdateDto);

    CategoryResponseDto toResponseDto(Category category);
}
