package com.convallyria.wanderingmarket.market;

import com.convallyria.wanderingmarket.market.item.MarketItem;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalMarket {

    private final List<MarketItem> marketItems = new ArrayList<>();

    public void addMarketItem(final MarketItem marketItem) {
        marketItems.add(marketItem);
    }

    public void removeMarketItem(final MarketItem marketItem) {
        marketItems.remove(marketItem);
    }

    public ImmutableList<MarketItem> getMarketItems() {
        return ImmutableList.copyOf(marketItems);
    }

    public ImmutableList<MarketItem> getActiveMarketItems() {
        return ImmutableList.copyOf(marketItems.stream().filter(marketItem -> !marketItem.isSold()).collect(Collectors.toList()));
    }
}
