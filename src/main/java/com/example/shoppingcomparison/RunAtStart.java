package com.example.shoppingcomparison;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.scrappers.MolieraScraper;
import com.example.shoppingcomparison.scrappers.VitkacScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RunAtStart {
    private final VitkacScraper vitkacScraper;
    private final MolieraScraper molieraScrapper;

    @Autowired
    public RunAtStart(VitkacScraper vitkacScraper, MolieraScraper molieraScrapper) {
        this.vitkacScraper = vitkacScraper;
        this.molieraScrapper = molieraScrapper;
    }

    @PostConstruct
    public void runAtStart() throws IOException {

            molieraScrapper.scrapeProducts(Category.SHOES);
//        for (Category category : Category.values()) {
//
//            molieraScrapper.scrapeProducts(category);
//        vitkacScraper.scrapeProducts(category);
//        }

    }
}

