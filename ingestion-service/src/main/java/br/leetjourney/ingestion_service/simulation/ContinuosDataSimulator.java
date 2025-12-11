package br.leetjourney.ingestion_service.simulation;


import br.leetjourney.ingestion_service.dto.EnergyUsageDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.util.Random;

@Component
@Log4j2
public class ContinuosDataSimulator implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    private final Random random = new Random();

    @Value("${simulation.request-internal}")
    private int requestInternal;

    @Value("${simulation.ingestion-endpoint}")
    private String ingestionEndpoint;


    @Override
    public void run(String... args) throws Exception {
        log.info("Starting continuous data simulation...");
    }

    @Scheduled(fixedRateString = "${simulation.interval-ms}")
    public void sendMockData() {
        for (int i = 0; i < requestInternal; i++) {
            EnergyUsageDto dto = EnergyUsageDto.builder()
                    .deviceId((long) random.nextInt(1, 6))
                    .energyConsumed(Math.round(random.nextDouble(0.0, 2.0) * 100.0) / 100.0)
                    .timestamp(java.time.Instant.now()
                            .atZone(ZoneId.systemDefault()).toInstant())
                    .build();


            try{
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<EnergyUsageDto> request = new HttpEntity<>(dto, headers);
                restTemplate.postForEntity(ingestionEndpoint, request, Void.class);
                log.info("Sent mock data: {}", dto);
            } catch (Exception e) {
                log.error("Failed to send mock data: {}", dto, e);
            }
        }
    }
}
