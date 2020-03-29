package com.elmenus.shoppingbasket.impl;


import com.elmenus.shoppingbasket.api.ShoppingBasketItem;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ShoppingBasketRepositoryImpl implements ShoppingBasketRepository {

    private CassandraSession db;

    @Inject
    public ShoppingBasketRepositoryImpl(CassandraSession db) {
        this.db = db;
    }


    @Override
    public CompletionStage<PSequence<ShoppingBasketItem>> getShoppingBasketItems(UUID shoppingBasketId) {
        return db.selectAll("SELECT * FROM " + ShoppingBasketEventProcessor.TABLE_NAME_SHOPPINGBASKET_ITEM +
                " WHERE shoppingBasketId=?", shoppingBasketId)
                .thenApply(items -> TreePVector.from(
                        items.stream()
                                .map(row -> ShoppingBasketItem
                                        .builder()
                                        .quantity(row.getInt("quantity"))
                                        .uuid(row.getString("uuid"))
                                        .build()).collect(Collectors.toList())
                ));
    }
}
