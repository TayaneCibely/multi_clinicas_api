package com.multiclinicas.api.mappers;

import com.multiclinicas.api.dtos.PlanoSaudeCreateDTO;
import com.multiclinicas.api.dtos.PlanoSaudeDTO;
import com.multiclinicas.api.models.PlanoSaude;
import org.springframework.stereotype.Component;

@Component
public class PlanoSaudeMapper {

    public PlanoSaudeDTO toDTO(PlanoSaude planoSaude) {
        if (planoSaude == null) {
            return null;
        }
        return new PlanoSaudeDTO(
                planoSaude.getId(),
                planoSaude.getNome(),
                planoSaude.getAtivo());
    }

    public PlanoSaude toEntity(PlanoSaudeCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        PlanoSaude planoSaude = new PlanoSaude();
        planoSaude.setNome(dto.nome());
        planoSaude.setAtivo(dto.ativo() != null ? dto.ativo() : true);
        return planoSaude;
    }
}
