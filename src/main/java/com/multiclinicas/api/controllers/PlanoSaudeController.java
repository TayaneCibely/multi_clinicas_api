package com.multiclinicas.api.controllers;

import com.multiclinicas.api.dtos.PlanoSaudeCreateDTO;
import com.multiclinicas.api.dtos.PlanoSaudeDTO;
import com.multiclinicas.api.mappers.PlanoSaudeMapper;
import com.multiclinicas.api.models.PlanoSaude;
import com.multiclinicas.api.services.PlanoSaudeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planos-saude")
public class PlanoSaudeController {

    private final PlanoSaudeService planoSaudeService;
    private final PlanoSaudeMapper planoSaudeMapper;

    public PlanoSaudeController(PlanoSaudeService planoSaudeService, PlanoSaudeMapper planoSaudeMapper) {
        this.planoSaudeService = planoSaudeService;
        this.planoSaudeMapper = planoSaudeMapper;
    }

    @GetMapping
    public ResponseEntity<List<PlanoSaudeDTO>> findAll(@RequestHeader("X-Clinic-ID") Long clinicId) {
        List<PlanoSaude> planos = planoSaudeService.findAllByClinicId(clinicId);
        return ResponseEntity.ok(planos.stream()
                .map(planoSaudeMapper::toDTO)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanoSaudeDTO> findById(@PathVariable Long id, @RequestHeader("X-Clinic-ID") Long clinicId) {
        PlanoSaude plano = planoSaudeService.findByIdAndClinicId(id, clinicId);
        return ResponseEntity.ok(planoSaudeMapper.toDTO(plano));
    }

    @PostMapping
    public ResponseEntity<PlanoSaudeDTO> create(@RequestHeader("X-Clinic-ID") Long clinicId,
            @RequestBody @Valid PlanoSaudeCreateDTO dto) {
        PlanoSaude plano = planoSaudeMapper.toEntity(dto);
        PlanoSaude createdPlano = planoSaudeService.create(clinicId, plano);
        return ResponseEntity.status(HttpStatus.CREATED).body(planoSaudeMapper.toDTO(createdPlano));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanoSaudeDTO> update(@PathVariable Long id,
            @RequestHeader("X-Clinic-ID") Long clinicId,
            @RequestBody @Valid PlanoSaudeCreateDTO dto) {
        PlanoSaude plano = planoSaudeMapper.toEntity(dto);
        PlanoSaude updatedPlano = planoSaudeService.update(id, clinicId, plano);
        return ResponseEntity.ok(planoSaudeMapper.toDTO(updatedPlano));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-Clinic-ID") Long clinicId) {
        planoSaudeService.delete(id, clinicId);
        return ResponseEntity.noContent().build();
    }
}
