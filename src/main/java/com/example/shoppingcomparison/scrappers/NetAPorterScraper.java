package com.example.shoppingcomparison.scrappers;

import com.example.shoppingcomparison.model.Category;
import com.example.shoppingcomparison.model.Product;
import com.example.shoppingcomparison.model.Shop;
import com.example.shoppingcomparison.repository.ProductRepository;
import com.example.shoppingcomparison.repository.ShopRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

    protected NetAPorterScraper(ProductRepository productRepository, ShopRepository shopRepository) throws MalformedURLException {
        super(productRepository, shopRepository);
        this.homeUrl = new URL("https://www.net-a-porter.com");
    }
    //currency E

    @Async
    @Override
    public void scrapeProducts(Category category) throws IOException {
//        String url = String.format("https://www.net-a-porter.com/%s", categoryMap.get(category));
        String url = formProperUrlFromCategoryMap(category);

        while (doesUrlExist(url)) {
            Document page = Jsoup.connect(url).get();
            for (Element row : page.select("div.ProductGrid50.ProductListWithLoadMore50__listingGrid a")) {
                String scrapeModel, scrapeBrand, scrapePrice, scrapeSellPrice, currentPrice, imageSrc, absHref;
                scrapeModel = row.select("span.ProductItem24__name").text();
                scrapeBrand = row.select("span.ProductItem24__designer").text();
                scrapePrice = row.select("span.PriceWithSchema9__value").text();
                scrapeSellPrice = row.select("div.PriceWithSchema9__value.PriceWithSchema9__value--sale").text();
                currentPrice = checkCurrentPrice(scrapePrice, scrapeSellPrice);
                imageSrc = row.select("img[src$=.jpg]").first().attr("src");
                absHref = row.select("a[href]").first().attr("abs:href");

                if (fieldIsEmpty(scrapeModel, scrapeBrand, currentPrice, imageSrc, absHref)) {
                    continue;
                } else {
                    BigDecimal price = formatPrice(currentPrice);
                    saveOneProduct(scrapeModel, scrapeBrand, price, absHref, imageSrc, category, shop);
                }
            }

            String nextUrl = returnNextUrlIfExist(url);
            if (nextUrl.equals(url)) {
                break;
            } else {
                url = nextUrl;
            }
        }
    }

    @Async
    void saveOneProduct(String scrapeModel, String scrapeBrand, BigDecimal price, String
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

    private boolean fieldIsEmpty(String scrapeModel, String scrapeBrand, String currentPrice, String imageSrc, String absHref) {
        return scrapeModel.isEmpty() || scrapeBrand.isEmpty() || currentPrice.isEmpty() || imageSrc.isEmpty() || absHref.isEmpty();
    }

    private boolean doesUrlExist(String url) {
        return url != null || !url.isEmpty();
    }

    private String returnNextUrlIfExist(String url) {
        String nextUrl = null;
        try {
            nextUrl = Jsoup.connect(url).get().select("a.Pagination7__next").attr("abs:href");
        } catch (IOException e) {
            logger.log(Level.INFO, "End of pagination on " + url);
        }
        return nextUrl;
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

    public String formProperUrlFromCategoryMap(Category category) {
        String url = null;
        try {
            url = new URL(homeUrl, categoryMap.get(category)).toString();
            logger.log(Level.INFO, "Scraping " + category + " from " + shop.getShopName());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Category " + category + " is not defined for " + this.getClass().getSimpleName());
        }
        return url;
    }

    public void populateMap() {
        categoryMap.put(Category.ACCESSORIES, "/en-pl/shop/accessories?cm_sp=topnav-_-accessories-_-topbar");
        categoryMap.put(Category.UNDERWEAR, "/en-pl/shop/lingerie?cm_sp=topnav-_-lingerie-_-alllingerie");
        categoryMap.put(Category.BLOUSE, "/en-pl/shop/clothing/tops/blouses?cm_sp=topnav-_-clothing-_-blouses&dScroll=0&image_view=product&npp=60");
        categoryMap.put(Category.SHOES, "en-pl/shop/shoes?cm_sp=topnav-_-shoes-_-allshoes");
        categoryMap.put(Category.JEANS, "/en-pl/shop/clothing/jeans?cm_sp=topnav-_-clothing-_-jeans");
        categoryMap.put(Category.JACKETS, "/en-pl/shop/clothing/jackets?cm_sp=topnav-_-clothing-_-jackets");
        categoryMap.put(Category.COATS, "/en-pl/shop/clothing/coats?cm_sp=topnav-_-clothing-_-coats");
        categoryMap.put(Category.SHORTS, "/en-pl/shop/clothing/shorts?cm_sp=topnav-_-clothing-_-shorts");
        categoryMap.put(Category.TROUSERS, "/en-pl/shop/clothing/pants?cm_sp=topnav-_-clothing-_-pants");
        categoryMap.put(Category.PURSES, "/en-pl/shop/bags?cm_sp=topnav-_-bags-_-allbags");
    }


}
