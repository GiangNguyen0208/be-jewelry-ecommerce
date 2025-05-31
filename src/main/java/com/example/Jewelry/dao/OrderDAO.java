package com.example.Jewelry.dao;

import com.example.Jewelry.entity.Order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDAO extends JpaRepository<Order, Integer> {
    Page<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);

}
