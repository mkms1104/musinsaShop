package com.musinsa.shop.webapi.controller;

import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.webapi.dto.CategoryCreateDto;
import com.musinsa.shop.webapi.dto.CategoryResponseDto;
import com.musinsa.shop.webapi.dto.CategoryUpdateDto;
import com.musinsa.shop.webapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("api/v1/mshop/categories")
@RestController
public class CategoryApiController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDto>> getCategory(
            @RequestParam(value = "category_id", required = false) Long id,
            @PageableDefault(sort = "name") Pageable pageable
    ) {
        Page<CategoryResponseDto> categoriesWithPage = categoryService.getCategory(id, pageable);
        return ResponseEntity.ok(categoriesWithPage);
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@Valid @RequestBody CategoryCreateDto dto) {
        Category savedCategory = categoryService.createCategory(dto);
        URI uri = WebMvcLinkBuilder.linkTo(CategoryApiController.class).slash(savedCategory.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("{category_id}")
    public ResponseEntity<Void> updateCategory(@PathVariable("category_id") Long id, @Valid @RequestBody CategoryUpdateDto dto) {
        categoryService.updateCategoryName(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{category_id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("category_id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
