package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.model.Product;
import com.example.shoppingcomparison.model.Shop;
import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class AbstractScraper implements Scraper {
    protected final ProductRepository productRepository;
    protected final ShopRepository shopRepository;
    protected Map<Category, String> categoryMap = new HashMap<>();
    protected URL homeUrl;
    protected TaskExecutor taskExecutor;
    protected final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    protected AbstractScraper(ProductRepository productRepository, ShopRepository shopRepository,  @Qualifier("taskExecutor") TaskExecutor taskExecutor) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.taskExecutor = taskExecutor;

    }

    public void scrapeEntireShop() {
        populateMap();
        categoryMap.keySet()
                .forEach(category -> taskExecutor.execute(() -> scrapeByCategory(category)));
    }

    @Async
    public void scrapeByCategory(Category category) {
        logger.log(Level.INFO, "Scraping " + category);
        scrapeAllPages(category);
    }

    public abstract void scrapeAllPages(Category category);

    public abstract void scrapeOnePage(Document page, Category category);

    public void saveSingleProduct(String scrapeModel, String scrapeBrand, BigDecimal price, String
            absHref, String imageUrl, Category category, Shop shop) {
        Product product = new Product.Builder()
                .model(scrapeModel)
                .brand(scrapeBrand)
                .price(price)
                .url(absHref)
                .imageUrl(imageUrl)
                .category(category)
                .shop(shop)
                .build();
        productRepository.save(product);
    }

    public boolean fieldIsNullOrEmpty(String scrapeModel, String scrapeBrand, String currentPrice, Element
            absHref, Element imageUrl) {
        return Stream.of(scrapeModel, scrapeBrand, currentPrice, absHref, imageUrl).anyMatch(Objects::isNull) ||
                Arrays.asList(scrapeModel, scrapeBrand, currentPrice, absHref, imageUrl).contains("");
    }
}