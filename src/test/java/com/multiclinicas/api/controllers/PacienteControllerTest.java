package com.multiclinicas.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiclinicas.api.config.WebConfig;
import com.multiclinicas.api.config.tenant.TenantInterceptor;
import com.multiclinicas.api.dtos.CreateEnderecoDTO;
import com.multiclinicas.api.dtos.EnderecoDTO;
import com.multiclinicas.api.dtos.PacienteCreateDTO;
import com.multiclinicas.api.dtos.PacienteDTO;
import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.mappers.PacienteMapper;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.Paciente;
import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.services.ClinicaService;
import com.multiclinicas.api.services.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
// Import para o print()
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PacienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ WebConfig.class, TenantInterceptor.class })
class PacienteControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private PacienteService pacienteService;

        @MockitoBean
        private PacienteMapper pacienteMapper;

        @MockitoBean
        private ClinicaService clinicaService;

        @MockitoBean
        private ClinicaRepository clinicaRepository;

        private PacienteCreateDTO pacienteCreateDTO;
        private PacienteDTO pacienteDTO;
        private Paciente paciente;

        @BeforeEach
        void setup() {
                when(clinicaRepository.existsById(any())).thenReturn(true);

                CreateEnderecoDTO enderecoCreate = new CreateEnderecoDTO(
                                "00000-000", "rua 3", "1", "casa", "Centro", "Recife", "PE", "brasil");
                EnderecoDTO enderecoRetorno = new EnderecoDTO(
                                1L, "Rua Teste", "123", "Apto 1", "Centro", "Cidade", "SP", "00000-000", "brasil");

                pacienteCreateDTO = new PacienteCreateDTO(
                                "João Silva",
                                "joao@email.com",
                                "123.456.789-00",
                                "11999999999",
                                null,
                                "senhaForte123",
                                enderecoCreate);

                pacienteDTO = new PacienteDTO(
                                1L,
                                10L,
                                "João Silva",
                                "joao@email.com",
                                "123.456.789-00",
                                "11999999999",
                                null,
                                enderecoRetorno);

                paciente = new Paciente();
                paciente.setId(1L);
                paciente.setNome("João Silva");
        }

        @Test
        @DisplayName("Deve retornar lista de pacientes")
        void shouldReturnListOfPacientes() throws Exception {
                when(pacienteService.findAll()).thenReturn(List.of(paciente));
                when(pacienteMapper.toDto(paciente)).thenReturn(pacienteDTO);

                mockMvc.perform(get("/pacientes")
                                .header("X-Clinic-ID", 1L)) // <--- ADICIONADO HEADER
                                .andDo(print()) // <--- ADICIONADO DEBUG
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1L))
                                .andExpect(jsonPath("$[0].nome").value("João Silva"));
        }

        @Test
        @DisplayName("Deve retornar paciente por ID")
        void shouldReturnPacienteById() throws Exception {
                Long id = 1L;
                when(pacienteService.findById(id)).thenReturn(paciente);
                when(pacienteMapper.toDto(paciente)).thenReturn(pacienteDTO);

                mockMvc.perform(get("/pacientes/{id}", id)
                                .header("X-Clinic-ID", 1L)) // <--- ADICIONADO HEADER
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id))
                                .andExpect(jsonPath("$.email").value("joao@email.com"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando paciente não encontrado")
        void shouldReturn404WhenPacienteNotFound() throws Exception {
                Long id = 999L;
                doThrow(new ResourceNotFoundException("Paciente não encontrado")).when(pacienteService).findById(id);

                mockMvc.perform(get("/pacientes/{id}", id)
                                .header("X-Clinic-ID", 1L)) // <--- ADICIONADO HEADER
                                .andDo(print())
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve criar paciente com sucesso")
        void shouldCreatePacienteSuccessfully() throws Exception {
                Long clinicaId = 10L;
                Clinica clinicaMock = new Clinica();
                clinicaMock.setId(clinicaId);

                when(pacienteMapper.toEntity(any(PacienteCreateDTO.class))).thenReturn(paciente);
                when(clinicaService.findById(clinicaId)).thenReturn(clinicaMock);
                when(pacienteService.create(any(Paciente.class))).thenReturn(paciente);
                when(pacienteMapper.toDto(paciente)).thenReturn(pacienteDTO);

                mockMvc.perform(post("/pacientes")
                                .header("X-Clinic-ID", clinicaId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pacienteCreateDTO)))
                                .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.nome").value("João Silva"));
        }

        @Test
        @DisplayName("Deve retornar 400 ao criar paciente com dados inválidos")
        void shouldReturn400WhenCreatingInvalidPaciente() throws Exception {
                PacienteCreateDTO invalidDTO = new PacienteCreateDTO(
                                "", "email-invalido", "", "", null, null, null);

                mockMvc.perform(post("/pacientes")
                                .header("X-Clinic-ID", 10L) // <--- ADICIONADO HEADER (Importante mesmo na falha)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve atualizar paciente com sucesso")
        void shouldUpdatePacienteSuccessfully() throws Exception {
                Long id = 1L;

                when(pacienteMapper.toEntity(any(PacienteCreateDTO.class))).thenReturn(paciente);
                when(pacienteService.update(eq(id), any(Paciente.class))).thenReturn(paciente);
                when(pacienteMapper.toDto(paciente)).thenReturn(pacienteDTO);

                mockMvc.perform(put("/pacientes/{id}", id)
                                .header("X-Clinic-ID", 1L) // <--- ADICIONADO HEADER
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pacienteCreateDTO)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nome").value("João Silva"));
        }

        @Test
        @DisplayName("Deve deletar paciente com sucesso")
        void shouldDeletePacienteSuccessfully() throws Exception {
                Long id = 1L;

                mockMvc.perform(delete("/pacientes/{id}", id)
                                .header("X-Clinic-ID", 1L)) // <--- ADICIONADO HEADER
                                .andDo(print())
                                .andExpect(status().isNoContent());
        }
}