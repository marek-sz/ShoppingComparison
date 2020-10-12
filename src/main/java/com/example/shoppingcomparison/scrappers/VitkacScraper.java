package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.model.Shop;
import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

@Service
public class VitkacScraper extends AbstractScraper {
    Shop shop = shopRepository.save(new Shop("Vitkac"));

    public VitkacScraper(ProductRepository productRepository, ShopRepository shopRepository,
                         @Qualifier("taskExecutor") TaskExecutor taskExecutor) throws MalformedURLException {
        super(productRepository, shopRepository, taskExecutor);
        this.homeUrl = new URL("https://www.vitkac.com");
    }

    public void scrapeAllPages(Category category) {
        String url = homeUrl + categoryMap.get(category);
        String nextUrl;

        while (true) {
            try {
                Document page = Jsoup.connect(url).get();
                scrapeOnePage(page, category);
                nextUrl = searchForNextUrl(page);
                if (nextUrl == null || nextUrl.equals(url)) {
                    break;
                }
                url = nextUrl;
            } catch (IOException e) {
                logger.log(Level.WARNING, "Unable to establish connection with  " + url);
            }
        }
    }

    private String searchForNextUrl(Document page) {
        String nextUrl = null;
        try {
            nextUrl = page.select("span#offsets_top.dropdown.na-stronie > a.small").last().attr("abs:href");
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "End of pagination");
        }
        return nextUrl;
    }

    @Async
    @Override
    public void scrapeOnePage(Document page, Category category) {
        Elements elements = page.select("article#productList div");
        for (Element row : elements) {
            String scrapeBrand = row.select("h4").text();
            String scrapeModel = row.select("p").text();
            String currentPrice = row.select("label").text();
            Element image = row.select("img.lazy.first").first();
            Element link = row.select("a.box-click").first();

            if (!fieldIsNullOrEmpty(scrapeModel, scrapeBrand, currentPrice, image, link)) {
                String imageUrl = image.attr("data-src");
                String absHref = link.attr("abs:href");
                BigDecimal price = formatPrice(currentPrice);
                saveSingleProduct(scrapeModel, scrapeBrand, price, absHref, imageUrl, category, shop);
            }
        }
    }

    private BigDecimal formatPrice(String scrapePrice) {
        String currentPriceFormatted = scrapePrice.substring(0, scrapePrice.indexOf('z')).replaceAll("\\s+", "").replaceAll(",", ".");
        return new BigDecimal(currentPriceFormatted);
    }

    public void populateMap() {
        categoryMap.put(Category.ACCESSORIES, "/pl/sklep/kobiety/akcesoria-2");
        categoryMap.put(Category.UNDERWEAR, "/pl/sklep/kobiety/bielizna-skarpety-1");
        categoryMap.put(Category.SHOES, "/pl/sklep/kobiety/buty-1");
        categoryMap.put(Category.JEANS, "/pl/sklep/kobiety/jeansy-1");
        categoryMap.put(Category.DUNGAREE, "/pl/sklep/kobiety/kombinezony");
        categoryMap.put(Category.SHIRTS, "/pl/sklep/kobiety/spodnice");
        categoryMap.put(Category.JACKETS, "/pl/sklep/kobiety/kurtki-1");
        categoryMap.put(Category.COATS, "/pl/sklep/kobiety/plaszcze-1");
        categoryMap.put(Category.SHORTS, "/pl/sklep/kobiety/spodnie-1");
        categoryMap.put(Category.PURSES, "/pl/sklep/kobiety/torby-2");
    }
}