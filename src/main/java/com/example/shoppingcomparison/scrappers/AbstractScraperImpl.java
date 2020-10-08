package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.model.Product;
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
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

@Service
public class AbstractScraperImpl extends AbstractScraper {
    Shop shop = shopRepository.save(new Shop("Moliera"));
    private TaskExecutor taskExecutor;

    protected AbstractScraperImpl(ProductRepository productRepository, ShopRepository shopRepository, @Qualifier("taskExecutor") TaskExecutor taskExecutor) throws MalformedURLException {
        super(productRepository, shopRepository);
        this.homeUrl = new URL("https://www.moliera2.com");
        this.taskExecutor = taskExecutor;
    }

    public void scrapeEntireShop() {
        populateMap();
        categoryMap.keySet()
                .forEach(category -> taskExecutor.execute(() -> scrapeByCategory(category)));
    }

    @Async
    public void scrapeByCategory(Category category) {
        String categoryBaseUrl = homeUrl + categoryMap.get(category);
        logger.log(Level.INFO, "Scraping " + category + " from " + shop.getShopName());
        try {
            Document page = Jsoup.connect(categoryBaseUrl).get();
            scrapeOnePage(page, category);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to establish connection with " + categoryBaseUrl);
        }
    }

    private void scrapeOnePage(Document page, Category category) {
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
        scrapeNextPage(page);
    }

    private void scrapeNextPage(Document page) {
        String nextUrl = page.select("ul.pagination > li.next > a").attr("abs:href");

    }

    private void saveSingleProduct(String scrapeModel, String scrapeBrand, BigDecimal price, String
            absHref, String imageUrl, Category category, Shop shop) {
        Product product = new Product.Builder()
                .model(scrapeModel)
                .brand(scrapeBrand)
                .price(price)
                .url(absHref)
                .imageUrl(imageUrl)
                .category(category)
                .shop(shop)
                .build();
        productRepository.save(product);
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

    public boolean fieldIsNullOrEmpty(String scrapeModel, String scrapeBrand, String currentPrice, Element
            absHref, Element imageUrl) {
        return Stream.of(scrapeModel, scrapeBrand, currentPrice, absHref, imageUrl).anyMatch(Objects::isNull) ||
                Arrays.asList(scrapeModel, scrapeBrand, currentPrice, absHref, imageUrl).contains("");
    }

    @Async
    @Override
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
