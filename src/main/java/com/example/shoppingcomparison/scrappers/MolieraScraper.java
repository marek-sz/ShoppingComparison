package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.model.Product;
import com.example.shoppingcomparison.model.Shop;
import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class MolieraScraper implements Scraper {
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private URL homeUrl = new URL("https://www.moliera2.com");
    private Map<Category, String> categoryMap = new HashMap<>();
    // currency PLN

    public MolieraScraper(ProductRepository productRepository, ShopRepository shopRepository) throws IOException {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }

    public void scrapeProducts(Category category) throws IOException {
        Shop shop = new Shop("Moliera");
        shopRepository.save(shop);

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
                Element link = row.select("a").first();

                if (scrapeBrand.equals("") || scrapeModel.equals("") || currentPrice == null || link == null) {
                    continue;
                } else {
                    String absHref = link.attr("abs:href");
                    BigDecimal price = formatPrice(currentPrice);

                    Product product = new Product();
                    product.setModel(scrapeModel);
                    product.setBrand(scrapeBrand);
                    product.setPrice(price);
                    product.setUrl(absHref);
                    product.setCategory(category);
                    shop.addProduct(product);
                    productRepository.save(product);
                    System.out.println(product);
                }
            }
            url = nextUrl;
        }
    }

    private void populateMap() {
        categoryMap.put(Category.ACCESSORIES, "/1/2/dodatki");
        categoryMap.put(Category.UNDERWEAR, "/1/1/30/bielizna");
        categoryMap.put(Category.BLOUSE, "/1/1/35/t-shirty---koszule---bluzki");
        categoryMap.put(Category.SHOES, "/1/4/buty");
        categoryMap.put(Category.JEANS, "/1/1/56/spodnie---jeansy---leginsy");
        categoryMap.put(Category.JEANS, "/1/1/56/spodnie---jeansy---leginsy");
        categoryMap.put(Category.DUNGAREE, "/1/1/126/kombinezony");
        categoryMap.put(Category.JACKETS, "/1/1/33/kurtki---plaszcze---parki");
        categoryMap.put(Category.DRESS_JACKETS, "/1/1/37/marynarki---kamizelki");
        categoryMap.put(Category.SHORTS, "/1/1/38/spodenki");
        categoryMap.put(Category.PURSES, "/1/3/torby");
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
}