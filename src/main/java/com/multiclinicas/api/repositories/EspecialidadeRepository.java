package com.multiclinicas.api.repositories;

import com.multiclinicas.api.models.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialidadeRepository extends JpaRepository <Especialidade, Long> {

    //Buscar as especialidades presentes em determinada clinica
    List<Especialidade> findByClinicaId(Long clinicaId);

    //Buscar a especialidade por id e id da clínica
    Optional<Especialidade> findByIdAndClinicaId(Long id, Long clinicaId);

    //Verificar se  já possui uma especialidade com o mesmo nome na clínica
    boolean existsByNomeAndClinicaId(String nome, Long clinicaId);

}

