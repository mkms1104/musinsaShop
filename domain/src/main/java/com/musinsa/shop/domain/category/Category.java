package com.musinsa.shop.domain.category;

import lombok.*;

import javax.persistence.*;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString(exclude = {"child"})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "depth"}, name = "uniqueConstraintWithNameAndDepth")
        }
)
@Entity
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;
    private String name;
    private Integer depth;
    private Long parentId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "parentId")
    private List<Category> child = new ArrayList<>();

    public void addChild(List<Category> child) {
        this.child = child;
    }

    // ============ construct ============ //

    @Builder
    public Category(String name, int depth, Long parentId, List<Category> child) {
        this.name = name;
        this.depth = depth;
        this.parentId = parentId;
        this.child = child;
    }

    public Category(String name, int depth, Long parentId) {
        this.name = name;
        this.depth = depth;
        this.parentId = parentId;
    }

    public Category(String name, int depth) {
        this(name, depth, null);
    }

    // ============ 비지니스 로직 ============ //
    public void updateCategoryName(String name) {
        this.name = name;
    }

    public boolean isRoot() {
        return depth == 1;
    }

    public void validExistParentIdWithChild() {
        if (!isRoot() && Objects.isNull(parentId)) {
            throw new ValidationException("parentId is null");
        }
    }
}
