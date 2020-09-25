package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;

public abstract class AbstractScraper implements Scraper {
    final ProductRepository productRepository;
    final ShopRepository shopRepository;

    protected AbstractScraper(ProductRepository productRepository, ShopRepository shopRepository) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }
}