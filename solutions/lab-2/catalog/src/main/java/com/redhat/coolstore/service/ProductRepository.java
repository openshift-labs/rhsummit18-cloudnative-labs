package com.redhat.coolstore.service;

import com.redhat.coolstore.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, String> {
}