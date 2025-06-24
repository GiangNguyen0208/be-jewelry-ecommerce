package com.example.Jewelry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String status;

    private String thumbnail;

    private boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Topic> topics;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

}
