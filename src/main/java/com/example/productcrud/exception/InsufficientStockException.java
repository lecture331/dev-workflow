package com.example.productcrud.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(int currentStock, int requestedQuantity) {
        super("재고가 부족합니다. 현재 재고: " + currentStock + ", 요청 수량: " + requestedQuantity);
    }
}
