package com.example.Jewelry.dao;

import com.example.Jewelry.entity.Category;
import com.example.Jewelry.entity.Product;
import com.example.Jewelry.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product, Integer> {

    List<Product> findByCategoryAndStatusOrderByIdDesc(Category category, String status);

    List<Product> findByStatusAndNameContainingIgnoreCaseOrderByIdDesc(String status, String name);

    List<Product> findByStatusOrderByIdDesc(String status);

    List<Product> findByDeletedFalse();
    List<Product> findByDeletedTrue();

}
