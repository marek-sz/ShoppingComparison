package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
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
import java.util.logging.Level;

@Service
public class NetAPorterScraper extends AbstractScraper {
    Shop shop = shopRepository.save(new Shop("Net A Porter"));

    protected NetAPorterScraper(ProductRepository productRepository, ShopRepository shopRepository,
                                @Qualifier("taskExecutor") TaskExecutor taskExecutor) throws MalformedURLException {
        super(productRepository, shopRepository, taskExecutor);
        this.homeUrl = new URL("https://www.net-a-porter.com");
    }

    @Override
    public void scrapeAllPages(Category category) {
        String url = homeUrl + categoryMap.get(category);
        while (!url.isEmpty()) {
            try {
                Document page = Jsoup.connect(url).get();
                String nextUrl = page.select("a.Pagination7__next").attr("abs:href");
                scrapeOnePage(page, category);
                if (!url.equals(nextUrl)) {
                    url = nextUrl;
                } else {
                    break;
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Unable to establish connection with  " + url);
            }
        }
    }

    @Async
    @Override
    public void scrapeOnePage(Document page, Category category) {
        Elements elements = page.select("div.ProductGrid50.ProductListWithLoadMore50__listingGrid a");
        for (Element row : elements) {
            String scrapeModel, scrapeBrand, scrapePrice, scrapeSellPrice, currentPrice;
            scrapeModel = row.select("span.ProductItem24__name").text();
            scrapeBrand = row.select("span.ProductItem24__designer").text();
            scrapePrice = row.select("span.PriceWithSchema9__value").text();
            scrapeSellPrice = row.select("div.PriceWithSchema9__value.PriceWithSchema9__value--sale").text();
            currentPrice = checkCurrentPrice(scrapePrice, scrapeSellPrice);
            Element image = row.select("img[src$=.jpg]").first();
            Element link = row.select("a[href]").first();

            if (!fieldIsNullOrEmpty(scrapeModel, scrapeBrand, currentPrice, image, link)) {
                String imageUrl = image.attr("src");
                String absHref = link.attr("abs:href");
                BigDecimal price = formatPrice(currentPrice);
                saveSingleProduct(scrapeModel, scrapeBrand, price, absHref, imageUrl, category, shop);
            }
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
        String currentPriceFormatted = currentPrice.replace("€", "").replaceAll(",", ".");
        return new BigDecimal(currentPriceFormatted);
    }

    public void populateMap() {
        categoryMap.put(Category.ACCESSORIES, "/en-pl/shop/accessories?cm_sp=topnav-_-accessories-_-topbar");
        categoryMap.put(Category.UNDERWEAR, "/en-pl/shop/lingerie?cm_sp=topnav-_-lingerie-_-alllingerie");
        categoryMap.put(Category.BLOUSE, "/en-pl/shop/clothing/tops/blouses?cm_sp=topnav-_-clothing-_-blouses&dScroll=0&image_view=product&npp=60");
        categoryMap.put(Category.SHOES, "/en-pl/shop/shoes?cm_sp=topnav-_-shoes-_-allshoes");
        categoryMap.put(Category.JEANS, "/en-pl/shop/clothing/jeans?cm_sp=topnav-_-clothing-_-jeans");
        categoryMap.put(Category.JACKETS, "/en-pl/shop/clothing/jackets?cm_sp=topnav-_-clothing-_-jackets");
        categoryMap.put(Category.COATS, "/en-pl/shop/clothing/coats?cm_sp=topnav-_-clothing-_-coats");
        categoryMap.put(Category.SHORTS, "/en-pl/shop/clothing/shorts?cm_sp=topnav-_-clothing-_-shorts");
        categoryMap.put(Category.TROUSERS, "/en-pl/shop/clothing/pants?cm_sp=topnav-_-clothing-_-pants");
        categoryMap.put(Category.PURSES, "/en-pl/shop/bags?cm_sp=topnav-_-bags-_-allbags");
    }
}
