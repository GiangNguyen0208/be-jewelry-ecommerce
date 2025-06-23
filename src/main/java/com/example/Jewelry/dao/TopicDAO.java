package com.example.Jewelry.dao;

import com.example.Jewelry.entity.Review;
import com.example.Jewelry.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicDAO extends JpaRepository<Topic, Integer> {
    @Query("SELECT t FROM Topic t WHERE t.status = :status")
    List<Topic> findAllByOPEN(@Param("status") String status);
}
