package service;

import com.musinsa.shop.domain.category.Category;
import com.musinsa.shop.domain.category.CategoryRepository;
import com.musinsa.shop.webapi.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ValidationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("자식 카테고리 등록 시 부모 카테고리 id 값 지정 여부에 따른 동작 확인")
    void existParentIdWithChild() {
        //given
        doReturn(Optional.empty()).when(categoryRepository).findById(111L);
        doReturn(Optional.of(mock(Category.class))).when(categoryRepository).findById(123123L);

        //when & then
        // 하위 카테고리 등록 시 부모 카테고리 id 값을 지정하지 않은 경우 벨리데이션 예외 발생
        ValidationException thrown01 = assertThrows(ValidationException.class, () -> categoryService.createCategory(new Category("카디건", 2)));
        assertEquals("parentId is null", thrown01.getMessage());

        // 하위 카테고리 등록 시 부모 카테고리 id 값을 지정하였으나 존재하지 않는 카테고리 id 값인 경우 벨리데이션 예외 발생
        ValidationException thrown02 = assertThrows(ValidationException.class, () -> categoryService.createCategory(new Category("카디건", 2, 111L)));
        assertEquals("parentId is not exist", thrown02.getMessage());

        assertDoesNotThrow(() -> categoryService.createCategory(new Category("카디건", 2, 123123L)));
    }

    @Test
    @DisplayName("카테고리명 중복 여부에 따른 동작 확인")
    void duplicatedCategoryName() {
        //given
        //2뎁스의 카디건 카테고리는 이미 존재한다.
        doReturn(Optional.of(mock(Category.class))).when(categoryRepository).findByNameAndDepth( "카디건", 2);
        doReturn(Optional.empty()).when(categoryRepository).findByNameAndDepth( "상의", 1);

        //when & then
        ValidationException thrown = assertThrows(ValidationException.class, () -> categoryService.createCategory(new Category("카디건", 2, 1L)));
        assertEquals("category name is duplicated", thrown.getMessage());

        assertDoesNotThrow(() ->categoryService.createCategory(new Category("상의", 1)));
    }

}
