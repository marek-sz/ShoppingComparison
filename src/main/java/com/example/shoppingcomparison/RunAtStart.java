package com.example.shoppingcomparison;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.scrappers.MolieraScraper;
import com.example.shoppingcomparison.scrappers.Scraper;
import com.example.shoppingcomparison.scrappers.VitkacScraper;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class RunAtStart {
    private List<Scraper> scrapers;

    @Autowired
    public RunAtStart(List<Scraper> scrapers) {
        this.scrapers = scrapers;
    }

    @PostConstruct
    public void runAtStart() throws IOException {

        for (Scraper scraper : scrapers) {
            scraper.scrapeProducts(Category.SHOES);
        }
        //https://dzone.com/articles/load-all-implementors
    }
}

