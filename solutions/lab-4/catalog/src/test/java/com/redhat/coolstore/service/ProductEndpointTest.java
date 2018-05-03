package com.redhat.coolstore.service;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.json;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.serverError;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.assertj.core.api.Assertions.assertThat;
import io.specto.hoverfly.junit.rule.HoverflyRule;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.redhat.coolstore.model.Inventory;
import com.redhat.coolstore.model.Product;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductEndpointTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static Inventory mockFedoraInventory, mockStickersInventory, mockDefaultInventory;

    static {
        mockFedoraInventory = new Inventory();
        mockFedoraInventory.setQuantity(123);
        mockFedoraInventory.setItemId("329299");
        
        mockStickersInventory = new Inventory();
        mockStickersInventory.setQuantity(98);
        mockStickersInventory.setItemId("329199");
    
        mockDefaultInventory = new Inventory();
        mockDefaultInventory.setQuantity(0);
        mockDefaultInventory.setItemId("{{ Request.Path.[3] }}");
    }

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode(dsl(
        service("mock-service.example.com:8080")                     
                .get("/services/inventory/329299")
                    .willReturn(success(json(mockFedoraInventory)))
                .get("/services/inventory/329199")
                    .willReturn(success(json(mockStickersInventory)))
                .get("/services/inventory/165613")
                    .willReturn(success(json(mockDefaultInventory)))
                .get("/services/inventory/165614")
                    .willReturn(success(json(mockDefaultInventory)))
                .get("/services/inventory/165954")
                    .willReturn(success(json(mockDefaultInventory)))
                .get("/services/inventory/444434")
                    .willReturn(success(json(mockDefaultInventory)))
                .get("/services/inventory/444435")
                    .willReturn(success(json(mockDefaultInventory)))
                .get("/services/inventory/444436")
                    .willReturn(serverError().body("Inventory is down"))
                    //.willReturn(success(json(mockDefaultInventory)))
    ));
    
    @Test
    public void test_retrieving_one_product() {
        hoverflyRule.printSimulationData();
        ResponseEntity<Product> response
                = restTemplate.getForEntity("/services/product/329199", Product.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Product product = response.getBody();
        assertThat(product)
                .returns("329199",p -> p.getItemId())
                .returns("Forge Laptop Sticker",p -> p.getName())
                .returns(98,p -> p.getQuantity())
                .returns(8.50,p -> p.getPrice());
    }
    
    @Test
    public void check_that_endpoint_returns_a_correct_list() {
        ResponseEntity<List<Product>> rateResponse =
                restTemplate.exchange("/services/products",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Product>>() {
                        });

        List<Product> productList = rateResponse.getBody();
        assertThat(productList).isNotNull();
        assertThat(productList).isNotEmpty();
        List<String> names = productList.stream().map(p -> p.getName()).collect(Collectors.toList());
        assertThat(names).contains("Red Fedora","Forge Laptop Sticker","Oculus Rift");   
    }
    
    @Test
    public void test_fallback() {
        hoverflyRule.printSimulationData();
        ResponseEntity<Product> response
                = restTemplate.getForEntity("/services/product/444436", Product.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Product product = response.getBody();
        assertThat(product)
                .returns("444436",p -> p.getItemId())
                .returns("Lytro Camera",p -> p.getName())
                .returns(-1,p -> p.getQuantity())
                .returns(44.30,p -> p.getPrice());
    }
}