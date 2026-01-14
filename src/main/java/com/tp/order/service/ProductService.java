package com.tp.order.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tp.order.dto.ProductDTO;
import com.tp.order.entity.Product;
import com.tp.order.exception.ResourceNotFoundException;
import com.tp.order.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {
    
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductDTO getProductById(Long id) {
        log.debug("Fetching the product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        if (product.getDeleted()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        
        return mapToDTO(product);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.debug("Fetching all the products ::::::::::");
        return productRepository.findByDeletedFalse(pageable)
                .map(this::mapToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String name, BigDecimal minPrice, 
                                           BigDecimal maxPrice, Boolean available, 
                                           Pageable pageable) {
        log.debug("Searching products with filters - name: {}, minPrice: {}, maxPrice: {}, available: {}", 
                name, minPrice, maxPrice, available);
        return productRepository.searchProducts(name, minPrice, maxPrice, available, pageable)
                .map(this::mapToDTO);
    }
    
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating new product: {}", productDTO.name());
        Product product = Product.builder()
                .name(productDTO.name())
                .description(productDTO.description())
                .price(productDTO.price())
                .quantity(productDTO.quantity())
                .deleted(false)
                .build();
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return mapToDTO(savedProduct);
    }
    
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        if (product.getDeleted()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        
        product.setName(productDTO.name());
        product.setDescription(productDTO.description());
        product.setPrice(productDTO.price());
        product.setQuantity(productDTO.quantity());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", id);
        return mapToDTO(updatedProduct);
    }
    
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        log.info("the product deleted with product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        product.setDeleted(true);
        productRepository.save(product);
        log.info("Product deleted successfully with id: {}", id);
    }
    
    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        log.debug("Decreasing stock for product id: {} by quantity: {}", productId, quantity);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        if (product.getQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
        }
        
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }
    
    private ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getDeleted(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
