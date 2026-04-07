package com.example.productcrud.service;

import com.example.productcrud.domain.Product;
import com.example.productcrud.dto.ProductRequest;
import com.example.productcrud.dto.ProductResponse;
import com.example.productcrud.exception.InsufficientStockException;
import com.example.productcrud.exception.ProductNotFoundException;
import com.example.productcrud.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.update(request.getName(), request.getPrice(), request.getStock());
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
    }

    /**
     * 재고 차감 메서드
     * 이 메서드는 의도적으로 테스트가 작성되지 X
     * SonarCloud가 uncovered lines로 감지하는지 확인하기 위한 용도
     */
    @Transactional
    public ProductResponse decreaseStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(product.getStock(), quantity);
        }

        product.decreaseStock(quantity);
        return ProductResponse.from(product);
    }

    // ========================================================================
    // [SonarCloud 테스트용 코드]
    // 아래 주석을 해제하면 SonarCloud Quality Gate에서 위반이 감지됩니다.
    // PR 테스트 시 주석을 풀고 push하세요.
    // ========================================================================

    // --- 위반 1: 사용하지 않는 변수 (java:S1481) ---
    // public void sonarTest_unusedVariable() {
    //     String unused = "이 변수는 사용되지 않음";
    //     int count = 0;
    // }

    // --- 위반 2: 빈 catch 블록 (java:S108) ---
    // public void sonarTest_emptyCatch() {
    //     try {
    //         productRepository.findById(1L);
    //     } catch (Exception e) {
    //     }
    // }

    // --- 위반 3: System.out 사용 (java:S106) ---
    // public void sonarTest_systemOut() {
    //     System.out.println("디버깅용 출력");
    // }

    // --- 위반 4: 하드코딩된 비밀번호 (java:S6437) ---
    // private String password = "admin1234";
}
