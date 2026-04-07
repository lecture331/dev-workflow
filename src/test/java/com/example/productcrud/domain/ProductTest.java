package com.example.productcrud.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    @DisplayName("상품을 생성한다")
    void create() {
        Product product = Product.builder()
                .name("테스트 상품")
                .price(10000)
                .stock(100)
                .build();

        assertThat(product.getName()).isEqualTo("테스트 상품");
        assertThat(product.getPrice()).isEqualTo(10000);
        assertThat(product.getStock()).isEqualTo(100);
    }

    @Test
    @DisplayName("상품 정보를 수정한다")
    void update() {
        Product product = Product.builder()
                .name("원본")
                .price(1000)
                .stock(10)
                .build();

        product.update("수정", 2000, 20);

        assertThat(product.getName()).isEqualTo("수정");
        assertThat(product.getPrice()).isEqualTo(2000);
        assertThat(product.getStock()).isEqualTo(20);
    }

    @Test
    @DisplayName("재고를 차감한다")
    void decreaseStock() {
        Product product = Product.builder()
                .name("상품")
                .price(1000)
                .stock(10)
                .build();

        product.decreaseStock(3);

        assertThat(product.getStock()).isEqualTo(7);
    }

    @Test
    @DisplayName("재고 부족 시 예외가 발생한다")
    void decreaseStock_insufficient() {
        Product product = Product.builder()
                .name("상품")
                .price(1000)
                .stock(5)
                .build();

        assertThatThrownBy(() -> product.decreaseStock(10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다");
    }
}
