package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;

import java.util.logging.Logger;

public abstract class AbstractScraper implements Scraper {
    final ProductRepository productRepository;
    final ShopRepository shopRepository;
    Logger logger;

    protected AbstractScraper(ProductRepository productRepository, ShopRepository shopRepository) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }
}