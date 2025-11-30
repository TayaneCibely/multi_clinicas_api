package com.multiclinicas.api.dtos;

import jakarta.validation.constraints.NotBlank;

public record PlanoSaudeCreateDTO(
        @NotBlank(message = "O nome é obrigatório") String nome,
        Boolean ativo) {
}
