package com.example.Jewelry.dao;

import com.example.Jewelry.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Integer> {
    User findByEmailId(String email);

    User findByEmailIdAndStatus(String email, String status);

    User findByRoleAndStatusIn(String role, List<String> status);

    List<User> findByRole(String role);

    User findByEmailIdAndRoleAndStatus(String emailId, String role, String status);

    List<User> findByRoleAndStatus(String role, String status);

    @Transactional
    @Modifying
    @Query("UPDATE User a SET a.status = 'Active' WHERE a.emailId = ?1")
    int activeUser(String email);

    @Query("SELECT u FROM User u WHERE u.mentorDetail IS NOT NULL AND u.role = 'Mentor'")
    List<User> findAllMentors();


}

