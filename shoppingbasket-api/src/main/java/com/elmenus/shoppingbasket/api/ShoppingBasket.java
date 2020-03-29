package com.elmenus.shoppingbasket.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.Wither;
import org.pcollections.PSequence;

//@Immutable
@JsonDeserialize
@Builder
@Wither
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class ShoppingBasket implements Jsonable {

    @NonNull
    String id;

    @NonNull
    String userUuid;

    double total;
    int subTotal;
    double tax;

    public int getSubTotal() {
        return subTotal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public PSequence<ShoppingBasketItem> getItems() {
        return items;
    }

    public void setItems(PSequence<ShoppingBasketItem> items) {
        this.items = items;
    }

    @NonNull
    PSequence<ShoppingBasketItem> items;
}
