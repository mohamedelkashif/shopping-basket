package com.elmenus.shoppingbasket.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.elmenus.shoppingbasket.api.ShoppingBasketService;

public class ShoppingBasketModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(ShoppingBasketService.class, ShoppingBasketServiceImpl.class));
        bind(ShoppingBasketRepository.class).to(ShoppingBasketRepositoryImpl.class);
    }
}
