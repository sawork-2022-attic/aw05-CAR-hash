package com.micropos.gateway.controller;

import com.micropos.datatype.cart.Item;
import com.micropos.datatype.product.Product;
import com.micropos.gateway.api.CartApi;
import com.micropos.gateway.dto.ItemDto;
import com.micropos.gateway.dto.ItemFieldsDto;
import com.micropos.gateway.dto.ProductDto;
import com.micropos.gateway.mapper.ItemMapper;
import com.micropos.gateway.mapper.ProductMapper;
import com.micropos.gateway.service.PosGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("gateway")
public class CartController implements CartApi {
    private final ItemMapper itemMapper;

    private final PosGatewayService posGatewayService;


    public CartController(PosGatewayService posGatewayService, ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
        this.posGatewayService = posGatewayService;
    }

    @Override
    public ResponseEntity<List<ItemDto>> addItem(ItemFieldsDto itemFieldsDto) {
        List<Item> cart=posGatewayService.addItem(itemFieldsDto);
        List<ItemDto> dc=(List<ItemDto>) itemMapper.toProductsDto(cart);
        return new ResponseEntity<>(dc,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ItemDto>> delItem(ItemFieldsDto itemFieldsDto) {
        List<Item> cart=posGatewayService.delItem(itemFieldsDto);
        List<ItemDto> dc=(List<ItemDto>) itemMapper.toProductsDto(cart);
        return new ResponseEntity<>(dc,HttpStatus.ACCEPTED);
    }
}
