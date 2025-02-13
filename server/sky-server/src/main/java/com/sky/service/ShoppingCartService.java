package com.sky.service;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ShoppingCartService {

    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    public List<ShoppingCart> showShoppingCart();

    public void clean();

    public void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
