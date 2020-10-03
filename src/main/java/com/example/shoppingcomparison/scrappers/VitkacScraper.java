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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class VitkacScraper extends AbstractScraper {
    private URL homeUrl = new URL("https://www.vitkac.com");
    private Map<Category, String> categoryMap = new HashMap<>();
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Autowired
    public VitkacScraper(ProductRepository productRepository, ShopRepository shopRepository) throws MalformedURLException {
        super(productRepository, shopRepository);
    }

    @Async
    public void scrapeProducts(Category category) throws IOException {
        Shop shop = new Shop("Vitkac");
        shopRepository.save(shop);
        populateMap();
        logger.log(Level.INFO, "Scraping " + category + " from " + shop.getShopName());

        String url = new URL(homeUrl, categoryMap.get(category)).toString();

        while (doesUrlExist(url)) {
            Document page = Jsoup.connect(url).get();
            for (Element row : page.select("article#productList div")) {
                String scrapeBrand = row.select("h4").text();
                String scrapeModel = row.select("p").text();
                String scrapePrice = row.select("label").text();
                Element image = row.select("img.lazy.first").first();
                Element link = row.select("a.box-click").first();

                if (scrapeBrand.equals("") || scrapeModel.equals("") || scrapePrice.equals("") || link == null || image == null) {
                    continue;
                } else {
                    String absHref = link.attr("abs:href");
                    String imageUrl = image.attr("data-src");
                    BigDecimal price = formatPrice(scrapePrice);

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

            String nextUrl = returnNextUrlIfExist(page);
            if (nextUrl == null || nextUrl.equals(url)) {
                break;
            } else {
                url = nextUrl;
            }
        }
    }

    private boolean doesUrlExist(String url) throws IOException {
        return !url.isEmpty() && getHttpResponseStatus(url);
    }

    private String returnNextUrlIfExist(Document page) throws NullPointerException {
        String nextUrl = null;
        try {
            nextUrl = page.select("span#offsets_top.dropdown.na-stronie > a.small").last().attr("abs:href");
        } catch (NullPointerException e) {
            logger.log(Level.INFO, "End of pagination on " + page.baseUri());
        }
        return nextUrl;
    }

    private boolean getHttpResponseStatus(String url) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
        connection.setRequestMethod("POST");
        connection.connect();
        String responseMessage = connection.getResponseMessage();
        return responseMessage.equals("OK");
    }

    private BigDecimal formatPrice(String scrapePrice) {
        String currentPriceFormatted = scrapePrice.substring(0, scrapePrice.indexOf('z')).replaceAll("\\s+", "").replaceAll(",", ".");
        return new BigDecimal(currentPriceFormatted);
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
}