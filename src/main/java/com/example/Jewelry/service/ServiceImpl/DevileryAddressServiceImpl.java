package com.example.Jewelry.service.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Jewelry.dao.DeliveryAddressDAO;
import com.example.Jewelry.entity.DeliveryAddress;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.service.DeliveryAddressService;
import com.example.Jewelry.service.UserService;

/***
 * Lớp hiện thực của service cho sổ địa chỉ
 */
@Service
public class DevileryAddressServiceImpl implements DeliveryAddressService {

    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryAddressDAO deliveryAddressDAO;

    @Override
    public List<DeliveryAddress> getByUserID(int userID) {
        User user = userService.getUserById(userID);
        if (user == null) {
            return null;
        }
        return deliveryAddressDAO.findByOwner(user);
    }

    @Override
    public DeliveryAddress addAddress(DeliveryAddress address) {
        return deliveryAddressDAO.save(address);
    }

    @Override
    public DeliveryAddress updateDeliveryAddress(DeliveryAddress address) {
        return deliveryAddressDAO.save(address);
    }

    @Override
    public boolean removeDeliveryAddress(DeliveryAddress address) {
        deliveryAddressDAO.delete(address);
        return deliveryAddressDAO.findById(address.getId()).isEmpty();
    }



}
