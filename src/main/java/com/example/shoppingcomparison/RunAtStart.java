package com.example.shoppingcomparison;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.scrappers.Scraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
public class RunAtStart {
    private List<Scraper> scrapers;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Autowired
    public RunAtStart(List<Scraper> scrapers) {
        this.scrapers = scrapers;
    }

    @PostConstruct
    public void runAtStart() throws IOException {
        for (Category category : Category.values()) {
            scrapers.get(1).scrapeProducts(category);
        }

//        for (Scraper scraper : scrapers) {
//            for (Category category : Category.values()) {
//                try {
//                    scraper.scrapeProducts(category);
//                } catch (IOException e) {
//                    logger.log(Level.WARNING, "Category " + category + " is not defined for " + scraper.getClass().getSimpleName());
//                }
//            }
//        }
//    }
    }
}