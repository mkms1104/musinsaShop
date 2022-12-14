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

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Page<CategoryResponseDto> getCategory(Long id, Pageable pageable) {
        if (pageable.getPageSize() > 100) throw new ValidationException("max page size is 100"); // 대량 조회 방지
        return Objects.isNull(id) ? getAllCategories(pageable) : getChildCategories(id, pageable);
    }

    // 하위 카테고리 조회
    private Page<CategoryResponseDto> getChildCategories(Long parentId, Pageable pageable) {
        Optional<Category> findCategoryOp = categoryRepository.findById(parentId);
        if (findCategoryOp.isEmpty()) throw new NoSuchElementException(parentId.toString());

        List<Category> child = findCategoryOp.get().getChild();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), child.size());
        return new PageImpl<>(child.subList(start, end), pageable, child.size()).map(CategoryMapper.INSTANCE::toResponseDto);
    }

    // 전체 카테고리 조회
    private Page<CategoryResponseDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(CategoryMapper.INSTANCE::toResponseDto);
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
        if (findCategoryOp.isEmpty()) throw new NoSuchElementException(id.toString());

        categoryRepository.deleteById(id);
    }

    private void existCategory(int depth, String categoryName) {
        Optional<Category> findUniqueCategoryOp = categoryRepository.findByNameAndDepth(categoryName, depth);
        if (findUniqueCategoryOp.isPresent()) throw new ValidationException("category name is duplicated");
    }
}
