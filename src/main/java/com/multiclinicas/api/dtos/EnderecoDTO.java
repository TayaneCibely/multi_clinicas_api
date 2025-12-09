package com.multiclinicas.api.dtos;

public record EnderecoDTO(
    Long id,
    String cep,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String pais
) {
}