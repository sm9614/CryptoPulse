package com.pm.priceingestionservice.websocket;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Component
public class KrakenWebSocketClient {
    private static final Logger log = LoggerFactory.getLogger(KrakenWebSocketClient.class);

    private final KrakenWebSocketHandler krakenWebSocketHandler;
    private final String KRAKEN_URL = "wss://ws.kraken.com";

    public KrakenWebSocketClient(KrakenWebSocketHandler krakenWebSocketHandler) {
        this.krakenWebSocketHandler = krakenWebSocketHandler;
    }

    @PostConstruct
    public void openConnection() {
        log.info("Connecting to Kraken WebSocket: {}", KRAKEN_URL);
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

        webSocketClient.execute(krakenWebSocketHandler, KRAKEN_URL).exceptionally(ex -> {
            log.error("Failed to connect to Kraken WebSocket: {}", KRAKEN_URL, ex);
            return null;
        });
    }
}