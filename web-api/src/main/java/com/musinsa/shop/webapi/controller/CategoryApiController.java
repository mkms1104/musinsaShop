package com.musinsa.shop.webapi.controller;

import com.musinsa.shop.domain.category.CategoryDto;
import com.musinsa.shop.webapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.musinsa.shop.domain.support.CategoryValidGroups.Create;
import static com.musinsa.shop.domain.support.CategoryValidGroups.Update;

@RequiredArgsConstructor
@RequestMapping("api/v1/mshop/categories")
@RestController
public class CategoryApiController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryDto>> getCategory(@RequestParam("category_id") Long id, @PageableDefault(sort = "name") Pageable pageable) {
        Page<CategoryDto> categoriesWithPage = categoryService.getCategory(id, pageable);
        return ResponseEntity.ok(categoriesWithPage);
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@Validated(Create.class) @RequestBody CategoryDto categoryDto) {
        Long id = categoryService.createCategory(categoryDto);
        URI uri = WebMvcLinkBuilder.linkTo(CategoryApiController.class).slash(id).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("{category_id}")
    public ResponseEntity<Void> updateCategory(@PathVariable("category_id") Long id, @Validated(Update.class) @RequestBody CategoryDto categoryDto) {
        categoryService.updateCategoryName(id, categoryDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{category_id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("category_id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

}
