package com.micropos.cart.service;

import com.micropos.cart.model.Item;

import java.util.List;

public interface ICartService {
    public List<Item> addItem(Item item,List<Item> cart);
    public List<Item> delItem(Item item,List<Item> cart);
}
