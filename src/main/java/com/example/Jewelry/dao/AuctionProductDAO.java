package com.example.Jewelry.dao;

import com.example.Jewelry.entity.AuctionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionProductDAO extends JpaRepository<AuctionProduct, Integer> {
}
