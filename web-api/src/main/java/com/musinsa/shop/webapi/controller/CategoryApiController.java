package com.musinsa.shop.webapi.controller;

import com.musinsa.shop.domain.category.CategoryDto;
import com.musinsa.shop.webapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("api/v1/mshop")
@RestController
public class CategoryApiController {
    private final CategoryService categoryService;

    // 조회 확인을 위한 임시 핸들러
    @GetMapping("categories/{category_id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable("category_id") Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @PostMapping("categories")
    public ResponseEntity<Void> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Long id = categoryService.createCategory(categoryDto);
        URI uri = WebMvcLinkBuilder.linkTo(CategoryApiController.class).slash(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("categories/{category_id}")
    public ResponseEntity<Void> updateCategory(@PathVariable("category_id") Long id, @RequestBody CategoryDto categoryDto) {
        categoryService.updateCategoryName(id, categoryDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("categories/{category_id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("category_id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
