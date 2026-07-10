package kafka;

import com.pm.priceingestionservice.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Test
    void shouldSendMessage() {
        String payload = "{\"symbol\":\"BTCUSD\"}";
        kafkaProducer.sendEvent(payload);
        verify(kafkaTemplate).send("crypto-prices", payload);
    }

    @Test
    void shouldNotSend() {
        String payload = "{\"symbol\":\"BTCUSD\"}";
        doThrow(new RuntimeException("Kafka error"))
                .when(kafkaTemplate).send("crypto-prices", payload);

        kafkaProducer.sendEvent(payload);
        verify(kafkaTemplate).send("crypto-prices", payload);
    }
}
