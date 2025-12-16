package com.multiclinicas.api.services;

import com.multiclinicas.api.exceptions.BusinessException;
import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.Especialidade;

import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.EspecialidadeRepository;
import lombok.RequiredArgsConstructor;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EspecialidadeServiceImpl implements EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;
    private final ClinicaRepository clinicaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Especialidade> findAllByClinicId(Long clinicId) {
        return especialidadeRepository.findByClinicaId(clinicId);
    }

    @Override
    @Transactional(readOnly = true)
    public Especialidade findByIdAndClinicId(Long id, Long clinicId){
        return especialidadeRepository.findByIdAndClinicaId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidade não encontrada para essa clínica"));
    }

    @Override
    @Transactional
    public Especialidade create(Long clinicId, Especialidade especialidade) {
        //Verifica se a clinica existe primeiro
        Clinica clinica = clinicaRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinica não encontrada"));

        //Valida se já existe essa especialidade na clinica
        if(especialidadeRepository.existsByNomeAndClinicaId(especialidade.getNome(), clinicId)){
            throw new BusinessException(
                    "Já existe uma especialidade com o nome ' " + especialidade.getNome() + "' nesta clínica");
        }
        especialidade.setClinica(clinica);
        return especialidadeRepository.save(especialidade);
    }

    @Override
    @Transactional
    public Especialidade update(Long id, Long clinicId, Especialidade especialidadeAtualizada) {
        // Busca especialidade existente
        Especialidade especialidadeExistente = findByIdAndClinicId(id, clinicId);

        // Valida se o novo nome já existe (se foi alterado)
        if (!especialidadeExistente.getNome().equals(especialidadeAtualizada.getNome()) &&
                especialidadeRepository.existsByNomeAndClinicaId(especialidadeAtualizada.getNome(), clinicId)) {
            throw new BusinessException(
                    "Já existe uma especialidade com o nome'" + especialidadeAtualizada.getNome() + "' nesta clínica");
        }

        especialidadeExistente.setNome(especialidadeAtualizada.getNome());
        return especialidadeRepository.save(especialidadeExistente);
    }

    @Override
    @Transactional
    public void delete(Long id, Long clinicId) {
        Especialidade especialidade = findByIdAndClinicId(id, clinicId);
        especialidadeRepository.delete(especialidade);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Especialidade> findByIdsAndClinicId(Set<Long> ids, Long clinicId) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("É necessário informar pelo menos uma especialidade");
        }

        Set<Especialidade> especialidades = new HashSet<>();

        for (Long id : ids) {
            Especialidade especialidade = findByIdAndClinicId(id, clinicId);
            especialidades.add(especialidade);
        }

        return especialidades;
    }
}

