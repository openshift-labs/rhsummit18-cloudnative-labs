package com.redhat.coolstore.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Product> productList = productRepository.readAll();

        //Get all the inventory and convert it to a Map.
        Map<String, Integer> inventoryMap = inventoryClient.getInventoryStatusForAll()
                .stream()
                .collect(Collectors.toMap((Inventory i) -> i.itemId, (Inventory i) -> i.quantity));

        productList.stream().forEach(p -> p.quantity = inventoryMap.get(p.itemId));
        return new ResponseEntity<List<Product>>(productList,HttpStatus.OK);
    }
    
    @ResponseBody
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> readOne(@PathVariable("id") String id) {
        Product product = productRepository.findById(id);
        try {
            Inventory inventory = inventoryClient.getInventoryStatus(id);
            product.quantity = inventory.quantity;
        } catch (feign.FeignException e) {
            product.quantity = -1;
        }
        return new ResponseEntity<Product>(product,HttpStatus.OK);
    }
}