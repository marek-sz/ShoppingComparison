package com.example.shoppingcomparison;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.scrappers.Scraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
public class RunAtStart {
    private List<Scraper> scrapers;

    @Autowired
    public RunAtStart(List<Scraper> scrapers) {
        this.scrapers = scrapers;
    }

//    int cpuCores = Runtime.getRuntime().availableProcessors();
//    ExecutorService service = Executors.newFixedThreadPool(cpuCores);

    @PostConstruct
    public void runAtStart() {
        for (Scraper scraper : scrapers) {
            for (Category category : Category.values()) {
                try {
                    scraper.scrapeProducts(category);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

