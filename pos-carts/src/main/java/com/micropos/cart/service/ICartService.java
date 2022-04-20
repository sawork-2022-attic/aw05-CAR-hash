package com.micropos.cart.service;

import com.micropos.datatype.cart.Item;

import java.util.List;

public interface ICartService {
    public boolean checkProduct(String productId);
    public List<Item> addItem(Item item, List<Item> cart);
    public List<Item> delItem(Item item,List<Item> cart);
}
