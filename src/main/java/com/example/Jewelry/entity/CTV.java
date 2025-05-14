package com.example.Jewelry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CTV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    private String phoneNo;
    private String location;
    private String experienceAndSkills;
    private String sampleWorkLink;
    private String reason;
    private LocalDateTime createdAt;
    private String status;
}

