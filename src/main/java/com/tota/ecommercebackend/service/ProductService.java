package com.tota.ecommercebackend.service;

import com.tota.ecommercebackend.model.Product;
import com.tota.ecommercebackend.model.dao.ProductDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductDAO productDAO;

    public ProductService(ProductDAO productDAO){
        this.productDAO=productDAO;
    }

    public List<Product> getProducts(){
        return productDAO.findAll();
    }
}
