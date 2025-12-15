package com.multiclinicas.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.PacienteRepository;

import jakarta.transaction.Transactional;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.Paciente;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClinicaRepository clinicaRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository, PasswordEncoder passwordEncoder,
            ClinicaRepository clinicaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.clinicaRepository = clinicaRepository;
    }

    @Override
    public List<Paciente> findAll(Long clinicId) {
        return pacienteRepository.findAllByClinicaId(clinicId);
    }

    @Override
    public Paciente findById(Long id, Long clinicId) {
        return pacienteRepository.findByIdAndClinicaId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado nesta clínica"));
    }

    @Override
    public Paciente create(Long clinicId, Paciente paciente) {
        Clinica clinica = clinicaRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada"));
        paciente.setClinica(clinica);
        paciente.setSenhaHash(passwordEncoder.encode(paciente.getSenhaHash()));
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional
    public void delete(Long id, Long clinicId) {
        if (!pacienteRepository.existsByIdAndClinicaId(id, clinicId)) {
            throw new ResourceNotFoundException("Paciente não encontrado nesta clínica");
        }
        pacienteRepository.deleteByIdAndClinicaId(id, clinicId);
    }

    @Override
    @Transactional
    public Paciente update(Long id, Paciente novosDados, Long clinicId) {

        Paciente antigo = pacienteRepository.findByIdAndClinicaId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado nesta clínica"));

        antigo.setNome(novosDados.getNome());
        antigo.setEmail(novosDados.getEmail());
        antigo.setEndereco(novosDados.getEndereco());

        if (novosDados.getSenhaHash() != null && !novosDados.getSenhaHash().isBlank()) {
            antigo.setSenhaHash(passwordEncoder.encode(novosDados.getSenhaHash()));
        }

        return antigo;
    }

}
