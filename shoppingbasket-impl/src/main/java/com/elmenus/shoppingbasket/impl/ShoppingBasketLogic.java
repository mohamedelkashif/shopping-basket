package com.elmenus.shoppingbasket.impl;

import com.elmenus.shoppingbasket.api.ShoppingBasket;
import com.elmenus.shoppingbasket.api.ShoppingBasketItem;
import org.pcollections.TreePVector;

import java.util.stream.Collectors;

public class ShoppingBasketLogic {

    /**
     * Can be used to add a new item or update the amount of an already existing item
     *
     * @param shoppingBasket
     * @param itemId         the unique identifier of the item/product
     * @param quantity       the number of products to add
     * @return a new immutable shopping basket with the added item
     */
    public static ShoppingBasket addItemToShoppingBasket(ShoppingBasket shoppingBasket, String itemId, int quantity, int price) {
        ShoppingBasket shoppingBasketWithoutItem = removeItemFromShoppingBasket(shoppingBasket, itemId);
        return shoppingBasketWithoutItem.withItems(
                shoppingBasketWithoutItem.getItems().plus(new ShoppingBasketItem(itemId, quantity, price))
        );
    }

    /**
     * @param shoppingBasket
     * @param itemId         the unique identifier of the item/product
     * @return a new immutable shopping basket without the removed item
     */
    public static ShoppingBasket removeItemFromShoppingBasket(ShoppingBasket shoppingBasket, String itemId) {
        return shoppingBasket.withItems(
                TreePVector.from(
                        shoppingBasket.getItems()
                                .stream().filter(shoppingBasketItem -> !shoppingBasketItem.getUuid().equals(itemId))
                                .collect(Collectors.toList())
                )
        );
    }

    public static int getAmountOfItems(ShoppingBasket shoppingBasket, String itemId) {
        return shoppingBasket.getItems()
                .stream().filter(shoppingBasketItem -> shoppingBasketItem.getUuid().equals(itemId))
                .map(ShoppingBasketItem::getQuantity)
                .reduce((total, itemAmount) -> total + itemAmount).orElse(0);
    }
}
