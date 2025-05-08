package com.example.Jewelry.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(name = "product_description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "product_price", nullable = false)
    private Double productPrice;

    @Column(name = "product_category")
    private String category;

    @Column(name = "product_brand")
    private String brand;

    @ElementCollection
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images;

    @Column(name = "size")
    private String size;

    @Column(name = "material")
    private String productMaterial;

    @Column(name = "product_occasion")
    private String productOccasion;

    @Column(name = "product_prev_price")
    private Double productPrevPrice;

    @Column(name = "product_is_favorite")
    private Boolean productIsFavorite;

    @Column(name = "product_is_cart")
    private Boolean productIsCart;

    @Column(name = "product_is_badge")
    private String productIsBadge;
}
