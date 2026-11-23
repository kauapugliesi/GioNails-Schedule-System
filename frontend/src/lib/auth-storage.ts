const CHAVE = "gionails.token";

/**
 * O token JWT fica no localStorage para manter a cliente conectada no celular.
 * Trade-off: é legível por JavaScript (risco em caso de XSS). Mitigações previstas na etapa 6
 * (Content-Security-Policy, sem HTML injetado). O token expira no backend (JWT_EXPIRATION_MINUTES).
 */
export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  try {
    return window.localStorage.getItem(CHAVE);
  } catch {
    return null;
  }
}

export function setToken(token: string): void {
  try {
    window.localStorage.setItem(CHAVE, token);
  } catch {
    /* armazenamento indisponível (modo privado) */
  }
}

export function clearToken(): void {
  try {
    window.localStorage.removeItem(CHAVE);
  } catch {
    /* ignora */
  }
}
