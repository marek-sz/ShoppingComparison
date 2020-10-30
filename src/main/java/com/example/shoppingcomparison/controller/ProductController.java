package com.example.shoppingcomparison.controller;

import com.example.shoppingcomparison.model.Product;
import com.example.shoppingcomparison.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String populateLandingPageWithRandomProducts(Model model) {
        List<Product> products = productService.populate();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/allProducts")
    public String getAllProducts(
            @RequestParam("category") String category, Model model) {
        List<Product> filteredProducts = productService.getAllProducts(category);
        model.addAttribute("products", filteredProducts);
        return "index";
    }

    @GetMapping("/products")
    public String findByName(
            @RequestParam("productName") String productName, Model model) {
        List<Product> filteredProducts = productService.findByName(productName);
        model.addAttribute("products", filteredProducts);
        return "index";
    }

    @GetMapping("/allProducts/byPrice")
    public String sortByPrice(Model model) {
        List<Product> products = productService.sortByPrice();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/allProducts/byName")
    public String sortByName(Model model) {
        List<Product> products = productService.sortByName();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/allProducts/byShop")
    public String sortByShop(Model model) {
        List<Product> products = productService.sortByShop();
        model.addAttribute("products", products);
        return "index";
    }
}
