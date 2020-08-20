package com.example.shoppingcomparison.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String brand;
    private String model;
    private BigDecimal price;
    private String url;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    private Shop shop;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category type) {
        this.category = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
