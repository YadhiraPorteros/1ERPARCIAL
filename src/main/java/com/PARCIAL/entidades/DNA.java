package com.PARCIAL.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ADN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DNA extends Base {

    @Column(name = "dna", columnDefinition = "TEXT")
    private String dna;

    private boolean isMutant;
}






