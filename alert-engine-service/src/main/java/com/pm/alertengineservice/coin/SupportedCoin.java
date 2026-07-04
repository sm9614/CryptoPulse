package com.pm.alertengineservice.coin;

public enum SupportedCoin {
    BTC, ETH, SOL, DOGE;

    public static boolean isSupported(String value) {
        if (value == null) {
            return false;
        }

        for (SupportedCoin coin : values()) {
            if (coin.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
