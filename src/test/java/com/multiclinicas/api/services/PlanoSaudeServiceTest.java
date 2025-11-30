package com.multiclinicas.api.services;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.PlanoSaude;
import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.PlanoSaudeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoSaudeServiceTest {

    @Mock
    private PlanoSaudeRepository planoSaudeRepository;

    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private PlanoSaudeServiceImpl planoSaudeService;

    @Test
    @DisplayName("Deve retornar lista de planos de saúde por clínica")
    void shouldReturnListOfPlanosByClinicId() {
        // Given
        Long clinicId = 1L;
        PlanoSaude plano = new PlanoSaude();
        plano.setId(1L);
        plano.setNome("Unimed");

        when(planoSaudeRepository.findByClinicaId(clinicId)).thenReturn(List.of(plano));

        // When
        List<PlanoSaude> result = planoSaudeService.findAllByClinicId(clinicId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Unimed", result.get(0).getNome());
        verify(planoSaudeRepository, times(1)).findByClinicaId(clinicId);
    }

    @Test
    @DisplayName("Deve retornar plano de saúde por ID e Clínica")
    void shouldReturnPlanoByIdAndClinicId() {
        // Given
        Long clinicId = 1L;
        Long id = 1L;
        Clinica clinica = new Clinica();
        clinica.setId(clinicId);

        PlanoSaude plano = new PlanoSaude();
        plano.setId(id);
        plano.setClinica(clinica);

        when(planoSaudeRepository.findById(id)).thenReturn(Optional.of(plano));

        // When
        PlanoSaude result = planoSaudeService.findByIdAndClinicId(id, clinicId);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(clinicId, result.getClinica().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando plano não pertence à clínica")
    void shouldThrowExceptionWhenPlanoDoesNotBelongToClinic() {
        // Given
        Long clinicId = 1L;
        Long otherClinicId = 2L;
        Long id = 1L;
        Clinica clinica = new Clinica();
        clinica.setId(otherClinicId);

        PlanoSaude plano = new PlanoSaude();
        plano.setId(id);
        plano.setClinica(clinica);

        when(planoSaudeRepository.findById(id)).thenReturn(Optional.of(plano));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> planoSaudeService.findByIdAndClinicId(id, clinicId));
    }

    @Test
    @DisplayName("Deve criar plano de saúde com sucesso")
    void shouldCreatePlanoSaudeSuccessfully() {
        // Given
        Long clinicId = 1L;
        Clinica clinica = new Clinica();
        clinica.setId(clinicId);

        PlanoSaude planoToCreate = new PlanoSaude();
        planoToCreate.setNome("Unimed");

        PlanoSaude savedPlano = new PlanoSaude();
        savedPlano.setId(1L);
        savedPlano.setNome("Unimed");
        savedPlano.setClinica(clinica);

        when(clinicaRepository.findById(clinicId)).thenReturn(Optional.of(clinica));
        when(planoSaudeRepository.save(any(PlanoSaude.class))).thenReturn(savedPlano);

        // When
        PlanoSaude result = planoSaudeService.create(clinicId, planoToCreate);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(clinicId, result.getClinica().getId());
        verify(clinicaRepository, times(1)).findById(clinicId);
        verify(planoSaudeRepository, times(1)).save(planoToCreate);
    }

    @Test
    @DisplayName("Deve atualizar plano de saúde com sucesso")
    void shouldUpdatePlanoSaudeSuccessfully() {
        // Given
        Long clinicId = 1L;
        Long id = 1L;
        Clinica clinica = new Clinica();
        clinica.setId(clinicId);

        PlanoSaude existingPlano = new PlanoSaude();
        existingPlano.setId(id);
        existingPlano.setNome("Unimed");
        existingPlano.setClinica(clinica);
        existingPlano.setAtivo(true);

        PlanoSaude updateData = new PlanoSaude();
        updateData.setNome("Unimed Atualizado");
        updateData.setAtivo(false);

        when(planoSaudeRepository.findById(id)).thenReturn(Optional.of(existingPlano));
        when(planoSaudeRepository.save(any(PlanoSaude.class))).thenReturn(existingPlano);

        // When
        PlanoSaude result = planoSaudeService.update(id, clinicId, updateData);

        // Then
        assertNotNull(result);
        assertEquals("Unimed Atualizado", result.getNome());
        assertFalse(result.getAtivo());
        verify(planoSaudeRepository, times(1)).save(existingPlano);
    }

    @Test
    @DisplayName("Deve deletar plano de saúde com sucesso")
    void shouldDeletePlanoSaudeSuccessfully() {
        // Given
        Long clinicId = 1L;
        Long id = 1L;
        Clinica clinica = new Clinica();
        clinica.setId(clinicId);

        PlanoSaude existingPlano = new PlanoSaude();
        existingPlano.setId(id);
        existingPlano.setClinica(clinica);

        when(planoSaudeRepository.findById(id)).thenReturn(Optional.of(existingPlano));

        // When
        planoSaudeService.delete(id, clinicId);

        // Then
        verify(planoSaudeRepository, times(1)).delete(existingPlano);
    }
}
