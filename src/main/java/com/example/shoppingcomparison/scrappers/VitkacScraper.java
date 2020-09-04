package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.model.Product;
import com.example.shoppingcomparison.model.Shop;
import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class VitkacScraper implements Scraper {
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    private URL homeUrl = new URL("https://www.vitkac.com");
    private Map<Category, String> categoryMap = new HashMap<>();
    // currency PLN

    public VitkacScraper(ProductRepository productRepository, ShopRepository shopRepository) throws IOException {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }

    public void scrapeProducts(Category category) throws IOException {
        Shop shop = new Shop("Vitkac");
        shopRepository.save(shop);

        populateMap();

        String url = new URL(homeUrl, categoryMap.get(category)).toString();

        while (!url.isEmpty() && getHttpResponseStatusCode(url) == 200) {
            Document page = Jsoup.connect(url).get();
            for (Element row : page.select("article#productList div")) {
                String scrapeBrand = row.select("h4").text();
                String scrapeModel = row.select("p").text();
                String scrapePrice = row.select("label").text();
                Element link = row.select("a.box-click").first();

                if (scrapeBrand.equals("") || scrapeModel.equals("") || scrapePrice.equals("") || link == null) {
                    continue;
                } else {
                    String absHref = link.attr("abs:href");
                    String scrapePriceFormatted = scrapePrice.substring(0, scrapePrice.indexOf('z')).replaceAll("\\s+", "").replaceAll(",", ".");
                    BigDecimal price = new BigDecimal(scrapePriceFormatted);

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

            String nextUrl = returnNextUrlIfExist(page);
            if (nextUrl == null || nextUrl.equals(url)) {
                break;
            } else {
                url = nextUrl;
            }
        }
    }

    private String returnNextUrlIfExist(Document page) throws NullPointerException {
        String nextUrl = null;
        try {
            nextUrl = page.select("span#offsets_top.dropdown.na-stronie > a.small").last().attr("abs:href");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return nextUrl;
    }

    private void populateMap() {
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

    private int getHttpResponseStatusCode(String url) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
        connection.setRequestMethod("POST");
        connection.connect();
        return connection.getResponseCode();
    }
}