package com.PARCIAL.servicios;

import com.PARCIAL.dtos.StatsResponse;
import com.PARCIAL.entidades.DNA;
import com.PARCIAL.repositorios.DNARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DNAService {

    @Autowired
    private DNARepository dnaRepository;

    @Transactional
    public void save(DNA dna) {
        dnaRepository.save(dna);
    }


    @Transactional
    public boolean isMutant( String[] dna) {
        String dnaString = String.join(",", dna); // Convertir el array de Strings a un solo String

        // Verificar si ya existe en la base de datos
        if (dnaRepository.findByDna(dnaString).isPresent()) {
            return dnaRepository.findByDna(dnaString).get().isMutant();
        }

        int sequencesFound = 0;
        int n = dna.length;

        boolean[][] counted = new boolean[n][n]; // Matriz para marcar posiciones contadas

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Verifica si hay secuencias en horizontal, vertical y diagonal
                if (!counted[i][j] && hasHorizontalSequence(dna, i, j, counted)) {
                    sequencesFound++;
                }
                if (!counted[i][j] && hasVerticalSequence(dna, i, j, counted)) {
                    sequencesFound++;
                }
                if (!counted[i][j] && hasDiagonalSequence(dna, i, j, counted)) {
                    sequencesFound++;
                }

                // Si ya se encontraron más de una secuencia, guardamos y retornamos
                if (sequencesFound > 1) {
                    save(new DNA(dnaString, true)); // Guardar como mutante
                    return true;
                }
            }
        }

        // Si no es mutante, guardamos como no mutante
        save(new DNA(dnaString, false));
        return false;
    }

    // Métodos auxiliares para verificar secuencias
    private boolean hasHorizontalSequence(String[] dna, int row, int col, boolean[][] counted) {
        int n = dna.length;
        if (col + 3 >= n) return false;  // Asegurarse de que no se salga del rango
        char base = dna[row].charAt(col);
        boolean found = base == dna[row].charAt(col + 1) && base == dna[row].charAt(col + 2) && base == dna[row].charAt(col + 3);

        if (found) {
            counted[row][col] = true;
            counted[row][col + 1] = true;
            counted[row][col + 2] = true;
            counted[row][col + 3] = true;
        }
        return found;
    }

    private boolean hasVerticalSequence(String[] dna, int row, int col, boolean[][] counted) {
        int n = dna.length;
        if (row + 3 >= n) return false;
        char base = dna[row].charAt(col);
        boolean found = base == dna[row + 1].charAt(col) && base == dna[row + 2].charAt(col) && base == dna[row + 3].charAt(col);

        if (found) {
            counted[row][col] = true;
            counted[row + 1][col] = true;
            counted[row + 2][col] = true;
            counted[row + 3][col] = true;
        }
        return found;
    }

    private boolean hasDiagonalSequence(String[] dna, int row, int col, boolean[][] counted) {
        return hasDiagonalRightSequence(dna, row, col, counted) || hasDiagonalLeftSequence(dna, row, col, counted);
    }

    private boolean hasDiagonalRightSequence(String[] dna, int row, int col, boolean[][] counted) {
        int n = dna.length;
        if (row + 3 >= n || col + 3 >= n) return false;
        char base = dna[row].charAt(col);
        boolean found = base == dna[row + 1].charAt(col + 1) && base == dna[row + 2].charAt(col + 2) && base == dna[row + 3].charAt(col + 3);

        if (found) {
            counted[row][col] = true;
            counted[row + 1][col + 1] = true;
            counted[row + 2][col + 2] = true;
            counted[row + 3][col + 3] = true;
        }
        return found;
    }

    private boolean hasDiagonalLeftSequence(String[] dna, int row, int col, boolean[][] counted) {
        int n = dna.length;
        if (row + 3 >= n || col - 3 < 0) return false;  // Asegurarse de que no se salga del rango
        char base = dna[row].charAt(col);
        boolean found = base == dna[row + 1].charAt(col - 1) && base == dna[row + 2].charAt(col - 2) && base == dna[row + 3].charAt(col - 3);

        if (found) {
            counted[row][col] = true;
            counted[row + 1][col - 1] = true;
            counted[row + 2][col - 2] = true;
            counted[row + 3][col - 3] = true;
        }
        return found;
    }


    @Transactional(readOnly = true)
    public StatsResponse getStats() {
        long countMutantDna = dnaRepository.countByIsMutant(true);
        long countHumanDna = dnaRepository.countByIsMutant(false);

        double ratio = countHumanDna > 0 ? (double) countMutantDna / countHumanDna : 0;

        return new StatsResponse(countMutantDna, countHumanDna, ratio);
    }

}

















