package com.example.Jewelry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String gender;

    private String firebaseUid;

    private String username;

    private String firstName;

    private String lastName;

    private String emailId;

    private String password;

    private String phoneNo;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private LocalDateTime deletedAt;

    private String oauth2_id;

    private String oauth2_provider;

    private String role;

    private boolean email_verified;

    private String avatar;

    private BigDecimal amount;

    private String status;
}

