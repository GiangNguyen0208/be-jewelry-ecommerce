package com.example.Jewelry.dao;

import com.example.Jewelry.entity.CartItem;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemDAO extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    void deleteByUser(User user);

    List<CartItem> findByUserAndDeletedFalse(User user);

    Optional<CartItem> findByUserAndProductAndDeletedFalse(User user, Product product);
}

