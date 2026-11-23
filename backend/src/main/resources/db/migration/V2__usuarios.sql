-- Etapa 2: usuários (clientes e manicure). Telefone armazenado só com dígitos, com DDI 55.
CREATE TABLE usuarios (
    id         uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    nome       varchar(120) NOT NULL,
    telefone   varchar(13)  NOT NULL,
    senha_hash varchar(100) NOT NULL,
    perfil     varchar(20)  NOT NULL,
    ativo      boolean      NOT NULL DEFAULT true,
    criado_em  timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT uk_usuarios_telefone UNIQUE (telefone),
    CONSTRAINT ck_usuarios_perfil CHECK (perfil IN ('CLIENTE', 'MANICURE'))
);
