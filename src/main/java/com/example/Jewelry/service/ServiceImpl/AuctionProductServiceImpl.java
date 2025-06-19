package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.dao.AuctionProductDAO;
import com.example.Jewelry.entity.AuctionProduct;
import com.example.Jewelry.entity.Product;
import com.example.Jewelry.service.AuctionProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuctionProductServiceImpl implements AuctionProductService {
    @Autowired
    AuctionProductDAO auctionProductDAO;

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
}
