package com.multiclinicas.api.repositories;

import com.multiclinicas.api.models.PlanoSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanoSaudeRepository extends JpaRepository<PlanoSaude, Long> {
    List<PlanoSaude> findByClinicaId(Long clinicId);

    List<PlanoSaude> findByClinicaIdAndAtivoTrue(Long clinicId);
}
