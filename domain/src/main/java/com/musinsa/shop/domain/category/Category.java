package com.musinsa.shop.domain.category;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@ToString @Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;
    @Column(unique = true)
    private String name;
    private Integer depth;
    private Long parentId;

    public Category(String name, Integer depth, Long parentId) {
        this.name = name;
        this.depth = depth;
        this.parentId = parentId;
    }

    public void updateCategoryName(String name) {
        this.name = name;
    }
}
