package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.dao.ProductDAO;
import com.example.Jewelry.entity.Category;
import com.example.Jewelry.entity.Product;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDAO productDAO;

    @Override
    public Product add(Product course) {
        // TODO Auto-generated method stub
        return productDAO.save(course);
    }

    @Override
    public Product update(Product course) {
        // TODO Auto-generated method stub
        return productDAO.save(course);
    }

    @Override
    public Product getById(int id) {

        Optional<Product> optional = this.productDAO.findById(id);

        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }

    }

    @Override
    public List<Product> getAll() {
        // TODO Auto-generated method stub
        return productDAO.findAll();
    }

    @Override
    public List<Product> getByStatus(String status) {
        // TODO Auto-generated method stub
        return productDAO.findByStatusOrderByIdDesc(status);
    }

    @Override
    public List<Product> getByCategoryAndStatus(Category category, String status) {
        // TODO Auto-generated method stub
        return productDAO.findByCategoryAndStatusOrderByIdDesc(category, status);
    }

    @Override
    public List<Product> getByNameAndStatus(String name, String status) {
        // TODO Auto-generated method stub
        return productDAO.findByStatusAndNameContainingIgnoreCaseOrderByIdDesc(status, name);
    }

    @Override
    public List<Product> updateAll(List<Product> courses) {
        // TODO Auto-generated method stub
        return productDAO.saveAll(courses);
    }

}
