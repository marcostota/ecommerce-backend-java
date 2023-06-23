package com.tota.ecommercebackend.model.dao;

import com.tota.ecommercebackend.model.LocalUser;
import com.tota.ecommercebackend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {
    List<WebOrder> findByUser(LocalUser user);
}
