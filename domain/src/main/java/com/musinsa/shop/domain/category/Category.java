package com.musinsa.shop.domain.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.ValidationException;
import java.util.Objects;

@ToString @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    uniqueConstraints={
            @UniqueConstraint(columnNames={"name", "depth"}, name = "uniqueConstraintWithNameAndDepth")
    }
)
@Entity
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;
    private String name;
    private Integer depth;
    private Long parentId;

//    @ToString.Exclude
//    @OneToMany(fetch = FetchType.LAZY)
//    private List<Category> childCategories;

    // ============ construct ============ //
    public Category(String name, int depth, Long parentId) {
        this.name = name;
        this.depth = depth;
        this.parentId = parentId;
    }

    public Category(String name, int depth) {
        this(name, depth, null);
    }

    public Category(String name) {
        this.name = name;
    }

    // ============ 비지니스 로직 ============ //
    public void updateCategoryName(String name) {
        this.name = name;
    }

    public boolean isRoot() {
        return depth == 1;
    }
    public void validExistParentIdWithRoot() {
        if (!isRoot() && Objects.isNull(parentId)) {
            throw new ValidationException("parentId is null");
        }
    }
}
