package com.PARCIAL.dtos;

import com.PARCIAL.validacion.ValidDNA;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DNARequest {
    @ValidDNA
    private String[] dna;  // Esta propiedad almacena la secuencia de ADN
}



