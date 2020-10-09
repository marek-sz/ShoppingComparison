package com.example.shoppingcomparison.scrappers;

public interface Scraper {
    void populateMap();

    void scrapeEntireShop();

//    @Async
//    void scrapeByCategory(Category category);
//
//    void scrapeAllPages(Category category);
//
//    void scrapeOnePage(Document page, Category category);
//
//    void saveSingleProduct(String scrapeModel, String scrapeBrand, BigDecimal price, String
//            absHref, String imageUrl, Category category, Shop shop);
}
