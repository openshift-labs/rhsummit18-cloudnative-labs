package com.redhat.coolstore.client;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.json;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers.startsWith;
import static org.assertj.core.api.Assertions.assertThat;
import io.specto.hoverfly.junit.rule.HoverflyRule;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.redhat.coolstore.model.Inventory;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InventoryClientTest {

    @Autowired
    InventoryClient inventoryClient;

    private static Inventory mockInventory;

    static {
        mockInventory = new Inventory();
        mockInventory.quantity = 98;
        mockInventory.itemId = "1234";
    }

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode(dsl(
            service("inventory:8080")
                    .get(startsWith("/services/inventory"))
                    .willReturn(success(json(mockInventory)))));

    @Test
    public void testInventoryClient() {
        Inventory inventory = inventoryClient.getInventoryStatus("1234");
        assertThat(inventory)
                .returns(98,i -> i.quantity)
                .returns("1234",i -> i.itemId);
    }
}