package com.example.Jewelry.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private int id;
    private int userId;
    private String username;
    private int productId;
    private int rating;
    private String comment;
    private int helpfulCount;
    private LocalDateTime createdAt;
}
