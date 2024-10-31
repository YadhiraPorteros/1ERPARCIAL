package com.PARCIAL.controladores;

import com.PARCIAL.dtos.DNARequest;
import com.PARCIAL.dtos.DNAResponse;
import com.PARCIAL.dtos.StatsResponse;
import com.PARCIAL.servicios.DNAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin(origins = "*")
public class DNAController {

    @Autowired
    private DNAService dnaService;

    @PostMapping("/mutant")
    public CompletableFuture<ResponseEntity<?>> isMutant(@RequestBody DNARequest dnaRequest) {
        // Validación en el controlador
        if (!isValidDNA(dnaRequest.getDna())) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("El ADN no es válido. Debe ser una matriz NxN con caracteres A, T, C, G y tamaño mínimo 4x4.")
            );
        }

        // Llamada al servicio solo si la validación es exitosa
        return dnaService.checkIfMutantAsync(dnaRequest.getDna())
                .thenApply(isMutant -> {
                    String message = isMutant ? "Es un mutante." : "Es un humano.";
                    DNAResponse response = new DNAResponse(isMutant, message);

                    if (isMutant) {
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                    }
                });
    }

    // Método de validación para ADN
    private boolean isValidDNA(String[] dna) {
        if (dna == null || dna.length < 4) {
            return false;  // ADN nulo o con menos de 4 filas
        }

        int n = dna.length;
        for (String row : dna) {
            if (row.length() != n || !row.matches("[ATCG]+")) {
                return false;  // No es una matriz NxN o contiene caracteres no válidos
            }
        }
        return true;
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        StatsResponse statsResponse = dnaService.getStats();
        return ResponseEntity.ok(statsResponse);
    }

    @GetMapping("/")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("La API está funcionando correctamente");
    }
}







