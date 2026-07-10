package websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.priceingestionservice.kafka.PriceEvent;
import com.pm.priceingestionservice.websocket.KrakenMessageParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KrakenMessageParserTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonNode root;

    @Mock
    private JsonNode trades;

    @Mock
    private JsonNode firstTrade;

    @Mock
    private JsonNode priceNode;

    @Mock
    private JsonNode typeNode;

    @Mock
    private JsonNode symbolNode;

    @InjectMocks
    private KrakenMessageParser parser;

    @Test
    void shouldReturnPriceEventWhenValidTradeMessage() throws Exception {

        when(objectMapper.readTree(anyString())).thenReturn(root);

        when(root.isObject()).thenReturn(false);
        when(root.isArray()).thenReturn(true);
        when(root.size()).thenReturn(4);

        when(root.get(2)).thenReturn(typeNode);
        when(typeNode.asText()).thenReturn("trade");

        when(root.get(1)).thenReturn(trades);
        when(trades.get(0)).thenReturn(firstTrade);
        when(firstTrade.get(0)).thenReturn(priceNode);
        when(priceNode.asDouble()).thenReturn(50000.0);

        when(root.get(3)).thenReturn(symbolNode);
        when(symbolNode.asText()).thenReturn("XBT/USD");

        Optional<PriceEvent> result = parser.parse("payload");

        assertTrue(result.isPresent());
        assertEquals("BTC", result.get().getSymbol());
        assertEquals(50000.0, result.get().getPrice());
        assertTrue(result.get().getTimestamp() > 0);
    }

    @Test
    void shouldReturnEmptyWhenNodeIsObject() throws Exception {

        when(objectMapper.readTree(anyString())).thenReturn(root);
        when(root.isObject()).thenReturn(true);

        assertTrue(parser.parse("payload").isEmpty());
    }

    @Test
    void ShouldReturnEmptyWhenNodeIsNotArray() throws Exception {

        when(objectMapper.readTree(anyString())).thenReturn(root);
        when(root.isObject()).thenReturn(false);
        when(root.isArray()).thenReturn(false);

        assertTrue(parser.parse("payload").isEmpty());
    }

    @Test
    void parse_ShouldReturnEmpty_WhenArrayTooSmall() throws Exception {

        when(objectMapper.readTree(anyString())).thenReturn(root);
        when(root.isObject()).thenReturn(false);
        when(root.isArray()).thenReturn(true);
        when(root.size()).thenReturn(3);

        assertTrue(parser.parse("payload").isEmpty());
    }

    @Test
    void ShouldReturnEmptyWhenMessageTypeIsNotTrade() throws Exception {

        when(objectMapper.readTree(anyString())).thenReturn(root);
        when(root.isObject()).thenReturn(false);
        when(root.isArray()).thenReturn(true);
        when(root.size()).thenReturn(4);

        when(root.get(2)).thenReturn(typeNode);
        when(typeNode.asText()).thenReturn("heartbeat");

        assertTrue(parser.parse("payload").isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenReadTreeThrowsException() throws Exception {

        when(objectMapper.readTree(anyString()))
                .thenThrow(new RuntimeException("Invalid JSON"));

        assertTrue(parser.parse("payload").isEmpty());
    }
}