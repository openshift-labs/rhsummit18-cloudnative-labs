package com.redhat.coolstore.service;

import com.redhat.coolstore.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/services")
public class ProductEndpoint {

    @Autowired
    private ProductRepository productRepository;

    @ResponseBody
    @GetMapping("/products")
    public ResponseEntity<List<Product>> readAll() {
        Spliterator<Product> iterator = productRepository.findAll().spliterator();
        List<Product> products = StreamSupport.stream(iterator, false).collect(Collectors.toList());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> readOne(@PathVariable("id") String id) {
        Product product = productRepository.findOne(id);
        return new ResponseEntity<Product>(product,HttpStatus.OK);
    }
}