package com.multiclinicas.api.services;

import java.util.List;

import com.multiclinicas.api.models.Paciente;

public interface PacienteService {
    List<Paciente> findAll(Long clinicId);

    Paciente findById(Long id, Long clinicId);

    Paciente create(Long clinicId, Paciente paciente);

    void delete(Long id, Long clinicId);

    Paciente update(Long id, Paciente novosDados, Long clinicId);
}
