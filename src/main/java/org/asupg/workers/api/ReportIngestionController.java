package org.asupg.workers.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.workers.service.RequestOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/report-ingest")
@RequiredArgsConstructor
public class ReportIngestionController {

    private final RequestOrchestratorService requestOrchestratorService;

    @PostMapping("/execute")
    public ResponseEntity<Object> executeReport() {
        requestOrchestratorService.requestReport();
        return ResponseEntity.ok().build();
    }

}
