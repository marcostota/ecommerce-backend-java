package com.tota.ecommercebackend.model.dao;

import com.tota.ecommercebackend.model.Address;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AddressDao extends ListCrudRepository<Address, Long> {

    List<Address> findByUser_Id(Long id);

}
