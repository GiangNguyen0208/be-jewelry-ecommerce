package com.example.Jewelry.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

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

    @OneToMany(mappedBy = "productId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Review> reviews;

    private boolean deleted;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private LocalDateTime deletedAt;


}
