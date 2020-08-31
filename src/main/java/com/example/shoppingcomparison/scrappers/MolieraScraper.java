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

    // currency PLN

    Map<Category, String> categoryMap = new HashMap<>();

    public MolieraScraper(ProductRepository productRepository, ShopRepository shopRepository) throws IOException {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }

    public void scrapeProducts(Category category) throws IOException {

        Shop shop = new Shop("Moliera");
        shopRepository.save(shop);

        categoryMap.put(Category.ACCESSORIES, "/1/2/dodatki");
        categoryMap.put(Category.UNDERWEAR, "/1/1/30/bielizna");
        categoryMap.put(Category.BLOUSE, "/1/1/35/t-shirty---koszule---bluzki");
        categoryMap.put(Category.SHOES, "/1/4/buty");
        categoryMap.put(Category.JEANS, "/1/1/56/spodnie---jeansy---leginsy");
        categoryMap.put(Category.DUNGAREE, "/1/1/126/kombinezony");
//        categoryMap.put(Category.SHIRTS, "/1/4/buty");
        categoryMap.put(Category.JACKETS, "/1/1/33/kurtki---plaszcze---parki");
//        categoryMap.put(Category.COATS, "/1/1/33/kurtki---plaszcze---parki");
//        categoryMap.put(Category.LEGGINGS, "/1/1/56/spodnie---jeansy---leginsy");
        categoryMap.put(Category.DRESS_JACKETS, "/1/1/37/marynarki---kamizelki");
        categoryMap.put(Category.SHORTS, "/1/1/38/spodenki");
//        categoryMap.put(Category.TROUSERS, "/1/1/56/spodnie---jeansy---leginsy");
        categoryMap.put(Category.PURSES, "/1/3/torby");

        String url = new URL(homeUrl, categoryMap.get(category)).toString();

        while (url != "") {
            Document page = Jsoup.connect(url).get();
            String nextUrl = page.select("ul.pagination > li.next > a").attr("abs:href");

            for (Element row : page.select("div#product-list div")) {
                String scrapeBrand = row.select("div.product-list-item-feature-designer").text();
                String scrapeModel = row.select("div.product-list-item-name").text();
                String scrapePrice = row.select("div.product-list-item-regular-price").text();
                String scrapeSellPrice = row.select("div.product-list-item-sell-price").text();
                Element link = row.select("a").first();

                if (scrapeBrand.equals("") || scrapeModel.equals("") || scrapePrice.equals("") || link == null) {
                    continue;
                } else {
                    String absHref = link.attr("abs:href");

                    String scrapePriceFormatted = scrapePrice.substring(0, scrapePrice.indexOf('z')).replaceAll("\\s+", "");
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
            //pagination
            url = nextUrl;
        }
    }

    boolean isDiscounted(String price) {
        return false;
    }
}