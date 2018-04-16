package com.redhat.coolstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redhat.coolstore.model.Product;

@Controller
@RequestMapping("/services")
public class ProductEndpoint {
    
    @Autowired
    private ProductRepository productRepository;
    
    @ResponseBody
    @GetMapping("/products")
    public ResponseEntity<List<Product>> readAll() {
        return new ResponseEntity<List<Product>>(productRepository.readAll(),HttpStatus.OK);
    }
    
    @ResponseBody
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> readOne(@PathVariable("id") String id) {
        return new ResponseEntity<Product>(productRepository.findById(id),HttpStatus.OK);
    }
}
