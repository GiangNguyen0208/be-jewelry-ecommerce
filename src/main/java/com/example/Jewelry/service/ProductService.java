package com.example.Jewelry.service;

import com.example.Jewelry.entity.Category;
import com.example.Jewelry.entity.Product;
import com.example.Jewelry.entity.User;

import java.util.List;

public interface ProductService {
    Product add(Product course);

    Product update(Product course);

    List<Product> updateAll(List<Product> courses);

    Product getById(int id);

    List<Product> getAll();

    List<Product> getByStatus(String status);

    List<Product> getByCategoryAndStatus(Category category, String status);

    List<Product> getByNameAndStatus(String name, String status);


}
