"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { api, ApiError } from "@/lib/api";
import { clearToken, getToken, setToken } from "@/lib/auth-storage";
import type { AuthResponse, Usuario } from "@/lib/types";

interface AuthState {
  usuario: Usuario | null;
  /** true enquanto verificamos se já existe uma sessão salva */
  carregando: boolean;
  entrar: (telefone: string, senha: string) => Promise<Usuario>;
  cadastrar: (nome: string, telefone: string, senha: string) => Promise<Usuario>;
  sair: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    if (!getToken()) {
      setCarregando(false);
      return;
    }
    api<Usuario>("/api/auth/me")
      .then(setUsuario)
      .catch((err) => {
        // token expirado/inválido: descarta. Falha de rede: mantém o token para tentar depois.
        if (err instanceof ApiError && (err.status === 401 || err.status === 403)) clearToken();
      })
      .finally(() => setCarregando(false));
  }, []);

  const iniciarSessao = useCallback((resposta: AuthResponse) => {
    setToken(resposta.token);
    setUsuario(resposta.usuario);
    return resposta.usuario;
  }, []);

  const entrar = useCallback(
    async (telefone: string, senha: string) =>
      iniciarSessao(
        await api<AuthResponse>("/api/auth/login", {
          method: "POST",
          body: JSON.stringify({ telefone, senha }),
        }),
      ),
    [iniciarSessao],
  );

  const cadastrar = useCallback(
    async (nome: string, telefone: string, senha: string) =>
      iniciarSessao(
        await api<AuthResponse>("/api/auth/register", {
          method: "POST",
          body: JSON.stringify({ nome, telefone, senha }),
        }),
      ),
    [iniciarSessao],
  );

  const sair = useCallback(() => {
    clearToken();
    setUsuario(null);
  }, []);

  const valor = useMemo(
    () => ({ usuario, carregando, entrar, cadastrar, sair }),
    [usuario, carregando, entrar, cadastrar, sair],
  );

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthState {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth deve ser usado dentro de <AuthProvider>");
  return ctx;
}
