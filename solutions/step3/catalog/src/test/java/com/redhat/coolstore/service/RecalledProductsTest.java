package com.redhat.coolstore.service;

import com.redhat.coolstore.model.Inventory;
import com.redhat.coolstore.model.Product;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.json;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations="classpath:test-recalled-products.properties")
public class RecalledProductsTest {

    @Autowired
    CatalogService catalogService;

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode(dsl(
            service("inventory:8080")
//                  .andDelay(500, TimeUnit.MILLISECONDS).forMethod("GET")
                    .get("/services/inventory/165613")
                        .willReturn(success(json(new Inventory("165613",13))))
                    .get("/services/inventory/165614")
                        .willReturn(success(json(new Inventory("165614",85))))
                    .get("/services/inventory/165954")
                        .willReturn(success(json(new Inventory("165954",78))))
                    .get("/services/inventory/329199")
                        .willReturn(success(json(new Inventory("329199",67))))
                    .get("/services/inventory/329299")
                        .willReturn(success(json(new Inventory("329299",98)))) //TODO: Replace with .willReturn(serverError())
                    .get("/services/inventory/444434")
                        .willReturn(success(json(new Inventory("444434",73))))
                    .get("/services/inventory/444435")
                        .willReturn(success(json(new Inventory("444435",64))))
                    .get("/services/inventory/444436")
                        .willReturn(success(json(new Inventory("444436",30))))

    ));

    @Test
    public void verify_recalled() throws Exception {
        List<Product> productList = catalogService.readAll();
        assertThat(productList).isNotNull();
        assertThat(productList).isNotEmpty();
        List<String> itemIds = productList.stream().map(Product::getItemId).collect(Collectors.toList());
        assertThat(itemIds).doesNotContain("329299", "329199");
    }
}