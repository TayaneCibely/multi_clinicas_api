-- V2: Adiciona Planos de Saúde, Atualiza Schema de Médicos e Agendamentos

-- =====================================================
-- 1. NOVAS TABELAS E ALTERAÇÕES EM MEDICOS/GRADES
-- =====================================================

-- 1.1 Criar tabela de Planos de Saúde
CREATE TABLE planos_saude (
  id BIGSERIAL PRIMARY KEY,
  clinic_id BIGINT NOT NULL,
  nome VARCHAR(255) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_planos_clinic FOREIGN KEY (clinic_id)
      REFERENCES clinicas(id) ON DELETE CASCADE,
  CONSTRAINT uk_planos_clinic_nome UNIQUE (clinic_id, nome)
);

CREATE INDEX idx_planos_clinic_id ON planos_saude(clinic_id);

-- 1.2 Mover duracao_consulta de grades_horario para medicos
-- Primeiro adicionamos a coluna em medicos (Default 30 min para dados legados, se houver)
ALTER TABLE medicos
    ADD COLUMN duracao_consulta INTEGER NOT NULL DEFAULT 30;

-- Agora removemos a coluna da tabela grades_horario
ALTER TABLE grades_horario
DROP COLUMN duracao_consulta;


-- =====================================================
-- 2. ALTERAÇÕES EM AGENDAMENTOS
-- =====================================================

-- 2.1 Adicionar campos de controle financeiro/autorização
ALTER TABLE agendamentos
    ADD COLUMN tipo_pagamento VARCHAR(20) NOT NULL DEFAULT 'PARTICULAR', -- Enum: PARTICULAR, CONVENIO
ADD COLUMN plano_saude_id BIGINT,
ADD COLUMN token_autorizacao VARCHAR(100);

-- 2.2 Adicionar Constraint de Chave Estrangeira para o Plano
ALTER TABLE agendamentos
    ADD CONSTRAINT fk_agendamentos_plano FOREIGN KEY (plano_saude_id)
        REFERENCES planos_saude(id) ON DELETE SET NULL;

-- 2.3 Indexar para performance em relatórios financeiros futuros
CREATE INDEX idx_agendamentos_tipo_pagamento ON agendamentos(tipo_pagamento);