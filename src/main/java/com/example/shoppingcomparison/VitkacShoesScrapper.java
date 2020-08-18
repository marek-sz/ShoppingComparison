package com.example.shoppingcomparison;

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
public class VitkacShoesScrapper {

    final String url = "https://www.vitkac.com/pl/sklep/kobiety/buty-1";
    final Document document = Jsoup.connect(url).userAgent("Chrome").ignoreHttpErrors(true).get();
    // currency PLN

    public VitkacShoesScrapper() throws IOException {
    }

    List<Product> scrapeProducts() {
        List<Product> products = new ArrayList<>();

        for (Element row : document.select("article#productList div")) {

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
                products.add(product);
            }
        }
        return products;
    }
}
