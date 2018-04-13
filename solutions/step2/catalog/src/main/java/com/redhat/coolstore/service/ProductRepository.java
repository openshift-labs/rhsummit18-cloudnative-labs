package com.redhat.coolstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.redhat.coolstore.model.Product;

@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate       jdbcTemplate;

    private RowMapper<Product> rowMapper = (rs, rowNum) -> {
                                             Product p = new Product();
                                             p.itemId = rs.getString("ITEMID");
                                             p.name = rs.getString("NAME");
                                             p.description = rs.getString("DESCRIPTION");
                                             p.price = rs.getDouble("PRICE");
                                             return p;
                                         };

    public Product findById(String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM CATALOG WHERE ITEMID = '" + id + "'", rowMapper);
    }

    public List<Product> readAll() {
        return jdbcTemplate.query("SELECT * FROM CATALOG", rowMapper);
    }
}
