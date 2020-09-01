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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class VitkacScraper implements Scraper {
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final String url = "https://www.vitkac.com/pl/sklep/kobiety/buty-1";
    private final Document document = Jsoup.connect(url).userAgent("Chrome").ignoreHttpErrors(true).get();
    // currency PLN

    public VitkacScraper(ProductRepository productRepository, ShopRepository shopRepository) throws IOException {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }

    public void scrapeProducts(Category category) throws IOException {
        Shop shop = new Shop("Vitkac");
        shopRepository.save(shop);

        Elements pagination = document.select("span#offsets_top > li > a");
        for (Element e : pagination) {
            String url = e.attr("abs:href");
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

                }
            }
        }
    }
}