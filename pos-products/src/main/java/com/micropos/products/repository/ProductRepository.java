package com.micropos.products.repository;


import com.micropos.datatype.product.Product;

import java.util.List;

public interface ProductRepository {

    public List<Product> allProducts();

    public Product findProduct(String productId);

}