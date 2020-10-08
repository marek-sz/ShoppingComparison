package com.example.shoppingcomparison;

import com.example.shoppingcomparison.scrappers.Scraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    public void runAtStart() {
        scrapers.get(0).scrapeEntireShop();

//        for (Scraper scraper : scrapers) {
//            taskExecutor.execute(scraper::scrapeEntireShop);
//                for (Category category : Category.values()) {
//                    try {
//                        scraper.scrapeProducts(category);
//                    } catch (IOException e) {
//                        logger.log(Level.WARNING, "Category " + category + " is not defined for " + scraper.getClass().getSimpleName());
//                    }
//                }


        ;
    }
}