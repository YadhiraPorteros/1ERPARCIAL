package com.PARCIAL.controladores;

import com.PARCIAL.dtos.DNARequest;
import com.PARCIAL.dtos.DNAResponse;
import com.PARCIAL.dtos.StatsResponse;
import com.PARCIAL.servicios.DNAService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/magneto/dna")
public class DNAController {

    @Autowired
    private DNAService dnaService;

    @PostMapping("/mutant")
    public ResponseEntity<DNAResponse> isMutant(@Valid @RequestBody DNARequest dnaRequest) {
        boolean isMutant = dnaService.isMutant(dnaRequest.getDna());

        DNAResponse response = new DNAResponse(isMutant);

        if (isMutant) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        StatsResponse statsResponse = dnaService.getStats();
        return ResponseEntity.ok(statsResponse);
    }
}





