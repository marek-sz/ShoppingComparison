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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

@Service
public class MolieraScraper extends AbstractScraper {
    Shop shop = shopRepository.save(new Shop("Moliera"));

    protected MolieraScraper(ProductRepository productRepository, ShopRepository shopRepository,
                             @Qualifier("taskExecutor") TaskExecutor taskExecutor) throws MalformedURLException {
        super(productRepository, shopRepository, taskExecutor);
        this.homeUrl = new URL("https://www.moliera2.com");
    }

    public void scrapeAllPages(Category category) {
        String url = homeUrl + categoryMap.get(category);
        while (!url.isEmpty()) {
            try {
                Document page = Jsoup.connect(url).get();
                String nextUrl = page.select("ul.pagination > li.next > a").attr("abs:href");
                scrapeOnePage(page, category);
                url = nextUrl;

            } catch (IOException e) {
                logger.log(Level.WARNING, "Unable to establish connection with  " + url);
            }
        }
    }

    public void scrapeOnePage(Document page, Category category) {
        Elements elements = page.select("div#product-list div");
        for (Element element : elements) {
            String scrapeBrand, scrapeModel, scrapeRegularPrice, scrapeSellPrice, currentPrice;
            scrapeBrand = element.select("div.product-list-item-feature-designer").text();
            scrapeModel = element.select("div.product-list-item-name").text();
            scrapeRegularPrice = element.select("div.product-list-item-regular-price").text();
            scrapeSellPrice = element.select("div.product-list-item-sell-price").text();
            currentPrice = checkCurrentPrice(scrapeRegularPrice, scrapeSellPrice);
            Element image = element.select("img[src$=.jpg]").first();
            Element link = element.select("a").first();

            if (!fieldIsNullOrEmpty(scrapeModel, scrapeBrand, currentPrice, image, link)) {
                String imageUrl = image.attr("data-src");
                String absHref = link.attr("abs:href");
                BigDecimal price = formatPrice(currentPrice);
                saveSingleProduct(scrapeModel, scrapeBrand, price, absHref, imageUrl, category, shop);
            }
        }
    }

    private String checkCurrentPrice(String regularPrice, String sellPrice) {
        if (!regularPrice.isEmpty()) {
            return regularPrice;
        } else if (!sellPrice.isEmpty()) {
            return sellPrice;
        } else {
            return null;
        }
    }

    private BigDecimal formatPrice(String currentPrice) {
        String currentPriceFormatted = currentPrice.substring(0, currentPrice.indexOf('z')).replaceAll("\\s+", "");
        return new BigDecimal(currentPriceFormatted);
    }

    public void populateMap() {
        categoryMap.put(Category.ACCESSORIES, "/1/2/dodatki");
        categoryMap.put(Category.UNDERWEAR, "/1/1/30/bielizna");
        categoryMap.put(Category.BLOUSE, "/1/1/35/t-shirty---koszule---bluzki");
        categoryMap.put(Category.SHOES, "/1/4/buty");
        categoryMap.put(Category.JEANS, "/1/1/56/spodnie---jeansy---leginsy");
        categoryMap.put(Category.DUNGAREE, "/1/1/126/kombinezony");
        categoryMap.put(Category.JACKETS, "/1/1/33/kurtki---plaszcze---parki");
        categoryMap.put(Category.DRESS_JACKETS, "/1/1/37/marynarki---kamizelki");
        categoryMap.put(Category.SHORTS, "/1/1/38/spodenki");
        categoryMap.put(Category.PURSES, "/1/3/torby");
    }
}
