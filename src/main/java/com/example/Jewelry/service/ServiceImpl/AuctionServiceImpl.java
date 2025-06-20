package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.dto.AuctionDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuctionServiceImpl {
    CommonApiResponse createAuction(AuctionDTO dto, List<MultipartFile> images);
    Page<Auction> getAllAuctions(int page, int size);
}
