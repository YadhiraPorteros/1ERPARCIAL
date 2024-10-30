package com.PARCIAL.validacion;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DNAValidator.class) // La clase que válida
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDNA {
    String message() default "El ADN no es válido"; // Mensaje por defecto

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


