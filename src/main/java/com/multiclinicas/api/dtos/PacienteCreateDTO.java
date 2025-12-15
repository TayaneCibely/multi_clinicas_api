package com.multiclinicas.api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PacienteCreateDTO(

        @NotBlank(message = "Nome é obrigatório") String nome,

        @Email(message = "Email inválido") @NotBlank(message = "Email é obrigatório") String email,

        @NotBlank(message = "CPF é obrigatório") @Size(min = 11, max = 14, message = "CPF deve ter entre 11 e 14 caracteres") String cpf,

        @NotBlank(message = "Telefone é obrigatório") String telefone,

        String telefoneSecundario,

        @NotBlank(message = "Senha é obrigatória") String senhaHash,
        @Valid @NotNull(message = "Endereço é obrigatório") CreateEnderecoDTO endereco) {
}
