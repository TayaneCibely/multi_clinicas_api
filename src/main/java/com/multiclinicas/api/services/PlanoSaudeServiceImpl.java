package com.multiclinicas.api.services;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.PlanoSaude;
import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.PlanoSaudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanoSaudeServiceImpl implements PlanoSaudeService {

    private final PlanoSaudeRepository planoSaudeRepository;
    private final ClinicaRepository clinicaRepository;

    public PlanoSaudeServiceImpl(PlanoSaudeRepository planoSaudeRepository, ClinicaRepository clinicaRepository) {
        this.planoSaudeRepository = planoSaudeRepository;
        this.clinicaRepository = clinicaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanoSaude> findAllByClinicId(Long clinicId) {
        return planoSaudeRepository.findByClinicaId(clinicId);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanoSaude findByIdAndClinicId(Long id, Long clinicId) {
        return planoSaudeRepository.findById(id)
                .filter(p -> p.getClinica().getId().equals(clinicId))
                .orElseThrow(() -> new ResourceNotFoundException("Plano de saúde não encontrado"));
    }

    @Override
    @Transactional
    public PlanoSaude create(Long clinicId, PlanoSaude planoSaude) {
        Clinica clinica = clinicaRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada"));

        planoSaude.setClinica(clinica);
        return planoSaudeRepository.save(planoSaude);
    }

    @Override
    @Transactional
    public PlanoSaude update(Long id, Long clinicId, PlanoSaude planoSaudeAtualizado) {
        PlanoSaude planoExistente = findByIdAndClinicId(id, clinicId);

        planoExistente.setNome(planoSaudeAtualizado.getNome());
        planoExistente.setAtivo(planoSaudeAtualizado.getAtivo());

        return planoSaudeRepository.save(planoExistente);
    }

    @Override
    @Transactional
    public void delete(Long id, Long clinicId) {
        PlanoSaude planoExistente = findByIdAndClinicId(id, clinicId);
        planoSaudeRepository.delete(planoExistente);
    }
}
