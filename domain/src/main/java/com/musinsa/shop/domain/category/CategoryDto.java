package com.musinsa.shop.domain.category;

import lombok.*;

import javax.validation.constraints.NotNull;

@ToString @Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryDto {
    @NotNull
    private String name;
    @NotNull
    private Integer depth;
    private Long parentId;
}
