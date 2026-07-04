package com.pm.priceingestionservice.websocket;

public enum SupportedCoin {
    BTC("XBT", "XBT/USD"),
    ETH("ETH", "ETH/USD"),
    SOL("SOL", "SOL/USD"),
    DOGE("XDG", "XDG/USD");

    private final String krakenBase;
    private final String krakenPair;

    SupportedCoin(String krakenBase, String krakenPair) {
        this.krakenBase = krakenBase;
        this.krakenPair = krakenPair;
    }

    public String getKrakenBase() {
        return krakenBase;
    }

    public String getKrakenPair() {
        return krakenPair;
    }

    public static SupportedCoin fromKrakenBase(String krakenBase) {
        for (SupportedCoin coin : values()) {
            if (coin.krakenBase.equalsIgnoreCase(krakenBase)) {
                return coin;
            }
        } throw new IllegalArgumentException("Unsupported Kraken base: " + krakenBase);
    }
}
