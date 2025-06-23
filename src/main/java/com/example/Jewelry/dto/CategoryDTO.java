package com.example.Jewelry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private int id;

    private String name;

    private String status;

    private String thumbnail;

    private boolean deleted;
}
