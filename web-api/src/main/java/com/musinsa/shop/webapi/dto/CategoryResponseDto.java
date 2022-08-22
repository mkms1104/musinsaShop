package com.musinsa.shop.webapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private Integer depth;
    private Long parentId;
}
