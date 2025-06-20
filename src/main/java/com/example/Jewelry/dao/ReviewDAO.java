package com.example.Jewelry.dao;

import com.example.Jewelry.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewDAO extends JpaRepository<Review, Integer> {

    List<Review> findByProductId(int productId);

    boolean existsByProductIdAndUserId(int productId, int userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId")
    double findAverageRatingByProductId(int productId);

    @Query("SELECT r FROM Review r WHERE r.productId = :productId AND r.rating = :rating ORDER BY r.createdAt DESC")
    List<Review> findByProductIdAndRating(int productId, int rating);

    List<Review> findByProductIdOrderByCreatedAtDesc(int productId);

    List<Review> findByProductIdOrderByHelpfulCountDesc(int productId);
}