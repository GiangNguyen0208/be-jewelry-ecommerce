//package com.example.Jewelry.controller;
//
//
//import com.example.Jewelry.dto.AuctionDTO;
//import com.example.Jewelry.dto.response.CommonApiResponse;
//import com.example.Jewelry.entity.Auction;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/auctions")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
//public class AuctionController {
//    private final AuctionService auctionService;
//
//    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<CommonApiResponse> createAuction(
//            @RequestPart("data") AuctionDTO dto,
//            @RequestPart(value = "images", required = false) List<MultipartFile> images
//    ) {
//        CommonApiResponse response = auctionService.createAuction(dto, images);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @GetMapping("/all-auction")
//    public ResponseEntity<Page<Auction>> getAllAuctions(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(auctionService.getAllAuctions(page, size));
//    }
//}
