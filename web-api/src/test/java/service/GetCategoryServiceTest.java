package service;

import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryRepository;
import com.musinsa.shop.webapi.dto.CategoryResponseDto;
import com.musinsa.shop.webapi.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class GetCategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @DisplayName("부모 카테고리 id 값을 지정하지 않는 경우 모든 카테고리를 조회한다.")
    @Test
    void getAllCategory() {
        //given
        List<Category> categories = List.of(
                new Category("상의", 1),
                new Category("반소매 티셔츠", 2, 1L),
                new Category("셔츠/블라우스", 2, 1L),
                new Category("바지", 1),
                new Category("데님 팬츠", 2, 4L),
                new Category("코튼 팬츠", 2, 4L),
                new Category("아우터", 1),
                new Category("카디건", 2, 7L),
                new Category("겨울 싱글 코트", 2, 7L),
                new Category("겨울 싱글 코트2", 2, 7L),
                new Category("겨울 싱글 코트3", 2, 7L)
        );

        given(categoryRepository.findAll(any(PageRequest.class)))
                .willAnswer(invocation -> {
                            Pageable pageable = invocation.getArgument(0);
                            int start = (int) pageable.getOffset();
                            int end = Math.min((start + pageable.getPageSize()), categories.size());
                            return new PageImpl<>(categories.subList(start, end), invocation.getArgument(0), categories.size());
                        }
                );


        //when
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<CategoryResponseDto> page = categoryService.getCategory(null, pageRequest); // 부모 카테고리 id 미지정

        //then
        assertEquals(3, page.getTotalPages());
        assertEquals(11, page.getTotalElements());
        assertEquals(0, page.getNumber());
        assertEquals(5, page.getSize());
        assertEquals(5, page.getNumberOfElements());
    }

    @DisplayName("지정한 부모 카테고리의 모든 하위 카테고리를 조회한다.")
    @Test
    void getCategory() {
        //given
        List<Category> categories = List.of(
                new Category("상의", 1),
                new Category("반소매 티셔츠", 2, 1L),
                new Category("긴소매 티셔츠", 2, 1L),
                new Category("셔츠/블라우스", 2, 1L),
                new Category("후드 티셔츠", 2, 1L),
                new Category("바지", 1),
                new Category("데님 팬츠", 2, 4L),
                new Category("코튼 팬츠", 2, 4L),
                new Category("아우터", 1),
                new Category("카디건", 2, 7L),
                new Category("겨울 싱글 코트", 2, 7L)
        );

        Category mockCategory = new Category("껍데기", 1);
        mockCategory.addChild(categories);

        given(categoryRepository.findById(any(Long.class))).willAnswer(invocation -> {
            long id = (Long) invocation.getArgument(0);
            List<Category> child = categories.stream().filter(v -> Objects.equals(id, v.getParentId())).collect(Collectors.toList());
            System.out.println(child.size());

            //chile 값이 세팅된 엔티티를 넘겨야한다.
            Category category = spy(Category.class);
            category.addChild(child);
            return Optional.of(category);
        });

        //when
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<CategoryResponseDto> page = categoryService.getCategory(1L, pageRequest); // 부모 카테고리 id 지정 (아무 값이나 무관)

        //then
        assertEquals(1, page.getTotalPages());
        assertEquals(4, page.getTotalElements());
        assertEquals(0, page.getNumber());
        assertEquals(5, page.getSize());
        assertEquals(4, page.getNumberOfElements());
    }

    @DisplayName("PageImpl 동작 테스트")
    @Test
    void pageImpl() {
        List<Category> categories = List.of(
                new Category("상의", 1),
                new Category("반소매 티셔츠", 2, 1L),
                new Category("셔츠/블라우스", 2, 1L),
                new Category("바지", 1),
                new Category("데님 팬츠", 2, 4L),
                new Category("코튼 팬츠", 2, 4L),
                new Category("아우터", 1),
                new Category("카디건", 2, 7L),
                new Category("겨울 싱글 코트", 2, 7L)
        );

        PageRequest pageRequest = PageRequest.of(0, 5);
        PageImpl<Category> page = new PageImpl<>(categories, pageRequest, categories.size());

        assertEquals(2, page.getTotalPages());
        assertEquals(9, page.getTotalElements());
        assertEquals(0, page.getNumber());
        assertEquals(5, page.getSize()); // 지정한 size
        assertEquals(9, page.getNumberOfElements());
    }
}
