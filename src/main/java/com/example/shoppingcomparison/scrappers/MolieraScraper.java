package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.model.Product;
import com.example.shoppingcomparison.model.Shop;
import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MolieraScraper extends AbstractScraper {
    private URL homeUrl = new URL("https://www.moliera2.com");
    private Map<Category, String> categoryMap = new HashMap<>();
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Autowired
    protected MolieraScraper(ProductRepository productRepository, ShopRepository shopRepository) throws MalformedURLException {
        super(productRepository, shopRepository);
    }

    @Override
    public void scrapeProducts(Category category) throws IOException {
        Shop shop = new Shop("Moliera");
        shopRepository.save(shop);
        logger.log(Level.INFO, "Scraping " + category + " from " + shop.getShopName());
        populateMap();

        String url = new URL(homeUrl, categoryMap.get(category)).toString();

        while (!url.isEmpty()) {
            Document page = Jsoup.connect(url).get();
            String nextUrl = page.select("ul.pagination > li.next > a").attr("abs:href");

            for (Element row : page.select("div#product-list div")) {
                String scrapeBrand, scrapeModel, scrapeRegularPrice, scrapeSellPrice, currentPrice;
                scrapeBrand = row.select("div.product-list-item-feature-designer").text();
                scrapeModel = row.select("div.product-list-item-name").text();
                scrapeRegularPrice = row.select("div.product-list-item-regular-price").text();
                scrapeSellPrice = row.select("div.product-list-item-sell-price").text();
                currentPrice = checkCurrentPrice(scrapeRegularPrice, scrapeSellPrice);
                Element image = row.select("img[src$=.jpg]").first();
                Element link = row.select("a").first();

                if (scrapeBrand.equals("") || scrapeModel.equals("") || currentPrice == null || link == null || image == null) {
                    continue;
                } else {
                    String absHref = link.attr("abs:href");
                    String imageUrl = image.attr("data-src");
                    BigDecimal price = formatPrice(currentPrice);

                    Product product = new Product.Builder()
                            .model(scrapeModel)
                            .brand(scrapeBrand)
                            .price(price)
                            .url(absHref)
                            .imageUrl(imageUrl)
                            .category(category)
                            .build();

                    shop.addProduct(product);
                    productRepository.save(product);
                }
            }
            url = nextUrl;
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

    private void populateMap() {
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