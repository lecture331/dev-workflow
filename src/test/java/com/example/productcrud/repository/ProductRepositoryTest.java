package com.example.productcrud.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.productcrud.domain.Product;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 저장하고 조회한다")
    void saveAndFindById() {
        // given
        Product product = Product.builder()
                .name("테스트 상품")
                .price(10000)
                .stock(100)
                .build();

        // when
        Product saved = productRepository.save(product);
        Optional<Product> found = productRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("테스트 상품");
        assertThat(found.get().getPrice()).isEqualTo(10000);
        assertThat(found.get().getStock()).isEqualTo(100);
    }

    @Test
    @DisplayName("전체 상품 목록을 조회한다")
    void findAll() {
        // given
        productRepository.save(Product.builder().name("상품1").price(1000).stock(10).build());
        productRepository.save(Product.builder().name("상품2").price(2000).stock(20).build());

        // when
        List<Product> products = productRepository.findAll();

        // then
        assertThat(products).hasSize(2);
    }

    @Test
    @DisplayName("상품을 삭제한다")
    void delete() {
        // given
        Product product = productRepository.save(
                Product.builder().name("삭제할 상품").price(5000).stock(30).build()
        );

        // when
        productRepository.delete(product);
        Optional<Product> found = productRepository.findById(product.getId());

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("상품 정보를 수정한다")
    void update() {
        // given
        Product product = productRepository.save(
                Product.builder().name("원본 상품").price(10000).stock(100).build()
        );

        // when
        product.update("수정된 상품", 20000, 200);
        productRepository.flush();

        Product found = productRepository.findById(product.getId()).orElseThrow();

        // then
        assertThat(found.getName()).isEqualTo("수정된 상품");
        assertThat(found.getPrice()).isEqualTo(20000);
        assertThat(found.getStock()).isEqualTo(200);
    }
}
