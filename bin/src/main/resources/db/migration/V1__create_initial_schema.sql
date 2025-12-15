-- Criação do schema inicial para o Sistema de Gerenciamento de Clínicas Médicas
-- Versão: 1.0
-- Descrição: Cria todas as tabelas base do sistema multi-tenant

-- =====================================================
-- 1. TABELA ENDERECOS
-- =====================================================
CREATE TABLE enderecos (
    id BIGSERIAL PRIMARY KEY,
    cep VARCHAR(9),
    logradouro VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(255),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    pais VARCHAR(50) DEFAULT 'Brasil'
);

-- =====================================================
-- 2. TABELA CLINICAS
-- =====================================================
CREATE TABLE clinicas (
    id BIGSERIAL PRIMARY KEY,
    nome_fantasia VARCHAR(255) NOT NULL,
    subdominio VARCHAR(255) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_clinicas_subdominio ON clinicas(subdominio);
CREATE INDEX idx_clinicas_ativo ON clinicas(ativo);

-- =====================================================
-- 3. TABELA USUARIOS_ADMIN
-- =====================================================
CREATE TABLE usuarios_admin (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14),
    telefone VARCHAR(20),
    telefone_secundario VARCHAR(20),
    email VARCHAR(255) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    endereco_id BIGINT,
    
    CONSTRAINT fk_usuarios_clinic FOREIGN KEY (clinic_id) REFERENCES clinicas(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuarios_endereco FOREIGN KEY (endereco_id) REFERENCES enderecos(id) ON DELETE SET NULL,
    CONSTRAINT uk_usuarios_clinic_email UNIQUE (clinic_id, email)
);

CREATE INDEX idx_usuarios_clinic_id ON usuarios_admin(clinic_id);
CREATE INDEX idx_usuarios_email ON usuarios_admin(email);

-- =====================================================
-- 4. TABELA ESPECIALIDADES
-- =====================================================
CREATE TABLE especialidades (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    
    CONSTRAINT fk_especialidades_clinic FOREIGN KEY (clinic_id) REFERENCES clinicas(id) ON DELETE CASCADE,
    CONSTRAINT uk_especialidades_clinic_nome UNIQUE (clinic_id, nome)
);

CREATE INDEX idx_especialidades_clinic_id ON especialidades(clinic_id);

-- =====================================================
-- 5. TABELA PACIENTES
-- =====================================================
CREATE TABLE pacientes (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    telefone VARCHAR(20),
    telefone_secundario VARCHAR(20),
    email VARCHAR(255),
    senha_hash VARCHAR(255),
    endereco_id BIGINT,
    
    CONSTRAINT fk_pacientes_clinic FOREIGN KEY (clinic_id) REFERENCES clinicas(id) ON DELETE CASCADE,
    CONSTRAINT fk_pacientes_endereco FOREIGN KEY (endereco_id) REFERENCES enderecos(id) ON DELETE SET NULL,
    CONSTRAINT uk_pacientes_clinic_cpf UNIQUE (clinic_id, cpf)
);

CREATE INDEX idx_pacientes_clinic_id ON pacientes(clinic_id);
CREATE INDEX idx_pacientes_cpf ON pacientes(cpf);
CREATE INDEX idx_pacientes_email ON pacientes(email);

-- =====================================================
-- 6. TABELA MEDICOS
-- =====================================================
CREATE TABLE medicos (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14),
    telefone VARCHAR(20),
    telefone_secundario VARCHAR(20),
    crm VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true,
    endereco_id BIGINT,
    
    CONSTRAINT fk_medicos_clinic FOREIGN KEY (clinic_id) REFERENCES clinicas(id) ON DELETE CASCADE,
    CONSTRAINT fk_medicos_endereco FOREIGN KEY (endereco_id) REFERENCES enderecos(id) ON DELETE SET NULL,
    CONSTRAINT uk_medicos_clinic_crm UNIQUE (clinic_id, crm)
);

CREATE INDEX idx_medicos_clinic_id ON medicos(clinic_id);
CREATE INDEX idx_medicos_crm ON medicos(crm);
CREATE INDEX idx_medicos_ativo ON medicos(ativo);

-- =====================================================
-- 7. TABELA MEDICO_ESPECIALIDADE
-- =====================================================
CREATE TABLE medico_especialidade (
    medico_id BIGINT NOT NULL,
    especialidades_id BIGINT NOT NULL,
    
    CONSTRAINT pk_medico_especialidade PRIMARY KEY (medico_id, especialidades_id),
    CONSTRAINT fk_medico_especialidade_medico FOREIGN KEY (medico_id) REFERENCES medicos(id) ON DELETE CASCADE,
    CONSTRAINT fk_medico_especialidade_especialidade FOREIGN KEY (especialidades_id) REFERENCES especialidades(id) ON DELETE CASCADE
);

CREATE INDEX idx_medico_especialidade_medico ON medico_especialidade(medico_id);
CREATE INDEX idx_medico_especialidade_especialidade ON medico_especialidade(especialidades_id);

-- =====================================================
-- 8. TABELA GRADES_HORARIO
-- =====================================================
CREATE TABLE grades_horario (
    id BIGSERIAL PRIMARY KEY,
    medico_id BIGINT NOT NULL,
    dia_semana INTEGER NOT NULL CHECK (dia_semana BETWEEN 0 AND 6),
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    duracao_consulta INTEGER NOT NULL,
    
    CONSTRAINT fk_grades_medico FOREIGN KEY (medico_id) REFERENCES medicos(id) ON DELETE CASCADE
);

CREATE INDEX idx_grades_medico_id ON grades_horario(medico_id);
CREATE INDEX idx_grades_dia_semana ON grades_horario(dia_semana);

-- =====================================================
-- 9. TABELA AGENDAMENTOS
-- =====================================================
CREATE TABLE agendamentos (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL,
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    data_consulta DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    observacoes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_agendamentos_clinic FOREIGN KEY (clinic_id) REFERENCES clinicas(id) ON DELETE CASCADE,
    CONSTRAINT fk_agendamentos_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE,
    CONSTRAINT fk_agendamentos_medico FOREIGN KEY (medico_id) REFERENCES medicos(id) ON DELETE CASCADE,
    CONSTRAINT uk_agendamentos_medico_data_hora UNIQUE (clinic_id, medico_id, data_consulta, hora_inicio)
);

CREATE INDEX idx_agendamentos_clinic_id ON agendamentos(clinic_id);
CREATE INDEX idx_agendamentos_paciente_id ON agendamentos(paciente_id);
CREATE INDEX idx_agendamentos_medico_id ON agendamentos(medico_id);
CREATE INDEX idx_agendamentos_data_consulta ON agendamentos(data_consulta);
CREATE INDEX idx_agendamentos_status ON agendamentos(status);
