package com.java.EcomerceApp.controller;

import com.java.EcomerceApp.dto.ProductDTO;
import com.java.EcomerceApp.dto.ProductResponse;
import com.java.EcomerceApp.model.Product;
import com.java.EcomerceApp.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/admin/category/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody @Valid ProductDTO productDTO,
                                                 @PathVariable Long categoryId) {
        return new ResponseEntity<>(productService.addProduct(productDTO, categoryId),HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public ResponseEntity<ProductResponse> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.FOUND);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        return new ResponseEntity<>(productService.getProductsByCategory(categoryId), HttpStatus.FOUND);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword) {
        return new ResponseEntity<>(productService.getProductsByKeyword(keyword), HttpStatus.FOUND);
    }

    @PutMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductDTO productDTO) {
        return new ResponseEntity<>(productService.updateProduct(productId, productDTO), HttpStatus.OK);
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        String result = productService.deleteProduct(productId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
