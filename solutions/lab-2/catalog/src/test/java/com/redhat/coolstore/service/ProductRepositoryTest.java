package com.redhat.coolstore.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.redhat.coolstore.model.Product;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ProductRepositoryTest {
    @Autowired
    ProductRepository repository;
    
    @Test
    public void test_findOne() {
        Product product = repository.findOne("444434");
        assertThat(product).isNotNull();
        assertThat(product.getName()).as("Verify product name").isEqualTo("Pebble Smart Watch");
        assertThat(product.getPrice()).as("Price should match ").isEqualTo(24.0);
    }
    
    @Test
    public void test_findAll() {
        Iterable<Product> products = repository.findAll();
        assertThat(products).isNotNull();
        assertThat(products).isNotEmpty();
        List<String> names = StreamSupport.stream(products.spliterator(), false).map(p -> p.getName()).collect(Collectors.toList());
        assertThat(names).contains("Red Fedora","Forge Laptop Sticker","Oculus Rift","Solid Performance Polo","16 oz. Vortex Tumbler","Pebble Smart Watch","Lytro Camera");
    }
}