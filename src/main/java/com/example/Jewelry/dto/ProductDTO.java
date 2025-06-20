package com.example.Jewelry.dto;

import com.example.Jewelry.dto.response.ImageDTO;
import com.example.Jewelry.entity.AuctionProduct;
import com.example.Jewelry.entity.Product;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private int id;
    private String name;
    private String description;
    private Double price;
    private String brand;
    private String size;
    private String productMaterial;
    private String occasion;
    private Double prevPrice;
    private Boolean productIsFavorite;
    private Boolean productIsCart;
    private String productIsBadge;
    private boolean deleted;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private LocalDateTime deletedAt;
    private List<ImageDTO> imageURLs;
    private List<MultipartFile> images;

    // Auction Product
    private AuctionProductDTO auctionProductDTO;

    // Category
    private int categoryId;
    private String categoryName;

    // Rating
    private double averageRating;
    private int totalRating;

    // CTV and Admin add
    private int ctvOrAdminId;

    public static ProductDTO fromEntity(Product product) {
        if (product == null) return null;

        List<ImageDTO> imageDTOs = null;
        if (product.getImages() != null) {
            imageDTOs = product.getImages().stream()
                    .map(image -> new ImageDTO(image.getId(), image.getUrl()))
                    .toList();
        }

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .prevPrice(product.getPrevPrice())
                .productIsBadge(product.getProductIsBadge())
                .imageURLs(imageDTOs)
                .build();
    }

    public static Product toEntity(ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product, "id", "ctvOrAdminId", "categoryId");
        return product;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}
