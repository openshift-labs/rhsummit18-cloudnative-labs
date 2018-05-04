package com.redhat.coolstore.service;

import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redhat.coolstore.client.InventoryClient;
import com.redhat.coolstore.model.Inventory;
import com.redhat.coolstore.model.Product;

@Controller
@RequestMapping("/services")
public class ProductEndpoint {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private InventoryClient inventoryClient;
    
    @ResponseBody
    @GetMapping("/products")
    public ResponseEntity<List<Product>> readAll() {
        Spliterator<Product> iterator = productRepository.findAll().spliterator();
        List<Product> products = StreamSupport.stream(iterator, false).collect(Collectors.toList());

        //Get all the inventory and convert it to a Map.
        Map<String, Integer> inventoryMap = inventoryClient.getInventoryStatusForAll()
                .stream()
                .collect(Collectors.toMap(
                  (Inventory i) -> i.getItemId(), (Inventory i) -> i.getQuantity())
                );

        products.stream().forEach(p -> p.setQuantity(inventoryMap.get(p.getItemId())));
        return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> readOne(@PathVariable("id") String id) {
        Product product = productRepository.findOne(id);
        try {
          Inventory inventory = inventoryClient.getInventoryStatus(id);
          product.setQuantity(inventory.getQuantity());
        } catch (feign.FeignException e) {
          p.setQuantity(-1);
        }
        return new ResponseEntity<Product>(product,HttpStatus.OK);
    }
}