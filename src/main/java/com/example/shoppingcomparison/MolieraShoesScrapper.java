package com.example.shoppingcomparison;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MolieraShoesScrapper {

    final String url = "https://www.moliera2.com/1/4/buty";
    final Document document = Jsoup.connect(url).userAgent("Chrome").ignoreHttpErrors(true).get();
        // currency PLN

    public MolieraShoesScrapper() throws IOException {
    }

    List<Product> scrapeProducts() {
        List<Product> products = new ArrayList<>();

        for (Element row : document.select("div#product-list div")) {
            String scrapeBrand = row.select("div.product-list-item-feature-designer").text();
            String scrapeModel = row.select("div.product-list-item-name").text();
            String scrapePrice = row.select("div.product-list-item-regular-price").text();
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
                product.setCategory(Category.SHOES);
                products.add(product);
            }
        }
        return products;
    }
}
