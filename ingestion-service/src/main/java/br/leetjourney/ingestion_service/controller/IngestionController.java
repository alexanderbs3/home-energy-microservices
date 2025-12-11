package br.leetjourney.ingestion_service.controller;

import br.leetjourney.ingestion_service.dto.EnergyUsageDto;
import br.leetjourney.ingestion_service.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ingestion") // Mapeia para /api/v1/ingestion
@RequiredArgsConstructor
public class IngestionController {


    private final IngestionService ingestionService;


    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public void ingestData(@RequestBody EnergyUsageDto usageDto){
        ingestionService.ingestEnergyUsage(usageDto);
    }
}