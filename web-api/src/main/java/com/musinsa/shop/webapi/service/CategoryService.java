package com.musinsa.shop.webapi.service;

import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryRepository;
import com.musinsa.shop.webapi.dto.CategoryCreateDto;
import com.musinsa.shop.webapi.dto.CategoryMapper;
import com.musinsa.shop.webapi.dto.CategoryResponseDto;
import com.musinsa.shop.webapi.dto.CategoryUpdateDto;
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
        return Objects.isNull(id) ? getAllCategories(pageable) : getChildCategories(id, pageable);
    }

    // 하위 카테고리 조회
    private Page<CategoryResponseDto> getChildCategories(Long parentId, Pageable pageable) {
        Optional<Category> findCategoryOp = categoryRepository.findById(parentId);
        if (findCategoryOp.isEmpty()) throw new NoSuchElementException(parentId.toString());

        Page<Category> findCategoriesWithPage = categoryRepository.findByParentId(parentId, pageable);
        return categoryResponseDtoBasedPage(findCategoriesWithPage, pageable);
    }

    // 전체 카테고리 조회
    private Page<CategoryResponseDto> getAllCategories(Pageable pageable) {
        Page<Category> allCategoriesWithPage = categoryRepository.findAll(pageable);
        return categoryResponseDtoBasedPage(allCategoriesWithPage, pageable);
    }

    private PageImpl<CategoryResponseDto> categoryResponseDtoBasedPage(Page<Category> categoriesWithPage, Pageable pageable) {
        List<CategoryResponseDto> categories = categoriesWithPage.stream()
                .map(v -> CategoryMapper.INSTANCE.toResponseDto(v))
                .collect(Collectors.toList());

        return new PageImpl<>(categories, pageable, categoriesWithPage.getTotalPages());
    }

    @Transactional
    public Category createCategory(CategoryCreateDto dto) {
        Category category = CategoryMapper.INSTANCE.toEntity(dto);

        Long parentId = category.getParentId();
        this.existCategory(category.getDepth(), category.getName());
        category.validExistParentIdWithChild();

        if (!Objects.isNull(parentId)) {
            Optional<Category> parentCategoryOp = categoryRepository.findById(parentId);
            if (parentCategoryOp.isEmpty()) throw new ValidationException("parentId is not exist");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void updateCategoryName(Long id, CategoryUpdateDto dto) {
        Category category = CategoryMapper.INSTANCE.toEntity(dto);

        Optional<Category> findCategoryOp = categoryRepository.findById(id);
        Category findCategory = findCategoryOp.orElseThrow(() -> {
            throw new NoSuchElementException(id.toString());
        });
        this.existCategory(findCategory.getDepth(), category.getName());

        findCategory.updateCategoryName(category.getName());
    }

    @Transactional
    public void deleteCategory(Long id) {
        Optional<Category> findCategoryOp = categoryRepository.findById(id);
        Category category = findCategoryOp.orElseThrow(() -> {
            throw new NoSuchElementException(id.toString());
        });
        categoryRepository.deleteById(id);
        categoryRepository.deleteByParentId(category.getId());
    }

    private void existCategory(int depth, String categoryName) {
        Optional<Category> findUniqueCategoryOp = categoryRepository.findByNameAndDepth(categoryName, depth);
        if (findUniqueCategoryOp.isPresent()) throw new ValidationException("category name is duplicated");
    }
}
