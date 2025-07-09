package com.example.Jewelry.dto;

import com.example.Jewelry.dto.response.ImageDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private int id;
    private int productId;
    private String nameProduct;
    private double price;
    private List<ImageDTO> imageDTOS;
    private int quantity;
    private int discount;
}
