package com.musinsa.shop.domain.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    Optional<Category> findByNameAndDepth(String name, int depth);

    Page<Category> findByParentId(Long parentId, Pageable pageable);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete from Category c where c.parentId = ?1")
    void deleteByParentId(Long parentId);
}
