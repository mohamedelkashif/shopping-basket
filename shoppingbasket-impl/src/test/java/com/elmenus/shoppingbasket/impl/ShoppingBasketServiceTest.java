package com.elmenus.shoppingbasket.impl;

import akka.Done;
import com.elmenus.shoppingbasket.api.*;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShoppingBasketServiceTest {

    private static ServiceTest.TestServer server;

    @BeforeClass
    public static void setUp() {
        server = startServer(defaultSetup()
                .withCluster(false)
                .withCassandra(true)
                .configureBuilder(guiceApplicationBuilder ->
                        // see: https://groups.google.com/d/msg/lagom-framework/WHgHES1Pmcs/K8c8M51cDAAJ
                        // and: https://github.com/lagom/lagom/issues/634
                        guiceApplicationBuilder.configure("cassandra-query-journal.eventual-consistency-delay", "0s")
                ));
    }

    @AfterClass
    public static void tearDown() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Test
    public void testShoppingBasketOperations() throws Exception {
        ShoppingBasketService service = server.client(ShoppingBasketService.class);

        final String shopId = "1";
        final String userUuId = "1";

        // Create the shopping basket
        CreateShoppingbasketRequest createShoppingbasketRequest = CreateShoppingbasketRequest.builder()
                .userUuid(userUuId)
                .build();
        final String shoppingBasketId = service.createShoppingBasket()
                .invoke(createShoppingbasketRequest)
                .toCompletableFuture().get(5, SECONDS);
        assertTrue(StringUtils.isNotBlank(shoppingBasketId));

        // Add an item
        final String itemId = "1234_a";
        AddItemToShoppingbasketRequest addItemToShoppingbasketRequest = AddItemToShoppingbasketRequest.builder()
                .itemId(itemId)
                .initialAmount(1)
                .build();
        assertEquals(Done.getInstance(), service.addItemToShoppingBasket(shoppingBasketId)
                .invoke(addItemToShoppingbasketRequest)
                .toCompletableFuture()
                .get(5, SECONDS));

        // Update amount of item
        UpdateItemAmountInShoppingbasketRequest updateItemAmountInShoppingbasketRequest =
                UpdateItemAmountInShoppingbasketRequest.builder()
                        .itemId(itemId)
                        .newAmount(2)
                        .build();
        assertEquals(Done.getInstance(), service.updateItemAmountInShoppingBasket(shoppingBasketId)
                .invoke(updateItemAmountInShoppingbasketRequest)
                .toCompletableFuture()
                .get(5, SECONDS));

        // Add another item
        final String itemId2 = "5678_b";
        AddItemToShoppingbasketRequest addItemToShoppingbasketRequest2 = AddItemToShoppingbasketRequest.builder()
                .itemId(itemId2)
                .initialAmount(1)
                .build();
        assertEquals(Done.getInstance(), service.addItemToShoppingBasket(shoppingBasketId)
                .invoke(addItemToShoppingbasketRequest2)
                .toCompletableFuture().get(5, SECONDS));

        // Check contents of shopping basket
        ShoppingBasket shoppingBasket =
                service.getShoppingBasket(shoppingBasketId).invoke().toCompletableFuture().get(5, SECONDS);
        assertEquals(2, shoppingBasket.getItems().size());

        assertThat(shoppingBasket.getItems()).containsExactlyInAnyOrder(
                new ShoppingBasketItem(itemId, 2, 3),
                new ShoppingBasketItem(itemId2, 1, 4)
        );

        // Remove one item
        RemoveItemFromShoppingbasketRequest removeItemFromShoppingbasketRequest = RemoveItemFromShoppingbasketRequest.builder()
                .itemId(itemId)
                .build();
        assertEquals(Done.getInstance(), service.removeItemFromShoppingBasket(shoppingBasketId)
                .invoke(removeItemFromShoppingbasketRequest)
                .toCompletableFuture()
                .get(5, SECONDS));

        // Check contents of shopping basket
        shoppingBasket =
                service.getShoppingBasket(shoppingBasketId).invoke().toCompletableFuture().get(5, SECONDS);

        assertThat(shoppingBasket.getItems()).containsOnly(new ShoppingBasketItem(itemId2, 1, 1));
    }


    @Test
    public void testGetShoppingBasketItemsFromReadStorage() throws Exception {
        ShoppingBasketService service = server.client(ShoppingBasketService.class);

        final String shopId = "1";
        final String userUuId = "1";

        // Create the shopping basket
        CreateShoppingbasketRequest createShoppingbasketRequest = CreateShoppingbasketRequest.builder()
                .userUuid(userUuId)
                .build();
        final String shoppingBasketId = service.createShoppingBasket()
                .invoke(createShoppingbasketRequest)
                .toCompletableFuture().get(5, SECONDS);

        // Add an item
        final String itemId = "1234_a";
        AddItemToShoppingbasketRequest addItemToShoppingbasketRequest = AddItemToShoppingbasketRequest.builder()
                .itemId(itemId)
                .initialAmount(3)
                .build();
        service.addItemToShoppingBasket(shoppingBasketId)
                .invoke(addItemToShoppingbasketRequest)
                .toCompletableFuture()
                .get(5, SECONDS);

        // Update the amount
        UpdateItemAmountInShoppingbasketRequest updateItemAmountInShoppingbasketRequest =
                UpdateItemAmountInShoppingbasketRequest.builder()
                        .itemId(itemId)
                        .newAmount(5)
                        .build();
        service.updateItemAmountInShoppingBasket(shoppingBasketId)
                .invoke(updateItemAmountInShoppingbasketRequest)
                .toCompletableFuture()
                .get(5, SECONDS);
    }
}
