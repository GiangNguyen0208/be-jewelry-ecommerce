package com.example.Jewelry.service;

import com.example.Jewelry.dto.response.AuctionDetailDTO;
import com.example.Jewelry.entity.AuctionProduct;
import com.example.Jewelry.entity.Product;
import com.example.Jewelry.dto.request.PromoteProductRequestDTO;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

public interface AuctionProductService {
    AuctionProduct add(AuctionProduct auctionProduct);

    AuctionProduct update(AuctionProduct auctionProduct);

    List<AuctionProduct> updateAll(List<AuctionProduct> auctionProducts);

    AuctionProduct getById(int id);

    List<AuctionProduct> getAll();

    void deleteProduct(int auctionID);

    List<Product> fetchAllMyProductAuction(String value, int userID);

    AuctionDetailDTO getAuctionDetailsForAdmin(int auctionProductId);

    Page<Product> getAllAuctionProductsForAdmin(int page, int size, String status);

    Product promoteAuctionToStoreProduct(int productId, PromoteProductRequestDTO request);
}
