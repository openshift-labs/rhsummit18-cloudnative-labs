package com.redhat.coolstore.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.redhat.coolstore.model.Inventory;

import java.util.List;

@FeignClient(name="inventory")
public interface InventoryClient {
    @RequestMapping(method = RequestMethod.GET, value = "/services/inventory/{itemId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    Inventory getInventoryStatus(@PathVariable("itemId") String itemId);

    @RequestMapping(method = RequestMethod.GET, value = "/services/inventory/all", consumes = {MediaType.APPLICATION_JSON_VALUE})
    List<Inventory> getInventoryStatusForAll();
}
