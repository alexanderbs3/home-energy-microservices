package br.leetjourney.ingestion_service.simulation;

import br.leetjourney.ingestion_service.dto.EnergyUsageDto;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component

public class ParallelDataSimulation implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Value("${simulation.parallel-threads}")
    private int parallelThreads;

    // CORRIGIDO: Injetando requestsPerInterval
    @Value("${simulation.requests-per-interval}")
    private int requestsPerInterval;

    @Value("${simulation.endpoint}")
    private String ingestionEndpoint;

    // REMOVIDO: ExecutorService não é final. Ele será inicializado em run().
    private ExecutorService executorService;

    // CONSTRUTOR PADRÃO: Necessário se não injetar nada, para que o Spring possa criar o Bean.
    public ParallelDataSimulation() {
        // Nada de inicialização complexa aqui
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Parallel Data Simulation started.");
        // NOVO: Inicializa o ExecutorService após a injeção das @Value
        this.executorService = Executors.newFixedThreadPool(parallelThreads);
    }

    @Scheduled(fixedRateString = "${simulation.execution-interval-ms}")
    public void sendMockData() {
        // ... (Corpo do método sendMockData permanece o mesmo) ...
        int batchSize = requestsPerInterval / parallelThreads;
        int remaining = requestsPerInterval % parallelThreads;

        for (int i = 0; i < parallelThreads; i++) {
            int requestsForThisThread = batchSize + (i < remaining ? 1 : 0);
            executorService.submit(() -> {
                for (int j = 0; j < requestsForThisThread; j++) {
                    EnergyUsageDto dto = EnergyUsageDto.builder()
                            .deviceId((long) random.nextInt(1, 1001))
                            .energyConsumed(random.nextDouble(0.1, 5.0))
                            .timestamp(LocalDateTime.now()
                                    .atZone(ZoneId.systemDefault()).toInstant())
                            .build();
                    try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<EnergyUsageDto> request = new HttpEntity<>(dto, headers);
                        restTemplate.postForEntity(ingestionEndpoint, request, Void.class);
                        log.info("Sent mock data: " + dto);
                    } catch (Exception e) {
                        log.error("Error sending mock data: " + e.getMessage());
                    }
                }

            });

        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        log.info("Parallel Data Simulation stopped.");
    }
}