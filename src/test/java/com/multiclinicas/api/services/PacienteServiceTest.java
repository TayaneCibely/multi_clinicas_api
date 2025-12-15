package com.multiclinicas.api.services;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.Endereco;
import com.multiclinicas.api.models.Paciente;
import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    private static final Long CLINIC_ID = 99L;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private ClinicaRepository clinicaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    @Test
    @DisplayName("Deve retornar todos os pacientes")
    void shouldReturnAllPacientes() {
        Paciente p1 = new Paciente();
        p1.setId(1L);
        Paciente p2 = new Paciente();
        p2.setId(2L);

        when(pacienteRepository.findAllByClinicaId(CLINIC_ID))
                .thenReturn(List.of(p1, p2));

        List<Paciente> result = pacienteService.findAll(CLINIC_ID);

        assertThat(result).hasSize(2);
        verify(pacienteRepository).findAllByClinicaId(CLINIC_ID);
    }

    @Test
    @DisplayName("Deve retornar paciente por ID quando existir")
    void shouldReturnPacienteByIdWhenExists() {
        Long id = 1L;
        Paciente paciente = new Paciente();
        paciente.setId(id);

        when(pacienteRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.of(paciente));

        Paciente result = pacienteService.findById(id, CLINIC_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(pacienteRepository).findByIdAndClinicaId(id, CLINIC_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar paciente inexistente por ID")
    void shouldThrowExceptionWhenPacienteNotFoundById() {
        Long id = 1L;

        when(pacienteRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.findById(id, CLINIC_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve criar paciente com sucesso")
    void shouldCreatePacienteSuccessfully() {
        Paciente paciente = new Paciente();
        paciente.setNome("Novo Paciente");
        paciente.setSenhaHash("123456");

        Clinica clinica = new Clinica();
        clinica.setId(CLINIC_ID);

        when(clinicaRepository.findById(CLINIC_ID)).thenReturn(Optional.of(clinica));
        when(passwordEncoder.encode("123456")).thenReturn("hashed_123456");
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Paciente result = pacienteService.create(CLINIC_ID, paciente);

        assertThat(result).isNotNull();
        assertThat(result.getSenhaHash()).isEqualTo("hashed_123456");
        assertThat(result.getClinica()).isEqualTo(clinica);

        verify(clinicaRepository).findById(CLINIC_ID);
        verify(passwordEncoder).encode("123456");
        verify(pacienteRepository).save(paciente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar paciente se a clínica não existir")
    void shouldThrowExceptionWhenCreatingPacienteIfClinicaNotFound() {
        Paciente paciente = new Paciente();
        when(clinicaRepository.findById(CLINIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.create(CLINIC_ID, paciente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Clínica não encontrada");
    }

    @Test
    @DisplayName("Deve atualizar paciente com sucesso")
    void shouldUpdatePacienteSuccessfully() {
        Long id = 1L;

        Paciente antigo = new Paciente();
        antigo.setId(id);
        antigo.setNome("Antigo Nome");
        antigo.setEmail("antigo@email");
        antigo.setSenhaHash("oldPassword");

        Paciente novosDados = new Paciente();
        novosDados.setNome("Novo Nome");
        novosDados.setEmail("novo@email");
        novosDados.setSenhaHash("novaSenha");

        Endereco novoEndereco = new Endereco();
        novoEndereco.setCidade("Nova Cidade");
        novosDados.setEndereco(novoEndereco);

        when(pacienteRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.of(antigo));

        when(passwordEncoder.encode("novaSenha"))
                .thenReturn("hashed_novaSenha");

        Paciente result = pacienteService.update(id, novosDados, CLINIC_ID);

        assertThat(result.getNome()).isEqualTo("Novo Nome");
        assertThat(result.getEmail()).isEqualTo("novo@email");
        assertThat(result.getSenhaHash()).isEqualTo("hashed_novaSenha");
        assertThat(result.getEndereco().getCidade()).isEqualTo("Nova Cidade");

        verify(passwordEncoder).encode("novaSenha");
        verify(pacienteRepository).findByIdAndClinicaId(id, CLINIC_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar paciente inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentPaciente() {
        Long id = 1L;
        Paciente novosDados = new Paciente();

        when(pacienteRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.update(id, novosDados, CLINIC_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve deletar paciente com sucesso")
    void shouldDeletePacienteSuccessfully() {
        Long id = 1L;

        when(pacienteRepository.existsByIdAndClinicaId(id, CLINIC_ID)).thenReturn(true);

        pacienteService.delete(id, CLINIC_ID);

        verify(pacienteRepository).deleteByIdAndClinicaId(id, CLINIC_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar paciente inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentPaciente() {
        Long id = 1L;

        when(pacienteRepository.existsByIdAndClinicaId(id, CLINIC_ID)).thenReturn(false);

        assertThatThrownBy(() -> pacienteService.delete(id, CLINIC_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(pacienteRepository, never()).deleteByIdAndClinicaId(any(), any());
    }
}