-- Etapa 1: base do banco.
-- btree_gist permitirá, na etapa de agendamento, uma EXCLUDE constraint que impede
-- intervalos de atendimento sobrepostos mesmo sob concorrência.
CREATE EXTENSION IF NOT EXISTS btree_gist;
