package com.pm.priceingestionservice.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.priceingestionservice.kafka.PriceEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class KrakenMessageParser {
    private final ObjectMapper objectMapper;

    public KrakenMessageParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String normalizeSymbol(String krakenPair) {
        String base = krakenPair.split("/")[0];
        return SupportedCoin.fromKrakenBase(base).name();
    }

    public Optional<PriceEvent> parse(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);

            if (node.isObject()) {
                return Optional.empty();
            }

            if (!node.isArray() || node.size() < 4) {
                return Optional.empty();
            }

            if (!node.get(2).asText().equals("trade")) {
                return Optional.empty();
            }

            JsonNode trades = node.get(1);
            JsonNode firstTrade = trades.get(0);
            double price = firstTrade.get(0).asDouble();
            String symbol = node.get(3).asText();

            PriceEvent event = new PriceEvent();
            event.setSymbol(normalizeSymbol(symbol));
            event.setPrice(price);
            event.setTimestamp(System.currentTimeMillis());

            return Optional.of(event);

        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
