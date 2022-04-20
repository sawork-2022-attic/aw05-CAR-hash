package com.micropos.cart.service;

import com.micropos.datatype.cart.Item;
import com.micropos.datatype.product.Product;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CartService implements ICartService{

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean checkProduct(String productId) {
        try {
            restTemplate.getForObject("http://pos-products-config/product/products/" + productId, Product.class);
        }
        catch (HttpClientErrorException e){
            return false;
        }
        return true;
    }

    @Override
    public List<Item> addItem(Item item, List<Item> cart) {
        for (Item i:
             cart) {
            if(i.productId.equals(item.productId)){
                i.quantity+=item.quantity;
                return cart;
            }
        }
        cart.add(item);
        return cart;
    }

    @Override
    public List<Item> delItem(Item item,List<Item> cart){
        for (Item i:
                cart) {
            if(i.productId.equals(item.productId)){
                i.quantity-=item.quantity;
                if(i.quantity<0||item.quantity<0){
                    cart.remove(i);
                }
                return cart;
            }
        }
        return cart;
    }
}
