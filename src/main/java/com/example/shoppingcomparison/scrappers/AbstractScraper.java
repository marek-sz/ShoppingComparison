package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractScraper implements Scraper {
    protected final ProductRepository productRepository;
    protected final ShopRepository shopRepository;
    protected final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    protected Map<Category, String> categoryMap = new HashMap<>();
    protected URL homeUrl;

    protected AbstractScraper(ProductRepository productRepository, ShopRepository shopRepository) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }

}