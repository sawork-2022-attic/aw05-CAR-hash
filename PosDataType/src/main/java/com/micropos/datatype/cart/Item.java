package com.micropos.datatype.cart;
import java.io.Serializable;

public class Item implements Serializable {
    public String productId;
    public Integer quantity;
    public Item(){
        productId="";
        quantity= 0;
    }
    public Item(String productId,Long quantity){
        this.productId=productId;
        this.quantity= quantity.intValue();
    }
    public Item(String productIdId,Integer quantity){
        this.productId=productIdId;
        this.quantity=quantity;
    }
}
