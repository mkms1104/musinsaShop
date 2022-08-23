package com.musinsa.shop.domain.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndDepth(String name, int depth);

    Page<Category> findByParentId(Long parentId, Pageable pageable);

    List<Category> findByParentId(Long parentId);
}
