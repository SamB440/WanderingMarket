package com.convallyria.wanderingmarket.market;

import com.convallyria.wanderingmarket.market.item.MarketItem;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class GlobalMarket {

    private final List<MarketItem> marketItems = new ArrayList<>();

    public void addMarketItem(final MarketItem marketItem) {
        marketItems.add(marketItem);
    }

    public void removeMarketItem(final MarketItem marketItem) {
        marketItems.remove(marketItem);
    }

    public List<MarketItem> getMarketItems() {
        return ImmutableList.copyOf(marketItems);
    }
}
