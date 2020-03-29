package com.elmenus.shoppingbasket.impl;

import com.elmenus.shoppingbasket.api.ShoppingBasketItem;
import org.pcollections.PSequence;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface ShoppingBasketRepository {
    CompletionStage<PSequence<ShoppingBasketItem>> getShoppingBasketItems(final UUID shoppingBasketId);
}
