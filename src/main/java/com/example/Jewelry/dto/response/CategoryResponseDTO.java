package com.example.Jewelry.dto.response;

import com.example.Jewelry.dto.CategoryDTO;
import com.example.Jewelry.entity.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryResponseDTO extends CommonApiResponse {
    private List<CategoryDTO> categories = new ArrayList<>();
}
