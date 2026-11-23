import { ApiError } from "./api";

const MSG_REDE = "Não foi possível conectar ao servidor. Verifique sua internet e tente novamente.";

export function mensagemDeErro(err: unknown): string {
  if (err instanceof ApiError) {
    return err.body?.message ?? `Algo deu errado (erro ${err.status}).`;
  }
  return MSG_REDE;
}

/** Mapeia as violações de validação do backend por nome de campo. */
export function errosPorCampo(err: unknown): Record<string, string> {
  if (err instanceof ApiError && err.body?.violations) {
    const mapa: Record<string, string> = {};
    for (const v of err.body.violations) {
      if (!mapa[v.field]) mapa[v.field] = v.message;
    }
    return mapa;
  }
  return {};
}
