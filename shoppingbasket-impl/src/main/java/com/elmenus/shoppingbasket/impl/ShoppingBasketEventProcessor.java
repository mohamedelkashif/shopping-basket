package com.elmenus.shoppingbasket.impl;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.elmenus.shoppingbasket.impl.ShoppingBasketEvent.ItemAddedToShoppingBasket;
import com.elmenus.shoppingbasket.impl.ShoppingBasketEvent.ItemAmountUpdatedInShoppingBasket;
import com.elmenus.shoppingbasket.impl.ShoppingBasketEvent.ItemRemovedFromShoppingBasket;
import com.elmenus.shoppingbasket.impl.ShoppingBasketEvent.ShoppingBasketCreated;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatement;

public class ShoppingBasketEventProcessor extends ReadSideProcessor<ShoppingBasketEvent> {

    public static final String TABLE_NAME_SHOPPINGBASKET = "shoppingbasket";
    public static final String TABLE_NAME_SHOPPINGBASKET_ITEM = "shoppingbasket_item";
    private final CassandraSession session;
    private final CassandraReadSide readSide;
    private PreparedStatement insertShoppingBasketPs;
    private PreparedStatement insertShoppingBasketItemPs;
    private PreparedStatement updatedShoppingBasketItemPs;
    private PreparedStatement deleteShoppingBasketItemPs;

    @Inject
    public ShoppingBasketEventProcessor(CassandraSession session, CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    @Override
    public PSequence<AggregateEventTag<ShoppingBasketEvent>> aggregateTags() {
        return TreePVector.singleton(ShoppingBasketEventTag.INSTANCE);
    }

    @Override
    public ReadSideHandler buildHandler() {
        return readSide.<ShoppingBasketEvent>builder("shoppingbasket_offset")
                .setGlobalPrepare(this::prepareCreateTables)
                .setPrepare((ignored) -> prepareStatements())
                .setEventHandler(ShoppingBasketCreated.class, this::processShoppingBasketCreated)
                .setEventHandler(ItemAddedToShoppingBasket.class, this::processItemAddedToShoppingBasket)
                .setEventHandler(ItemAmountUpdatedInShoppingBasket.class, this::processItemAmountUpdatedInShoppingBasket)
                .setEventHandler(ItemRemovedFromShoppingBasket.class, this::processItemRemovedFromShoppingBasket)
                .build();
    }

    private CompletionStage<Done> prepareCreateTables() {
        // @formatter:off
        CompletionStage<Done> createShoppingBasketTable = session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SHOPPINGBASKET + " (" +
                        "id uuid, " +
                        "userUuid text PRIMARY KEY, " +
                        "subTotal int, " +
                        "created timestamp" + ")");

        CompletionStage<Done> createShoppingBasketItemTable = session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SHOPPINGBASKET_ITEM + " (" +
                        "shoppingBasketId uuid, " +
                        "itemId text, " +
                        "amount int, " +
                        "added timestamp, " +
                        "lastUpdated timestamp, " +
                        "PRIMARY KEY (shoppingBasketId, itemId)" +
                        ")");
        // @formatter:on

        return createShoppingBasketTable
                .thenCombine(createShoppingBasketItemTable, ((done, done2) -> done));
    }

    private CompletionStage<Done> prepareStatements() {
        CompletionStage<Done> insertShoppingBasket = session.prepare("INSERT INTO " + TABLE_NAME_SHOPPINGBASKET +
                " (id, userUuid, subTotal, created) VALUES (?, ?, ?, ?)")
                .thenApply(ps -> {
                    insertShoppingBasketPs = ps;
                    return Done.getInstance();
                });

        CompletionStage<Done> insertShoppingBasketItem = session.prepare("INSERT INTO " + TABLE_NAME_SHOPPINGBASKET_ITEM +
                " (shoppingBasketId, itemId, amount, added) VALUES (?, ?, ?, ?)")
                .thenApply(ps -> {
                    insertShoppingBasketItemPs = ps;
                    return Done.getInstance();
                });

        CompletionStage<Done> updateShoppingBasketItem = session.prepare("UPDATE " + TABLE_NAME_SHOPPINGBASKET_ITEM +
                " SET amount=?, lastUpdated=? where shoppingBasketId=? and itemId=?")
                .thenApply(ps -> {
                    updatedShoppingBasketItemPs = ps;
                    return Done.getInstance();
                });

        CompletionStage<Done> deleteShoppingBasketItem = session.prepare("DELETE FROM " + TABLE_NAME_SHOPPINGBASKET_ITEM +
                " where shoppingBasketId=? and itemId=?")
                .thenApply(ps -> {
                    deleteShoppingBasketItemPs = ps;
                    return Done.getInstance();
                });

        return insertShoppingBasket
                .thenCombine(insertShoppingBasketItem, ((done, done2) -> done))
                .thenCombine(updateShoppingBasketItem, ((done, done2) -> done))
                .thenCombine(deleteShoppingBasketItem, ((done, done2) -> done));
    }

    private CompletionStage<List<BoundStatement>> processShoppingBasketCreated(ShoppingBasketCreated event) {
        BoundStatement bindInsertItem = insertShoppingBasketPs.bind()
                .setUUID("id", UUID.fromString(event.getId()))
                .setString("userUuid", event.getUserUuid())
                .setTimestamp("created", new Date());
        return completedStatement(bindInsertItem);
    }

    private CompletionStage<List<BoundStatement>> processItemAddedToShoppingBasket(ItemAddedToShoppingBasket event) {
        BoundStatement bindInsertItem = insertShoppingBasketItemPs.bind()
                .setUUID("shoppingBasketId", UUID.fromString(event.getShoppingBasketId()))
                .setString("itemId", event.getItemId())
                .setInt("amount", event.getInitialAmount())
                .setTimestamp("added", new Date());
        return completedStatement(bindInsertItem);
    }

    private CompletionStage<List<BoundStatement>> processItemAmountUpdatedInShoppingBasket(
            ItemAmountUpdatedInShoppingBasket event) {
        BoundStatement bindUpdateItem = updatedShoppingBasketItemPs.bind()
                .setUUID("shoppingBasketId", UUID.fromString(event.getShoppingBasketId()))
                .setString("itemId", event.getItemId())
                .setInt("amount", event.getNewAmount())
                .setTimestamp("lastUpdated", new Date());
        return completedStatement(bindUpdateItem);
    }

    private CompletionStage<List<BoundStatement>> processItemRemovedFromShoppingBasket(
            ItemRemovedFromShoppingBasket event) {
        BoundStatement bindDeleteItem = deleteShoppingBasketItemPs.bind()
                .setUUID("shoppingBasketId", UUID.fromString(event.getShoppingBasketId()))
                .setString("itemId", event.getItemId());
        return completedStatement(bindDeleteItem);
    }
}
