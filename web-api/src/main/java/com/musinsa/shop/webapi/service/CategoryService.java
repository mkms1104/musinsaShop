package com.musinsa.shop.webapi.service;

import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryDto;
import com.musinsa.shop.domain.category.CategoryMapper;
import com.musinsa.shop.domain.category.CategoryRepository;
import com.musinsa.shop.webapi.exception.NoDataFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryDto getCategory(Long id) {
        Optional<Category> findCategoryOp = categoryRepository.findById(id);
        Category category = findCategoryOp.orElseThrow(() -> { throw new NoDataFoundException(id); });
        return CategoryMapper.INSTANCE.toDto(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Transactional
    public Long createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.INSTANCE.toEntity(categoryDto);
        Category saved = categoryRepository.save(category);
        return saved.getId();
    }

    @Transactional
    public void updateCategoryName(Long id, CategoryDto categoryDto) {
        Optional<Category> findCategoryOp = categoryRepository.findById(id);
        Category category = findCategoryOp.orElseThrow(() -> { throw new NoDataFoundException(id); });
        category.updateCategoryName(categoryDto.getName());
    }
}
