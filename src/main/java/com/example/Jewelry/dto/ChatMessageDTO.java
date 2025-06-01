package com.example.Jewelry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ChatMessageDTO {
    private int id;
    private String content;
    private Integer senderId; // Map to User.id
    private Integer recipientId; // Map to User.id
    private Integer productId; // Map to Product.id
    private String sentAt;
}
