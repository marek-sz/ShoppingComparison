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
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    private Shop shop;

    public Product() {
    }

    public Product(final Builder builder) {
        this.brand = builder.brand;
        this.model = builder.model;
        this.price = builder.price;
        this.url = builder.url;
        this.imageUrl = builder.imageUrl;
        this.category = builder.category;
        this.shop = builder.shop;
    }

    public static class Builder {
        private String brand;
        private String model;
        private BigDecimal price;
        private String url;
        private String imageUrl;
        private Category category;
        private Shop shop;

        public Builder brand(final String brand) {
            this.brand = brand;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder shop(Shop shop) {
            this.shop = shop;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category type) {
        this.category = type;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }


}
