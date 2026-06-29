package com.pm.priceingestionservice.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.priceingestionservice.kafka.KafkaProducer;
import com.pm.priceingestionservice.kafka.PriceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Optional;

@Component
public class KrakenWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(KrakenWebSocketHandler.class);
    private final KafkaProducer kafkaProducer;
    private final KrakenMessageParser messageParser;
    private final ObjectMapper objectMapper;

    public KrakenWebSocketHandler(KafkaProducer kafkaProducer,
                                  KrakenMessageParser messageParser,
                                  ObjectMapper objectMapper) {

        this.kafkaProducer = kafkaProducer;
        this.messageParser = messageParser;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String jsonPayload = message.getPayload();
        Optional<PriceEvent> priceEvent = messageParser.parse(jsonPayload);
            priceEvent.ifPresent(event -> {
                try {
                    kafkaProducer.sendEvent(objectMapper.writeValueAsString(event));
                } catch (Exception e) {
                    log.error("Failed to deserialize price event: {}", event);
                }
            });
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
