package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.model.Product;
import com.example.shoppingcomparison.model.Shop;
import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

public class AbstractScraperImpl extends AbstractScraper {
    Shop shop = shopRepository.save(new Shop("Moliera"));
    private TaskExecutor taskExecutor;

    protected AbstractScraperImpl(ProductRepository productRepository, ShopRepository shopRepository) throws MalformedURLException {
        super(productRepository, shopRepository);
        this.homeUrl = new URL("https://www.moliera2.com");
    }

    public void scrapeEntireShop() {
        taskExecutor.execute(() -> categoryMap.keySet().forEach(this::scrapeByCategory));
    }

    @Async
    public void scrapeByCategory(Category category) {
        String categoryBaseUrl = homeUrl + categoryMap.get(category);
        try {
            Document page = Jsoup.connect(categoryBaseUrl).get();
            page.select("div#product-list div").forEach(element -> {
                String scrapeModel, scrapeBrand, scrapePrice, scrapeSellPrice, currentPrice, imageSrc, absHref;
                scrapeModel = element.select("span.ProductItem24__name").text();
                scrapeBrand = element.select("span.ProductItem24__designer").text();
                scrapePrice = element.select("span.PriceWithSchema9__value").text();
                scrapeSellPrice = element.select("div.PriceWithSchema9__value.PriceWithSchema9__value--sale").text();
                currentPrice = checkCurrentPrice(scrapePrice, scrapeSellPrice);
                imageSrc = element.select("img[src$=.jpg]").first().attr("src");
                absHref = element.select("a[href]").first().attr("abs:href");

                if (!fieldIsNullOrEmpty(scrapeModel, scrapeBrand, currentPrice, absHref, imageSrc)) {
                    saveSingleProduct(scrapeModel, scrapeBrand, formatPrice(currentPrice), absHref, imageSrc, category, shop);
                }
            });

        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to connect to " + categoryBaseUrl);
        }
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

    @Override
    public void scrapeProducts(Category category) throws IOException {

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

    public boolean fieldIsNullOrEmpty(String scrapeModel, String scrapeBrand, String currentPrice, String
            absHref, String imageUrl) {
        return Stream.of(scrapeModel, scrapeBrand, currentPrice, absHref, imageUrl).anyMatch(Objects::isNull) ||
                Arrays.asList(scrapeModel, scrapeBrand, currentPrice, absHref, imageUrl).contains("");
    }

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
