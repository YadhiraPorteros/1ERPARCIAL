package com.PARCIAL.servicios;

import com.PARCIAL.dtos.StatsResponse;
import com.PARCIAL.entidades.DNA;
import com.PARCIAL.repositorios.DNARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@EnableAsync
public class DNAService {

    @Autowired
    private DNARepository dnaRepository;

    // Caché para almacenar resultados de secuencias de ADN ya procesadas
    private ConcurrentHashMap<String, Boolean> cache = new ConcurrentHashMap<>();

    @Transactional
    public void save(DNA dna) {
        dnaRepository.save(dna);
    }

    @Transactional
    public boolean isMutant( String[] dna) {
        String dnaString = String.join(",", dna); // Convertir el array de Strings a un solo String

        // Verificar si la secuencia ya está en el caché
        if (cache.containsKey(dnaString)) {
            return cache.get(dnaString); // Devuelve el resultado almacenado
        }

        // Verificar si ya existe en la base de datos
        if (dnaRepository.findByDna(dnaString).isPresent()) {
            boolean isMutant = dnaRepository.findByDna(dnaString).get().isMutant();
            cache.put(dnaString, isMutant); // Actualiza el caché
            return isMutant;
        }

        int sequencesFound = 0;
        int n = dna.length;
        boolean[][] counted = new boolean[n][n]; // Matriz para marcar posiciones contadas

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (sequencesFound > 1) {  // Si encontramos más de una secuencia, termina la búsqueda
                    cache.put(dnaString, true);  // Guarda en el caché como mutante
                    save(new DNA(dnaString, true)); // Guarda en la base de datos
                    return true;
                }

                // Verificar secuencias en horizontal, vertical y diagonal
                if (!counted[i][j] && hasHorizontalSequence(dna, i, j, counted)) sequencesFound++;
                if (!counted[i][j] && hasVerticalSequence(dna, i, j, counted)) sequencesFound++;
                if (!counted[i][j] && hasDiagonalSequence(dna, i, j, counted)) sequencesFound++;
            }
        }

        // Si no es mutante, guarda en la base de datos y en el caché
        cache.put(dnaString, false);
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

    @Cacheable("statsCache")
    @Transactional(readOnly = true)
    public StatsResponse getStats() {
        long countMutantDna = dnaRepository.countByIsMutant(true);
        long countHumanDna = dnaRepository.countByIsMutant(false);
        double ratio = countHumanDna > 0 ? (double) countMutantDna / countHumanDna : 0;

        return new StatsResponse(countMutantDna, countHumanDna, ratio);
    }

    @Async
    public CompletableFuture<Boolean> checkIfMutantAsync(String[] dna) {
        boolean isMutant = isMutant(dna);
        return CompletableFuture.completedFuture(isMutant);
    }
}


















