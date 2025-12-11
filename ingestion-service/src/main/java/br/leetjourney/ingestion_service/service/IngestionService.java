package br.leetjourney.ingestion_service.service;


import br.leetjourney.ingestion_service.dto.EnergyUsageDto;
import kafkaEvent.EnergyUsageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j

public class IngestionService {

    private final KafkaTemplate<String, EnergyUsageEvent> kafkaTemplate;


    public IngestionService(KafkaTemplate<String, EnergyUsageEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void ingestEnergyUsage(EnergyUsageDto input) {
        EnergyUsageEvent event = EnergyUsageEvent.builder()
                .deviceId(input.deviceId())
                .energyConsumed(input.energyConsumed())
                .timestamp(input.timestamp())
                .build();



        kafkaTemplate.send("energy-usage", event);
        log.info("Published EnergyUsageEvent to Kafka: {}", event);
    }
}
