package com.example.productcrud.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.productcrud.dto.ProductRequest;
import com.example.productcrud.dto.ProductResponse;
import com.example.productcrud.exception.GlobalExceptionHandler;
import com.example.productcrud.exception.ProductNotFoundException;
import com.example.productcrud.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("GET /products - 전체 상품 목록 조회")
    void findAll() throws Exception {
        // given
        ProductResponse response = new ProductResponse(1L, "테스트 상품", 10000, 100);
        given(productService.findAll()).willReturn(List.of(response));

        // when & then
        mockMvc.perform(get("/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("테스트 상품"));
    }

    @Test
    @DisplayName("GET /products/{id} - 상품 단건 조회")
    void findById() throws Exception {
        // given
        ProductResponse response = new ProductResponse(1L, "테스트 상품", 10000, 100);
        given(productService.findById(1L)).willReturn(response);

        // when & then
        mockMvc.perform(get("/products/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("테스트 상품"))
                .andExpect(jsonPath("$.price").value(10000));
    }

    @Test
    @DisplayName("GET /products/{id} - 존재하지 않는 상품 조회 시 404")
    void findById_notFound() throws Exception {
        // given
        given(productService.findById(999L)).willThrow(new ProductNotFoundException(999L));

        // when & then
        mockMvc.perform(get("/products/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /products - 상품 생성")
    void create() throws Exception {
        // given
        ProductRequest request = new ProductRequest("새 상품", 20000, 50);
        ProductResponse response = new ProductResponse(1L, "새 상품", 20000, 50);
        given(productService.create(any(ProductRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("새 상품"));
    }

    @Test
    @DisplayName("POST /products - 유효하지 않은 요청 시 400")
    void create_invalidRequest() throws Exception {
        // given
        ProductRequest request = new ProductRequest("", -1, -1);

        // when & then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /products/{id} - 상품 수정")
    void update() throws Exception {
        // given
        ProductRequest request = new ProductRequest("수정된 상품", 15000, 80);
        ProductResponse response = new ProductResponse(1L, "수정된 상품", 15000, 80);
        given(productService.update(eq(1L), any(ProductRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(put("/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정된 상품"));
    }

    @Test
    @DisplayName("DELETE /products/{id} - 상품 삭제")
    void deleteProduct() throws Exception {
        // given
        willDoNothing().given(productService).delete(1L);

        // when & then
        mockMvc.perform(delete("/products/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /products/{id} - 존재하지 않는 상품 삭제 시 404")
    void deleteProduct_notFound() throws Exception {
        // given
        willThrow(new ProductNotFoundException(999L)).given(productService).delete(999L);

        // when & then
        mockMvc.perform(delete("/products/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
