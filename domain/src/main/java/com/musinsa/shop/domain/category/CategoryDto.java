package com.musinsa.shop.domain.category;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

import java.util.Objects;

import static com.musinsa.shop.domain.support.CategoryValidGroups.Create;
import static com.musinsa.shop.domain.support.CategoryValidGroups.Update;

@ToString @Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryDto {
    @NotBlank(groups = {Create.class, Update.class}, message = "name param is null")
    private String name;

    @NotNull(groups = Create.class, message = "depth param is null")
    @Min(groups = Create.class, value = 1, message = "depth must be a value between 1 and 3")
    @Max(groups = Create.class, value = 3, message = "depth must be a value between 1 and 3")
    private Integer depth;

    private Long parentId;

    public boolean isRoot() {
        return this.depth == 1;
    }
}
