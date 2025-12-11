package com.multiclinicas.api.services;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.Endereco;
import com.multiclinicas.api.models.UsuarioAdmin;
import com.multiclinicas.api.models.enums.Role;
import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.UsuarioAdminRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioAdminServiceTest {

    @Mock
    private UsuarioAdminRepository usuarioAdminRepository;

    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private UsuarioAdminServiceImpl usuarioAdminService;

    private UsuarioAdmin usuarioAdmin;
    private Clinica clinica;
    private Endereco endereco;
    private final Long clinicId = 1L;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        clinica = new Clinica();
        clinica.setId(clinicId);
        clinica.setNomeFantasia("Clinica Teste");

        endereco = new Endereco();
        endereco.setId(10L);
        endereco.setLogradouro("Rua Antiga");
        endereco.setCep("00000-000");

        usuarioAdmin = new UsuarioAdmin();
        usuarioAdmin.setId(userId);
        usuarioAdmin.setNome("Admin Teste");
        usuarioAdmin.setEmail("admin@teste.com");
        usuarioAdmin.setClinica(clinica);
        usuarioAdmin.setEndereco(endereco);
    }

    @Test
    @DisplayName("Deve retornar lista de usuários admin por ID da clínica")
    void shouldFindAllByClinicId() {
        when(usuarioAdminRepository.findAllByClinicaId(clinicId)).thenReturn(List.of(usuarioAdmin));

        List<UsuarioAdmin> result = usuarioAdminService.findAllByClinicId(clinicId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getId());
        verify(usuarioAdminRepository).findAllByClinicaId(clinicId);
    }

    @Test
    @DisplayName("Deve retornar usuário admin por ID e ID da clínica")
    void shouldFindByIdAndClinicId() {
        when(usuarioAdminRepository.findByIdAndClinicaId(userId, clinicId)).thenReturn(Optional.of(usuarioAdmin));

        UsuarioAdmin result = usuarioAdminService.findByIdAndClinicId(userId, clinicId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(usuarioAdminRepository).findByIdAndClinicaId(userId, clinicId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void shouldThrowNotFoundWhenFindById() {
        when(usuarioAdminRepository.findByIdAndClinicaId(99L, clinicId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> usuarioAdminService.findByIdAndClinicId(99L, clinicId));
    }


    @Test
    @DisplayName("Deve criar um usuário admin com sucesso")
    void shouldCreateUsuarioAdmin() {
        UsuarioAdmin newUser = new UsuarioAdmin();
        newUser.setNome("Novo Admin");

        when(clinicaRepository.findById(clinicId)).thenReturn(Optional.of(clinica));
        when(usuarioAdminRepository.save(any(UsuarioAdmin.class))).thenAnswer(i -> {
            UsuarioAdmin saved = i.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        UsuarioAdmin result = usuarioAdminService.createUsuarioAdmin(clinicId, newUser);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(clinicId, result.getClinica().getId());
        verify(clinicaRepository).findById(clinicId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário em clínica inexistente")
    void shouldThrowNotFoundWhenCreateInNonExistentClinic() {
        when(clinicaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> usuarioAdminService.createUsuarioAdmin(99L, new UsuarioAdmin()));
        verify(usuarioAdminRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve atualizar dados básicos do usuário e manter ID e Clínica")
    void shouldUpdateBasicUserData() {
        UsuarioAdmin updateDTO = new UsuarioAdmin();
        updateDTO.setNome("Nome Atualizado");
        updateDTO.setEmail("novo@email.com");
        updateDTO.setRole(Role.ADMIN);
        
        when(usuarioAdminRepository.findByIdAndClinicaId(userId, clinicId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioAdminRepository.save(any(UsuarioAdmin.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioAdmin result = usuarioAdminService.updateUsuarioAdmin(userId, clinicId, updateDTO);

        assertEquals("Nome Atualizado", result.getNome());
        assertEquals("novo@email.com", result.getEmail());

        assertEquals(userId, result.getId());
        assertEquals(clinicId, result.getClinica().getId());
    }

    @Test
    @DisplayName("Deve atualizar campos do endereço existente sem alterar o ID do endereço")
    void shouldUpdateExistingAddressFields() {
        
        Endereco novoEnderecoDados = new Endereco();
        novoEnderecoDados.setLogradouro("Rua Nova");
        novoEnderecoDados.setCep("99999-999");
        
        UsuarioAdmin updateDTO = new UsuarioAdmin();
        updateDTO.setNome("Admin");
        updateDTO.setEndereco(novoEnderecoDados);

        when(usuarioAdminRepository.findByIdAndClinicaId(userId, clinicId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioAdminRepository.save(any(UsuarioAdmin.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioAdmin result = usuarioAdminService.updateUsuarioAdmin(userId, clinicId, updateDTO);

        assertNotNull(result.getEndereco());
        assertEquals("Rua Nova", result.getEndereco().getLogradouro());
        assertEquals("99999-999", result.getEndereco().getCep());
        assertEquals(10L, result.getEndereco().getId()); 
    }

    @Test
    @DisplayName("Deve criar novo endereço se usuário não tinha um anteriormente")
    void shouldCreateAddressWhenUserHadNone() {
        usuarioAdmin.setEndereco(null);
        
        Endereco novoEnderecoDados = new Endereco();
        novoEnderecoDados.setLogradouro("Primeira Rua");
        novoEnderecoDados.setCep("11111-111");
        
        UsuarioAdmin updateDTO = new UsuarioAdmin();
        updateDTO.setNome("Admin");
        updateDTO.setEndereco(novoEnderecoDados);

        when(usuarioAdminRepository.findByIdAndClinicaId(userId, clinicId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioAdminRepository.save(any(UsuarioAdmin.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioAdmin result = usuarioAdminService.updateUsuarioAdmin(userId, clinicId, updateDTO);

        assertNotNull(result.getEndereco());
        assertEquals("Primeira Rua", result.getEndereco().getLogradouro());
    }

    @Test
    @DisplayName("Deve manter o endereço antigo se o DTO não trouxer endereço novo (Null Safety)")
    void shouldKeepOldAddressWhenInputAddressIsNull() {
        UsuarioAdmin updateDTO = new UsuarioAdmin();
        updateDTO.setNome("Admin Mudou Só Nome");
        updateDTO.setEndereco(null);

        when(usuarioAdminRepository.findByIdAndClinicaId(userId, clinicId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioAdminRepository.save(any(UsuarioAdmin.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioAdmin result = usuarioAdminService.updateUsuarioAdmin(userId, clinicId, updateDTO);

        assertNotNull(result.getEndereco());
        assertEquals("Rua Antiga", result.getEndereco().getLogradouro());
    }

    @Test
    @DisplayName("Deve atualizar senha apenas se fornecida e não vazia")
    void shouldUpdatePasswordOnlyIfProvided() {
        UsuarioAdmin updateDTO = new UsuarioAdmin();
        updateDTO.setSenhaHash("novaSenhaHash");

        when(usuarioAdminRepository.findByIdAndClinicaId(userId, clinicId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioAdminRepository.save(any(UsuarioAdmin.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioAdmin result = usuarioAdminService.updateUsuarioAdmin(userId, clinicId, updateDTO);
        
        assertEquals("novaSenhaHash", result.getSenhaHash());
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUsuarioAdmin() {
        when(usuarioAdminRepository.findByIdAndClinicaId(userId, clinicId)).thenReturn(Optional.of(usuarioAdmin));
        doNothing().when(usuarioAdminRepository).delete(usuarioAdmin);

        usuarioAdminService.delete(userId, clinicId);

        verify(usuarioAdminRepository).delete(usuarioAdmin);
    }
}