package com.elmenus.shoppingbasket.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
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
public class AddItemToShoppingbasketRequest {

    @NonNull
    String itemId;

    @NonNull
    int initialAmount;

    int price;

    @JsonCreator
    public AddItemToShoppingbasketRequest(String uuid, int initialAmount, int price) {
        this.itemId = Preconditions.checkNotNull(UUID.randomUUID().toString(), "productId");
        this.initialAmount = initialAmount;
        this.price = price;
    }

}
