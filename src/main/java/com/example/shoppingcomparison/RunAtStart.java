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
    public RunAtStart(List<Scraper> scrapers, PasswordEncoder passwordEncoder) {
        this.scrapers = scrapers;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @PostConstruct
    public void runAtStart() {
        User user = new User();
        user.setUserName("admin");
        user.setPassword(passwordEncoder.encode("pass"));
        user.setActive(true);
        user.setRoles("ROLE_ADMIN");
        userRepository.save(user);

        scrapers.forEach(Scraper::scrapeEntireShop);
    }
}