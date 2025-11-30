package com.multiclinicas.api.services;

import com.multiclinicas.api.models.PlanoSaude;

import java.util.List;

public interface PlanoSaudeService {
    List<PlanoSaude> findAllByClinicId(Long clinicId);

    PlanoSaude findByIdAndClinicId(Long id, Long clinicId);

    PlanoSaude create(Long clinicId, PlanoSaude planoSaude);

    PlanoSaude update(Long id, Long clinicId, PlanoSaude planoSaude);

    void delete(Long id, Long clinicId);
}
