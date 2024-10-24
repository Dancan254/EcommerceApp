package com.java.EcomerceApp.service.product;

import com.java.EcomerceApp.dto.ProductDTO;
import com.java.EcomerceApp.dto.ProductResponse;
import com.java.EcomerceApp.exception.ResourceNotFoundException;
import com.java.EcomerceApp.model.Category;
import com.java.EcomerceApp.model.Product;
import com.java.EcomerceApp.repository.CategoryRepository;
import com.java.EcomerceApp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        // Check if a product with the same name already exists
        if (productRepository.existsByProductName(productDTO.getProductName())) {
            throw new ResourceNotFoundException("Product with name: " + productDTO.getProductName() + " already exists");
        }
        //find the category by id
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        //map the productDTO to product
        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        //set the special price
        Double specialPrice = product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01);
        product.setSpecialPrice(specialPrice);
        //save the product then map to productDTO
        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
        return new ProductResponse(productDTOS);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId) {
        //find category by id
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        //List of products from db
        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        //check if list is empty
        if(products.isEmpty()){
            throw new ResourceNotFoundException("product list is empty");
        }
        //map the products to productDTO
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        return new ProductResponse(productDTOS);
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%");
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found for the keyword: " + keyword);
        }
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
        return new ProductResponse(productDTOS);
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO updateProduct) {
        // Find the productFromDb by id
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        Product product = modelMapper.map(updateProduct, Product.class);
        // Update the productFromDb details
        productFromDb.setProductName(updateProduct.getProductName());
        productFromDb.setProductDescription(updateProduct.getProductDescription());
        productFromDb.setPrice(updateProduct.getPrice());
        productFromDb.setQuantity(updateProduct.getQuantity());
        productFromDb.setDiscount(updateProduct.getDiscount());
        Double specialPrice = product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01);
        productFromDb.setSpecialPrice(specialPrice);
        // Save the updated productFromDb
        Product savedProduct = productRepository.save(productFromDb);

        // Return the updated productFromDb as a ProductDTO
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public String deleteProduct(Long productId) {
        // Find the product by id
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Delete the product
        productRepository.delete(product);

        return "Product deleted successfully";
    }
}
