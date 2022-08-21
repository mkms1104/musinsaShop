package com.musinsa.shop.webapi.service;

import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryRepository;
import com.musinsa.shop.webapi.dto.CategoryResponseDto;
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

    public Page<CategoryResponseDto> getCategory(Long id, Pageable pageable) {
        // 전체 카테고리 조회
        if(Objects.isNull(id)) {
            Page<Category> allCategoriesWithPage = categoryRepository.findAll(pageable);
            return categoryResponseDtoBasedPage(allCategoriesWithPage, pageable);
        }

        Optional<Category> findCategoryOp = categoryRepository.findById(id);
        if(findCategoryOp.isEmpty()) throw new NoSuchElementException(id.toString());

        Page<Category> findCategoriesWithPage = categoryRepository.findByParentId(id, pageable);
        return categoryResponseDtoBasedPage(findCategoriesWithPage, pageable);
    }

    private PageImpl categoryResponseDtoBasedPage(Page<Category> categoriesWithPage, Pageable pageable) {
        List<CategoryResponseDto> categories = categoriesWithPage.stream()
                .map(v -> new CategoryResponseDto(v))
                .collect(Collectors.toList());

        return new PageImpl<>(categories, pageable, categoriesWithPage.getTotalPages());
    }

    @Transactional
    public Long createCategory(Category category) {
        Long parentId = category.getParentId();
        this.existCategory(category.getDepth(), category.getName());
        category.validExistParentIdWithRoot();

        if(!Objects.isNull(parentId)) {
            Optional<Category> parentCategoryOp = categoryRepository.findById(parentId);
            if(parentCategoryOp.isEmpty()) throw new ValidationException("parentId is not exist");
        }

        Category saved = categoryRepository.save(category);
        return saved.getId();
    }

    @Transactional
    public void updateCategoryName(Long id, Category category) {
        Optional<Category> findCategoryOp = categoryRepository.findById(id);
        Category findCategory = findCategoryOp.orElseThrow(() -> { throw new NoSuchElementException(id.toString()); });
        this.existCategory(findCategory.getDepth(), category.getName());

        findCategory.updateCategoryName(category.getName());
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
