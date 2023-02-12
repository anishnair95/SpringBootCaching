package com.javatechie.crud.example.service;

import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //BulkSave
    //Delete all the cache entry. So fetch will store new cache on method invocation
    @CacheEvict(value="userCache",allEntries = true)
    public List<Product> saveProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    //Single Product Save
    //Delete all the cache entry. So fetch will store new cache on method invocation
    @CacheEvict(value="userCache",allEntries = true)
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    //Get All Products
    @Cacheable(cacheNames = {"userCache"})
    public List<Product> getProducts() {
        LOGGER.info("Hitting DB for 1st time");
        return productRepository.findAll();
    }

    //Get Product By Id
    @Cacheable(value = "userCache",key="#id",unless="#result==null")
    public Product getProductById(int id) {

        LOGGER.info("Hitting DB for 1st time");
        return productRepository.findById(id).orElse(null);
    }


    //Get Product By Name

    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }


    //Update Product

    //cache if result is not null with key as id
    //Delete all the cache entry. So fetch will store new cache on method invocation
    @CacheEvict(value="userCache",allEntries = true)
    @CachePut(value="userCache",key="#product.id",unless = "#result==null")
    public Product updateProduct(Product product) {
        Product existingProduct = productRepository.findById(product.getId()).orElse(null);

        if(existingProduct==null) {
            return null;
        }
        existingProduct.setName(product.getName());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());
        return productRepository.save(existingProduct);
    }


    //Delete all the cache entry. So fetch will store new cache on method invocation
    @CacheEvict(value="userCache",allEntries = true)
    public String deleteProduct(int id) {
        LOGGER.info("Hitting DB for 1st time");
        Product productData = getProductById(id);

        if(productData!=null) {
            productRepository.deleteById(id);
            return "product removed !! " + id;
        }

        return "No product exist with id !! " + id;
    }





}
