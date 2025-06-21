package com.example.Jewelry.service;

import com.example.Jewelry.entity.AuctionProduct;
import com.example.Jewelry.entity.Product;

import java.util.List;

public interface AuctionProductService {
    AuctionProduct add(AuctionProduct auctionProduct);

    AuctionProduct update(AuctionProduct auctionProduct);

    List<AuctionProduct> updateAll(List<AuctionProduct> auctionProducts);

    AuctionProduct getById(int id);

    List<AuctionProduct> getAll();

    void deleteProduct(int auctionID);
}
