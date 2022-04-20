package com.micropos.gateway.service;



import com.micropos.datatype.cart.Item;
import com.micropos.datatype.product.Product;
import com.micropos.gateway.dto.ItemFieldsDto;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public interface PosGatewayService {
    public List<Product> getProducts();

    public Product getProduct(String productId);

    public List<Item> getCart();

    public List<Item> addItem(ItemFieldsDto itemFieldsDto);

    public List<Item> delItem(ItemFieldsDto itemFieldsDto);
}
