package com.example.shoppingcomparison.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Data
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String shopName;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        product.setShop(this);
        this.products.add(product);
    }

    public Shop() {
    }

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    @Override
    public String toString() {
        return shopName;
    }
}
