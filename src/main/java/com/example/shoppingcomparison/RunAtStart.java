package com.example.shoppingcomparison;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.scrappers.Scraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RunAtStart {
    private TaskExecutor taskExecutor;
    private List<Scraper> scrapers;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Autowired
    public RunAtStart(List<Scraper> scrapers, @Qualifier("taskExecutor") TaskExecutor taskExecutor) {
        this.scrapers = scrapers;
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    public void executeAsynchronously() {
        taskExecutor.execute(() -> {
            for (Scraper scraper : scrapers) {
                scraper.populateMap();
                for (Category category : Category.values()) {
                    try {
                        scraper.scrapeProducts(category);
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Category " + category + " is not defined for " + scraper.getClass().getSimpleName());
                    }
                }
            }
        });
    }
}