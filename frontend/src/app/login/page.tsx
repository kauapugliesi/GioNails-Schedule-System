"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { FormEvent, useEffect, useState } from "react";
import AuthShell from "@/components/AuthShell";
import { AlertaErro, Botao, Campo } from "@/components/ui";
import { useAuth } from "@/context/AuthContext";
import { mensagemDeErro } from "@/lib/erros";
import { mascararTelefone, somenteDigitos } from "@/lib/telefone";

export default function LoginPage() {
  const router = useRouter();
  const { usuario, carregando, entrar } = useAuth();
  const [telefone, setTelefone] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    if (!carregando && usuario) router.replace("/");
  }, [carregando, usuario, router]);

  async function aoEnviar(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      await entrar(somenteDigitos(telefone), senha);
      router.replace("/");
    } catch (err) {
      setErro(mensagemDeErro(err));
    } finally {
      setEnviando(false);
    }
  }

  return (
    <AuthShell
      titulo="Entrar"
      subtitulo="Use seu telefone e senha para ver e gerenciar seus horários."
      rodape={
        <>
          Ainda não tem conta?{" "}
          <Link href="/cadastro" className="font-semibold underline underline-offset-4">
            Criar conta
          </Link>
        </>
      }
    >
      <form onSubmit={aoEnviar} className="space-y-5" noValidate>
        <Campo
          id="telefone"
          label="Telefone"
          type="tel"
          inputMode="tel"
          autoComplete="tel-national"
          placeholder="(82) 99999-9999"
          value={telefone}
          onChange={(e) => setTelefone(mascararTelefone(e.target.value))}
          required
        />
        <Campo
          id="senha"
          label="Senha"
          type="password"
          autoComplete="current-password"
          value={senha}
          onChange={(e) => setSenha(e.target.value)}
          required
        />
        <AlertaErro mensagem={erro} />
        <Botao type="submit" carregando={enviando}>
          Entrar
        </Botao>
      </form>
    </AuthShell>
  );
}
