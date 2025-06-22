package com.example.Jewelry.dao;

import com.example.Jewelry.entity.AuctionProduct;
import com.example.Jewelry.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionProductDAO extends JpaRepository<AuctionProduct, Integer> {

    @Query("SELECT a FROM AuctionProduct a WHERE a.status = :status and a.author.id = :userID")
    List<AuctionProduct> findAllMyProductAuction(@Param("status") String status, @Param("userID") int userID);
    @Query("SELECT p FROM Product p WHERE p.auctionProduct IS NOT NULL")
    Page<Product> findAllAuctionProducts(Pageable pageable);
}
