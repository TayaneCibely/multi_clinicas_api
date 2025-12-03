package com.multiclinicas.api.services;

import java.util.List;
import java.util.Set;

import com.multiclinicas.api.models.Medico;

public interface MedicoService {
	
	List<Medico> findAllByClinicId(Long clinicId);
	
	List<Medico> findAllActiveByClinicId(Long clinicId);
	
	Medico findByIdAndClinicId(Long id, Long clinicId);
	
	Medico create(Long clinicId, Medico medico, Set<Long> especialidadeIds);
	
	Medico update(Long id, Long clinicId, Medico medico, Set<Long> especialidadeIds);
	
	void delete(Long id, Long clinicId);

}