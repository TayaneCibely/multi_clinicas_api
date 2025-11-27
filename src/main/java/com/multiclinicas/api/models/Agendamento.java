package com.multiclinicas.api.models;

import com.multiclinicas.api.models.enums.StatusAgendamento;
import com.multiclinicas.api.models.enums.TipoPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
// Garante que não haja agendamentos com o mesmo médico, data e hora de início
@Table(name = "agendamentos", uniqueConstraints = @UniqueConstraint(columnNames = { "clinic_id", "medico_id",
        "data_consulta", "hora_inicio" }))
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinica clinica;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPagamento tipoPagamento = TipoPagamento.PARTICULAR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_saude_id")
    private PlanoSaude planoSaude; // Null se for Particular

    private String tokenAutorizacao; // Preenchido pela recepcionista
    // -------------------------------------------------

    private LocalDate dataConsulta;

    private LocalTime horaInicio;

    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}