package com.multiclinicas.api.mappers;

import org.springframework.stereotype.Component;

import com.multiclinicas.api.dtos.PacienteDTO;
import com.multiclinicas.api.dtos.PacienteCreateDTO;
import com.multiclinicas.api.models.Paciente;

@Component
public class PacienteMapper {

    private final EnderecoMapper enderecoMapper;

    public PacienteMapper(EnderecoMapper enderecoMapper) {
        this.enderecoMapper = enderecoMapper;
    }

    public PacienteDTO toDto(Paciente paciente) {
        if (paciente == null)
            return null;

        return new PacienteDTO(
                paciente.getId(),
                paciente.getClinica().getId(),
                paciente.getNome(),
                paciente.getEmail(),
                paciente.getCpf(),
                paciente.getTelefone(),
                paciente.getTelefoneSecundario(),
                enderecoMapper.toDto(paciente.getEndereco()));
    }

    public Paciente toEntity(PacienteCreateDTO dto) {
        if (dto == null)
            return null;

        Paciente paciente = new Paciente();
        paciente.setNome(dto.nome());
        paciente.setEmail(dto.email());
        paciente.setCpf(dto.cpf());
        paciente.setTelefone(dto.telefone());
        paciente.setTelefoneSecundario(dto.telefoneSecundario());
        paciente.setSenhaHash(dto.senhaHash());
        paciente.setEndereco(enderecoMapper.toEntity(dto.endereco()));

        return paciente;
    }
}
