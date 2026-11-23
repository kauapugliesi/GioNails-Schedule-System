package br.com.giovannapugliesi.agenda.controller;

import br.com.giovannapugliesi.agenda.dto.StatusDTO;
import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public StatusDTO health() {
        return new StatusDTO("UP", "gionails-schedule-api", Instant.now());
    }
}
