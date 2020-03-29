package com.elmenus.shoppingbasket.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;

import javax.annotation.concurrent.Immutable;

public interface ShoppingBasketEvent extends Jsonable, AggregateEvent<ShoppingBasketEvent> {

    @Override
    default public AggregateEventTag<ShoppingBasketEvent> aggregateTag() {
        return ShoppingBasketEventTag.INSTANCE;
    }

    @Immutable
    @JsonDeserialize
    @Value
    @Builder
    @Wither
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class ShoppingBasketCreated implements ShoppingBasketEvent {
        @NonNull
        String id;
        @NonNull
        String userUuid;

        int subTotal;

        public int getSubTotal() {
            return subTotal;
        }
    }


    @Immutable
    @JsonDeserialize
    @Value
    @Builder
    @Wither
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class ItemAddedToShoppingBasket implements ShoppingBasketEvent {
        @NonNull
        String shoppingBasketId;
        @NonNull
        String userUuid;
        @NonNull
        String itemId;
        @NonNull
        Integer initialAmount;

        int price;

        public int getPrice() {
            return price;
        }
    }

    @Immutable
    @JsonDeserialize
    @Value
    @Builder
    @Wither
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class ItemAmountUpdatedInShoppingBasket implements ShoppingBasketEvent {
        @NonNull
        String shoppingBasketId;
        @NonNull
        String itemId;
        @NonNull
        Integer newAmount;

        int price;

    }

    @Immutable
    @JsonDeserialize
    @Value
    @Builder
    @Wither
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class ItemRemovedFromShoppingBasket implements ShoppingBasketEvent {
        @NonNull
        String shoppingBasketId;
        @NonNull
        String itemId;
    }
}
