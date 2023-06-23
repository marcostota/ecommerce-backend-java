package com.tota.ecommercebackend.model.dao;

import com.tota.ecommercebackend.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductDAO extends ListCrudRepository<Product, Long> {
}
