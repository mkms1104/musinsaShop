package com.musinsa.shop.webapi.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryUpdateDto {
    @NotBlank(message = "name param is null")
    private String name;

    public CategoryUpdateDto(String name) {
        this.name = name;
    }
}
