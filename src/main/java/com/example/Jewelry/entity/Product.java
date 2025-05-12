package com.example.Jewelry.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "category_id")
    private Category category;;

    @Column(name = "brand")
    private String brand;

    @ElementCollection
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images;

    @Column(name = "size")
    private String size;

    @Column(name = "material")
    private String productMaterial;

    @Column(name = "occasion")
    private String occasion;

    @Column(name = "prev_price")
    private Double prevPrice;

    @Column(name = "product_is_favorite")
    private Boolean productIsFavorite;

    @Column(name = "product_is_cart")
    private Boolean productIsCart;

    @Column(name = "product_is_badge")
    private String productIsBadge;

    private boolean deleted;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private LocalDateTime deletedAt;


}
