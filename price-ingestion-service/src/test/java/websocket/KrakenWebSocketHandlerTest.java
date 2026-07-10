package websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.priceingestionservice.kafka.KafkaProducer;
import com.pm.priceingestionservice.kafka.PriceEvent;
import com.pm.priceingestionservice.websocket.KrakenMessageParser;
import com.pm.priceingestionservice.websocket.KrakenWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KrakenWebSocketHandlerTest {

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private KrakenMessageParser parser;

    @Mock
    private WebSocketSession session;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private KrakenWebSocketHandler handler;

    @Test
    void shouldSendParsedEvent() throws Exception {
        String payload = "incoming-json";

        PriceEvent event = new PriceEvent();
        event.setSymbol("BTC");
        event.setPrice(50000.0);
        event.setTimestamp(Instant.now().toEpochMilli());

        when(parser.parse(payload)).thenReturn(Optional.of(event));
        handler.handleMessage(session, new TextMessage(payload));
        verify(kafkaProducer).sendEvent(objectMapper.writeValueAsString(event));
    }

    @Test
    void shouldNotSendEvent() throws Exception {
        when((parser.parse(anyString()))).thenReturn(Optional.empty());
        handler.handleMessage(session, new TextMessage("invalid-json"));
        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void afterConnectionEstablishedShouldSendMessage() throws Exception {
        when(session.getId()).thenReturn("session-id");
        handler.afterConnectionEstablished(session);
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        String sent = captor.getValue().getPayload();
        assertTrue(sent.contains("\"event\":\"subscribe\""));
        assertTrue(sent.contains("\"subscription\""));
        assertTrue(sent.contains("\"trade\""));
    }

    @Test
    void afterConnectionClosedShouldNotThrow() throws Exception {
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);
        verify(session).getId();
    }
}
