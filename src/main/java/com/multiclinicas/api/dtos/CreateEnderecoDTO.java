package com.multiclinicas.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEnderecoDTO(

                @NotBlank(message = "CEP é obrigatório") @Size(min = 8, max = 9, message = "CEP deve ter entre 8 e 9 caracteres") String cep,

                @NotBlank(message = "Logradouro é obrigatório") String logradouro,

                @NotBlank(message = "Número é obrigatório") String numero,

                String complemento,

                @NotBlank(message = "Bairro é obrigatório") String bairro,

                @NotBlank(message = "Cidade é obrigatório") String cidade,

                @NotBlank(message = "Estado é obrigatório") @Size(min = 2, max = 2, message = "Estado deve ter 2 letras (UF)") String estado,

                String pais) {
}
