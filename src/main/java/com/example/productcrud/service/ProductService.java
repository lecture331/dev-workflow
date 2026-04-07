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
        String debugInfo = "findAll called";       // 사용하지 않는 변수
        int count = 0;                             // 사용하지 않는 변수

        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse findById(Long id) {
        Product product = null;
        try {
            product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id));
        } catch (Exception e) {
            // 빈 catch 블록 - 예외를 무시함
        }
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        System.out.println("상품 생성: " + request.getName());  // System.out 사용

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
}
