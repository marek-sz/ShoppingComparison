package com.example.shoppingcomparison.service;

import com.example.shoppingcomparison.model.Product;
import com.example.shoppingcomparison.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ProductService {
    ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> populate() {
        return productRepository.findAllById(generateRandomIds());
    }

    // TODO: 2020-10-30 replace findAll with something less memory-draining
    public List<Product> getAllProducts(String category) {
        return productRepository.findAll()
                .stream()
                .filter(p -> p.getCategory().toString().equals(category))
                .collect(Collectors.toList());
    }

    public List<Product> findByName(String productName) {
        return productRepository.findAll()
                .stream()
                .filter(p -> p.getBrand().toLowerCase().contains(productName.toLowerCase().trim())
                        || p.getModel().toLowerCase().contains(productName.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    public List<Product> sortByPrice() {
        return productRepository.findAll(Sort.by("price"));
    }

    public List<Product> sortByName() {
        return productRepository.findAll(Sort.by("brand").and(Sort.by("model")));
    }

    public List<Product> sortByShop() {
        return productRepository.findAll(Sort.by("shop"));
    }

    private List<Long> generateRandomIds() {
        long[] longs = ThreadLocalRandom.current().longs(10, 1, productRepository.count()).toArray();
        return Arrays.stream(longs)
                .boxed()
                .collect(Collectors.toList());
    }
}
