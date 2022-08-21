package com.musinsa.shop.webapi.service;

import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryDto;
import com.musinsa.shop.domain.category.CategoryMapper;
import com.musinsa.shop.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Page<CategoryDto> getCategory(Long id, Pageable pageable) {
        Optional<Category> findCategoryOp = categoryRepository.findById(id);
        if(findCategoryOp.isEmpty()) throw new NoSuchElementException(id.toString());

        Page<Category> findCategoriesWithPage = categoryRepository.findByParentId(id, pageable);
        List<CategoryDto> categories = findCategoriesWithPage.stream()
                                        .map(v -> CategoryMapper.INSTANCE.toDto(v))
                                        .collect(Collectors.toList());

        return new PageImpl<>(categories, pageable, findCategoriesWithPage.getTotalPages());
    }

    @Transactional
    public Long createCategory(CategoryDto categoryDto) {
        Long parentId = categoryDto.getParentId();
        this.existCategory(categoryDto.getDepth(), categoryDto.getName());

        if(!categoryDto.isRoot() && Objects.isNull(parentId)) {
            throw new ValidationException("parentId is null");
        }

        if(!Objects.isNull(parentId)) {
            Optional<Category> parentCategoryOp = categoryRepository.findById(parentId);
            if(parentCategoryOp.isEmpty()) throw new ValidationException("parentId is not exist");
        }

        Category category = CategoryMapper.INSTANCE.toEntity(categoryDto);
        Category saved = categoryRepository.save(category);
        return saved.getId();
    }

    @Transactional
    public void updateCategoryName(Long id, CategoryDto categoryDto) {
        Optional<Category> findCategoryOp = categoryRepository.findById(id);
        Category category = findCategoryOp.orElseThrow(() -> { throw new NoSuchElementException(id.toString()); });
        this.existCategory(category.getDepth(), categoryDto.getName());

        category.updateCategoryName(categoryDto.getName());
    }

    @Transactional
    public void deleteCategory(Long id) {
        Optional<Category> findCategoryOp = categoryRepository.findById(id);
        Category category = findCategoryOp.orElseThrow(() -> { throw new NoSuchElementException(id.toString()); });
        categoryRepository.deleteById(id);
        categoryRepository.deleteByParentId(category.getId());
    }

    private void existCategory(int depth, String categoryName) {
        Optional<Category> findUniqueCategoryOp = categoryRepository.findByDepthAndName(depth, categoryName);
        if(findUniqueCategoryOp.isPresent()) throw new ValidationException("category name is duplicated");
    }
}
