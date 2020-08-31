package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;

import java.io.IOException;

public interface Scraper {
    void scrapeProducts(Category category) throws IOException;
}
