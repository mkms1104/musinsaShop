package com.musinsa.shop.domain.category;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.List;

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

    public boolean isRoot() {
        return depth == 1;
    }

    public Category(String name, int depth, Long parentId) {
        this.name = name;
        this.depth = depth;
        this.parentId = parentId;
    }

    public void updateCategoryName(String name) {
        this.name = name;
    }
}
