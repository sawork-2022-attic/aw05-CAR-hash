package com.micropos.cart.service;

import com.micropos.cart.model.Item;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService implements ICartService{
    @Override
    public List<Item> addItem(Item item,List<Item> cart) {
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
