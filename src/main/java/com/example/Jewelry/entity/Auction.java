package com.example.Jewelry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auctions")
@Getter
@Setter
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String jewelryType;
    private String material;
    private String size;
    private String description;
    private String specialRequest;
    private String budget;
    private LocalDate deadline;
    private boolean deleted;

    @ElementCollection
    private List<String> images = new ArrayList<>();

}
