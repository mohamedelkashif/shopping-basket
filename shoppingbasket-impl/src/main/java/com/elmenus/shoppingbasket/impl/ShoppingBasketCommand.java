package com.elmenus.shoppingbasket.impl;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;
import com.elmenus.shoppingbasket.api.ShoppingBasket;

import javax.annotation.concurrent.Immutable;

public interface ShoppingBasketCommand extends Jsonable {

    @Immutable
    @JsonDeserialize
    @Value
    @Builder
    @Wither
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class CreateShoppingBasket implements ShoppingBasketCommand, PersistentEntity.ReplyType<String> {
        @NonNull
        String userUuid;
    }


    @Immutable
    @JsonDeserialize
    @Value
    @Builder
    @Wither
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class GetShoppingBasket implements ShoppingBasketCommand, PersistentEntity.ReplyType<ShoppingBasket> {
    }

    @Immutable
    @JsonDeserialize
    @Value
    @Builder
    @Wither
   //@AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class AddItemInShoppingBasket implements ShoppingBasketCommand, PersistentEntity.ReplyType<Done> {
        @NonNull
        String itemId;

        @NonNull
        int initialAmount;

        int price;

        public int getPrice() {
            return price;
        }

        @JsonCreator
        public AddItemInShoppingBasket(String itemId, int initialAmount, int price) {
            this.itemId = itemId;
            this.initialAmount = initialAmount;
            this.price = price;
        }


    }

    @Immutable
    @JsonDeserialize
    @Value
    @Builder
    @Wither
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class UpdateItemAmountInShoppingBasket implements ShoppingBasketCommand, PersistentEntity.ReplyType<Done> {
        @NonNull
        String itemId;

        @NonNull
        int newAmount;
    }

    @Immutable
    @JsonDeserialize
    @Value
    @Builder
    @Wither
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class RemoveItemFromShoppingBasket implements ShoppingBasketCommand, PersistentEntity.ReplyType<Done> {
        @NonNull
        String itemId;

    }
}
