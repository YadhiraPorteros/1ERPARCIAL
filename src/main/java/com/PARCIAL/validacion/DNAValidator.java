package com.PARCIAL.validacion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DNAValidator implements ConstraintValidator<ValidDNA, String[]> {

    @Override
    public void initialize(ValidDNA constraintAnnotation) {
    }

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        // Validación de que la matriz no sea nula o vacía
        if (dna == null || dna.length == 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("El ADN no puede ser nulo o vacío.")
                    .addConstraintViolation();
            return false;
        }

        int n = dna.length;

        // Verificar que la matriz sea de al menos 4x4
        if (n < 4) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("La matriz de ADN debe ser al menos de 4x4.")
                    .addConstraintViolation();
            return false;
        }

        // Validar que cada fila tenga la misma longitud que el número total de filas (matriz NxN)
        for (String row : dna) {
            if (row.length() != n || !row.matches("[ATCG]+")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("La matriz debe ser cuadrada y solo contener A, T, C, G.")
                        .addConstraintViolation();
                return false;  // La matriz no es cuadrada o contiene caracteres no válidos
            }
        }

        return true;  // El ADN es válido (matriz NxN con solo A, T, C, G y tamaño mínimo 4x4)
    }
}




