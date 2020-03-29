package com.elmenus.shoppingbasket.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.PSequence;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

public interface ShoppingBasketService extends Service {

    ServiceCall<CreateShoppingbasketRequest, String> createShoppingBasket();

    ServiceCall<NotUsed, ShoppingBasket> getShoppingBasket(String shoppingBasketId);

    ServiceCall<AddItemToShoppingbasketRequest, Done> addItemToShoppingBasket(String shoppingBasketId);

    ServiceCall<UpdateItemAmountInShoppingbasketRequest, Done> updateItemAmountInShoppingBasket(String shoppingBasketId);

    ServiceCall<RemoveItemFromShoppingbasketRequest, Done> removeItemFromShoppingBasket(String shoppingBasketId);

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("basket").withCalls(
                restCall(Method.POST, "/api/basket", this::createShoppingBasket),
                restCall(Method.GET, "/api/basket/:shoppingBasketId", this::getShoppingBasket),
                restCall(Method.PUT, "/api/basket/:shoppingBasketId", this::addItemToShoppingBasket),
                restCall(Method.PUT, "/api/basket/update/:shoppingBasketId", this::updateItemAmountInShoppingBasket)
        ).withAutoAcl(true);
        // @formatter:on
    }
}
