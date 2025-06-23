package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.dao.AuctionProductDAO;
import com.example.Jewelry.dao.ProductDAO;
import com.example.Jewelry.dto.CtvInfoDTO;
import com.example.Jewelry.dto.CustomerInfoDTO;
import com.example.Jewelry.dto.ProductDTO;
import com.example.Jewelry.dto.request.PromoteProductRequestDTO;
import com.example.Jewelry.dto.response.AuctionDetailDTO;
import com.example.Jewelry.entity.*;
import com.example.Jewelry.exception.ResourceNotFoundException;
import com.example.Jewelry.service.AuctionProductService;
import com.example.Jewelry.service.CategoryService;
import com.example.Jewelry.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuctionProductServiceImpl implements AuctionProductService {
    @Autowired
    AuctionProductDAO auctionProductDAO;
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public AuctionProduct add(AuctionProduct auctionProduct) {
        return auctionProductDAO.save(auctionProduct);
    }

    @Override
    public AuctionProduct update(AuctionProduct auctionProduct) {
        return auctionProductDAO.save(auctionProduct);
    }

    @Override
    public List<AuctionProduct> updateAll(List<AuctionProduct> auctionProducts) {
        return auctionProductDAO.findAll();
    }

    @Override
    public AuctionProduct getById(int id) {
        Optional<AuctionProduct> optional = this.auctionProductDAO.findById(id);

        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    @Override
    public List<AuctionProduct> getAll() {
        return auctionProductDAO.findAll();
    }

    @Override
    public void deleteProduct(int auctionID) {
        this.auctionProductDAO.deleteById(auctionID);
    }

    @Override
    public List<Product> fetchAllMyProductAuction(String status, int userID) {
        return productDAO.findAllMyProductAuction(status, userID);
    }

    @Override
    public AuctionDetailDTO getAuctionDetailsForAdmin(int auctionProductId) {
        AuctionProduct auctionProduct = auctionProductDAO.findById(auctionProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiên đấu giá với ID: " + auctionProductId));

        User customer = auctionProduct.getAuthor();
        CTV ctv = auctionProduct.getCtv();

        ProductDTO productDTO = ProductDTO.fromEntity(auctionProduct.getProduct());

        CustomerInfoDTO customerInfoDTO = CustomerInfoDTO.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmailId())
                .phoneNo(customer.getPhoneNo())
                .build();

        CtvInfoDTO ctvInfoDTO = null;
        if (ctv != null) {
            ctvInfoDTO = CtvInfoDTO.builder()
                    .ctvId(ctv.getId())
                    .userId(ctv.getUser().getId())
                    .name(ctv.getUser().getFirstName() + " " + ctv.getUser().getLastName())
                    .email(ctv.getUser().getEmailId())
                    .phoneNo(ctv.getPhoneNo())
                    .location(ctv.getLocation())
                    .experienceAndSkills(ctv.getExperienceAndSkills())
                    .build();
        }

        return AuctionDetailDTO.builder()
                .productDetails(productDTO)
                .customerInfo(customerInfoDTO)
                .ctvInfo(ctvInfoDTO)
                .build();
    }

    @Override
    public Product promoteAuctionToStoreProduct(int productId, PromoteProductRequestDTO request) {
        Product productToPromote = productService.getById(productId);
        if (productToPromote.getAuctionProduct() == null) {
            throw new IllegalArgumentException("Sản phẩm này không phải là sản phẩm đấu giá.");
        }

        Category category = categoryService.getCategoryById(request.getCategoryId());
        if (category == null) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId());
        }

        productToPromote.setName(request.getName());
        productToPromote.setDescription(request.getDescription());
        productToPromote.setPrice(request.getPrice());
        productToPromote.setCategory(category);
        productToPromote.setStatus(Constant.ActiveStatus.ACTIVE.value());

        AuctionProduct auctionInfo = productToPromote.getAuctionProduct();
        auctionInfo.setStatus("PROMOTED");

        return productDAO.save(productToPromote);
    }

    @Override
    public Page<Product> getAllAuctionProductsForAdmin(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return auctionProductDAO.findAllAuctionProducts(pageable);
    }

}
