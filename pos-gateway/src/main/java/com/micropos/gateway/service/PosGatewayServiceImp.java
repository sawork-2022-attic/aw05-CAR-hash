package com.micropos.gateway.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropos.datatype.cart.Item;
import com.micropos.datatype.product.Product;
import com.micropos.gateway.dto.ItemFieldsDto;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.sf.json.JSONObject;

@Service
public class PosGatewayServiceImp implements PosGatewayService{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public List<Product> getProducts() {
        CircuitBreaker circuitBreaker=circuitBreakerFactory.create("circuitBreaker");

        Object obj = circuitBreaker.run(()->restTemplate.getForObject("http://pos-products-config/product/products",Object.class));
        assert obj != null;

        ObjectMapper mapper=new ObjectMapper();

        List<Product> products;

        products=mapper.convertValue(obj, new TypeReference<List<Product>>() {
        });

        return products;
    }

    @Override
    public Product getProduct(String productId) {
        CircuitBreaker circuitBreaker=circuitBreakerFactory.create("circuitBreaker");

        Object obj = circuitBreaker.run(()->restTemplate.getForObject("http://pos-products-config/product/products/"+productId,Object.class));
        assert obj != null;

        ObjectMapper mapper=new ObjectMapper();

        Product products;

        products=mapper.convertValue(obj, new TypeReference<Product>() {
        });

        return products;
    }

    @Override
    public List<Item> getCart() {
        return null;
    }

    @Override
    public List<Item> addItem(ItemFieldsDto itemFieldsDto) {
        CircuitBreaker circuitBreaker=circuitBreakerFactory.create("circuitBreaker");

        Object obj = circuitBreaker.run(()->restTemplate.postForObject("http://pos-cart-config/api/cart/add",itemFieldsDto,Object.class));

        ObjectMapper mapper=new ObjectMapper();

        List<Item> items;

        items=mapper.convertValue(obj, new TypeReference<List<Item>>() {
        });

        return items;
    }

    @Override
    public List<Item> delItem(ItemFieldsDto itemFieldsDto) {
        CircuitBreaker circuitBreaker=circuitBreakerFactory.create("circuitBreaker");
        Object obj = circuitBreaker.run(()->restTemplate.postForObject("http://pos-cart-config/api/cart/del",itemFieldsDto,Object.class));
        ObjectMapper mapper=new ObjectMapper();

        List<Item> items;

        items=mapper.convertValue(obj, new TypeReference<List<Item>>() {
        });

        return items;
    }


}
