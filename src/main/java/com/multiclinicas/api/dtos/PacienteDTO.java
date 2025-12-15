package com.multiclinicas.api.dtos;

public record PacienteDTO(
                Long id,
                Long clinicaId,
                String nome,
                String email,
                String cpf,
                String telefone,
                String telefoneSecundario,
                EnderecoDTO endereco) {
}
