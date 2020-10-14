package com.example.shoppingcomparison;

import com.example.shoppingcomparison.model.User;
import com.example.shoppingcomparison.repository.UserRepository;
import com.example.shoppingcomparison.scrappers.Scraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class RunAtStart {
    private List<Scraper> scrapers;

    @Autowired
    public RunAtStart(List<Scraper> scrapers) {
        this.scrapers = scrapers;
    }

    @PostConstruct
    public void runAtStart() {
        scrapers.forEach(Scraper::scrapeEntireShop);
    }
}