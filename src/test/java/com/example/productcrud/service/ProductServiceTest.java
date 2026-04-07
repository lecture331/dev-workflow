package com.example.productcrud.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.example.productcrud.domain.Product;
import com.example.productcrud.dto.ProductRequest;
import com.example.productcrud.dto.ProductResponse;
import com.example.productcrud.exception.ProductNotFoundException;
import com.example.productcrud.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("전체 상품 목록을 조회한다")
    void findAll() {
        // given
        Product product = createProduct(1L, "테스트 상품", 10000, 100);
        given(productRepository.findAll()).willReturn(List.of(product));

        // when
        List<ProductResponse> result = productService.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("테스트 상품");
    }

    @Test
    @DisplayName("ID로 상품을 조회한다")
    void findById() {
        // given
        Product product = createProduct(1L, "테스트 상품", 10000, 100);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        ProductResponse result = productService.findById(1L);

        // then
        assertThat(result.getName()).isEqualTo("테스트 상품");
        assertThat(result.getPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외가 발생한다")
    void findById_notFound() {
        // given
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("상품을 생성한다")
    void create() {
        // given
        ProductRequest request = new ProductRequest("새 상품", 20000, 50);
        Product savedProduct = createProduct(1L, "새 상품", 20000, 50);
        given(productRepository.save(any(Product.class))).willReturn(savedProduct);

        // when
        ProductResponse result = productService.create(request);

        // then
        assertThat(result.getName()).isEqualTo("새 상품");
        assertThat(result.getPrice()).isEqualTo(20000);
        assertThat(result.getStock()).isEqualTo(50);
    }

    @Test
    @DisplayName("상품을 수정한다")
    void update() {
        // given
        Product product = createProduct(1L, "기존 상품", 10000, 100);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ProductRequest request = new ProductRequest("수정된 상품", 15000, 80);

        // when
        ProductResponse result = productService.update(1L, request);

        // then
        assertThat(result.getName()).isEqualTo("수정된 상품");
        assertThat(result.getPrice()).isEqualTo(15000);
        assertThat(result.getStock()).isEqualTo(80);
    }

    @Test
    @DisplayName("존재하지 않는 상품 수정 시 예외가 발생한다")
    void update_notFound() {
        // given
        given(productRepository.findById(999L)).willReturn(Optional.empty());
        ProductRequest request = new ProductRequest("수정", 10000, 10);

        // when & then
        assertThatThrownBy(() -> productService.update(999L, request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("상품을 삭제한다")
    void delete() {
        // given
        Product product = createProduct(1L, "삭제할 상품", 10000, 100);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        productService.delete(1L);

        // then
        then(productRepository).should(times(1)).delete(product);
    }

    @Test
    @DisplayName("존재하지 않는 상품 삭제 시 예외가 발생한다")
    void delete_notFound() {
        // given
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.delete(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // NOTE: decreaseStock() 메서드는 의도적으로 테스트를 작성하지 않았습니다.
    // SonarCloud에서 uncovered lines로 감지되는지 확인하기 위한 용도입니다.

    private Product createProduct(Long id, String name, int price, int stock) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .build();
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }
}
