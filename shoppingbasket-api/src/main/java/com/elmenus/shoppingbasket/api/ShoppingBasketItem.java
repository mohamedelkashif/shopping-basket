package com.elmenus.shoppingbasket.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;

import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
@JsonDeserialize
@Value
@Builder
@Wither
//@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class ShoppingBasketItem implements Jsonable {

    @NonNull
    String uuid;

    int quantity;

    int price;


    @JsonCreator
    public ShoppingBasketItem(String uuid, int quantity, int price) {
        this.uuid = Preconditions.checkNotNull(UUID.randomUUID().toString(), "productId");
        this.quantity = quantity;
        this.price = price;
    }
}
