package com.example.Jewelry.dto;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.example.Jewelry.entity.OrderItem;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemDTO {
    
    private Integer productId;

    private String productName;

    private Integer quantity;

    private Double price;

    public static List<OrderItemDTO> convertItemList(List<OrderItem> items) {
        return items.stream().map((item) -> convertItem(item)).toList();
    }

    public static OrderItemDTO convertItem(OrderItem item) {

        OrderItemDTO result = new OrderItemDTO();
        result.setProductId(item.getProductId());
        result.setProductName(null);
        result.setPrice(item.getPrice());
        result.setQuantity(item.getQuantity());

        return result;

    }
}
