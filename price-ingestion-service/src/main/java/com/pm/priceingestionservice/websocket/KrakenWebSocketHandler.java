package com.pm.priceingestionservice.websocket;

import com.pm.priceingestionservice.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class KrakenWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(KrakenWebSocketHandler.class);
    private final KafkaProducer kafkaProducer;

    public KrakenWebSocketHandler(KafkaProducer kafkaProducer ) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String jsonPayload = message.getPayload();
        log.info("Received message from Kraken WebSocket: {}", jsonPayload);
        kafkaProducer.sendEvent(jsonPayload);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connection established with Kraken: {}", session.getId());
        String subscribeMessage = """
        {
            "event": "subscribe",
            "pair": ["XBT/USD"],
            "subscription": {"name": "trade"}
        }
        """;

        session.sendMessage(new TextMessage(subscribeMessage));

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket connection closed with Kraken: {}, Status: {}", session.getId(), status);
    }
}
