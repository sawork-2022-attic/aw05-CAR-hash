package com.micropos.gateway.controller;

import com.micropos.datatype.product.Product;
import com.micropos.gateway.api.ProductApi;
import com.micropos.gateway.dto.ProductDto;
import com.micropos.gateway.mapper.ProductMapper;
import com.micropos.gateway.service.PosGatewayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("gateway")
public class ProductsController implements ProductApi {
    private final ProductMapper productMapper;

    private final PosGatewayService posGatewayService;

    public ProductsController(PosGatewayService posGatewayService, ProductMapper productMapper) {
        this.productMapper = productMapper;
        this.posGatewayService = posGatewayService;
    }

    @Override
    public ResponseEntity<List<ProductDto>> listProducts() {
        List<Product> products=posGatewayService.getProducts();
        List<ProductDto> nP=(List<ProductDto>) productMapper.toProductsDto(products);
        return new ResponseEntity<List<ProductDto>>(nP,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProductDto> showProductById(String productId) {
        Product product=posGatewayService.getProduct(productId);
        ProductDto nP=productMapper.toProductDto(product);
        return new ResponseEntity<>(nP,HttpStatus.OK);
    }
}
