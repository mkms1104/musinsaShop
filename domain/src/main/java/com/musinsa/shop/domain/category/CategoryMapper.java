package com.musinsa.shop.domain.category;

import com.musinsa.shop.domain.support.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper extends GenericMapper<CategoryDto, Category> {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
}
