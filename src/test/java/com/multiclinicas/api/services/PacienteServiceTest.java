package com.multiclinicas.api.services;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Endereco;
import com.multiclinicas.api.models.Paciente;
import com.multiclinicas.api.repositories.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    @Test
    @DisplayName("Deve retornar todos os pacientes")
    void shouldReturnAllPacientes() {
        // Given
        Paciente p1 = new Paciente();
        p1.setId(1L);
        p1.setNome("João");

        Paciente p2 = new Paciente();
        p2.setId(2L);
        p2.setNome("Maria");

        when(pacienteRepository.findAll()).thenReturn(List.of(p1, p2));

        // When
        List<Paciente> result = pacienteService.findAll();

        // Then
        assertThat(result).hasSize(2);
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar paciente por ID quando existir")
    void shouldReturnPacienteByIdWhenExists() {
        // Given
        Long id = 1L;
        Paciente paciente = new Paciente();
        paciente.setId(id);
        paciente.setNome("João");

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(paciente));

        // When
        Paciente result = pacienteService.findById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(pacienteRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar paciente inexistente por ID")
    void shouldThrowExceptionWhenPacienteNotFoundById() {
        // Given
        Long id = 1L;
        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pacienteService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Não foi possível encontrar paciente com Id: " + id);
    }

    @Test
    @DisplayName("Deve criar paciente com sucesso")
    void shouldCreatePacienteSuccessfully() {
        // Given
        Paciente paciente = new Paciente();
        paciente.setNome("Novo Paciente");

        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        // When
        Paciente result = pacienteService.create(paciente);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Novo Paciente");
        verify(pacienteRepository).save(paciente);
    }

    @Test
    @DisplayName("Deve atualizar paciente com sucesso")
    void shouldUpdatePacienteSuccessfully() {
        // Given
        Long id = 1L;

        Paciente pacienteAntigo = new Paciente();
        pacienteAntigo.setId(id);
        pacienteAntigo.setNome("Nome Antigo");
        pacienteAntigo.setEmail("antigo@email.com");
        pacienteAntigo.setSenhaHash("senhaAntiga");

        Paciente novosDados = new Paciente();
        novosDados.setNome("Nome Novo");
        novosDados.setEmail("novo@email.com");
        novosDados.setSenhaHash("senhaNova");

        Endereco novoEndereco = new Endereco();
        novoEndereco.setCidade("Nova Cidade");
        novosDados.setEndereco(novoEndereco);

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(pacienteAntigo));

        // When
        Paciente result = pacienteService.update(id, novosDados);

        // Then
        assertThat(result.getNome()).isEqualTo("Nome Novo");
        assertThat(result.getEmail()).isEqualTo("novo@email.com");
        assertThat(result.getSenhaHash()).isEqualTo("senhaNova");
        assertThat(result.getEndereco().getCidade()).isEqualTo("Nova Cidade");

        verify(pacienteRepository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar paciente inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentPaciente() {
        // Given
        Long id = 1L;
        Paciente novosDados = new Paciente();

        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pacienteService.update(id, novosDados))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Não foi possível encontrar paciente com id: " + id);
    }

    @Test
    @DisplayName("Deve deletar paciente com sucesso")
    void shouldDeletePacienteSuccessfully() {
        // Given
        Long id = 1L;
        when(pacienteRepository.existsById(id)).thenReturn(true);

        // When
        pacienteService.delete(id);

        // Then
        verify(pacienteRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar paciente inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentPaciente() {
        // Given
        Long id = 1L;
        when(pacienteRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> pacienteService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("O paciente com Id " + id + " não existe");

        verify(pacienteRepository, never()).deleteById(any());
    }
}