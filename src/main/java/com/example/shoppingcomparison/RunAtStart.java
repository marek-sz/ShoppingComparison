package com.example.shoppingcomparison;

import com.example.shoppingcomparison.model.Product;
import com.example.shoppingcomparison.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class RunAtStart {
    private final ProductRepository productRepository;
    private final VitkacShoesScrapper vitkacShoesScrapper;
    private final MolieraShoesScrapper molieraShoesScrapper;

    @Autowired
    public RunAtStart(ProductRepository productRepository, VitkacShoesScrapper vitkacShoesScrapper, MolieraShoesScrapper molieraShoesScrapper) {
        this.productRepository = productRepository;
        this.vitkacShoesScrapper = vitkacShoesScrapper;
        this.molieraShoesScrapper = molieraShoesScrapper;
    }

    @PostConstruct
    public void runAtStart() {
        List<Product> vitkacProducts = vitkacShoesScrapper.scrapeProducts();
        List<Product> molieraProducts = molieraShoesScrapper.scrapeProducts();

        for (Product product : vitkacProducts) {
            productRepository.save(product);
        }

        for (Product molieraProduct : molieraProducts) {
            productRepository.save(molieraProduct);
        }

        List<Product> allProductsFromRepo = productRepository.findAll();
        printAll(allProductsFromRepo);
    }

    private void printAll(List<Product> products) {
        products.forEach(System.out::println);
    }

}

