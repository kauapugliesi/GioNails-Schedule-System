"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { FormEvent, useEffect, useState } from "react";
import AuthShell from "@/components/AuthShell";
import { AlertaErro, Botao, Campo } from "@/components/ui";
import { useAuth } from "@/context/AuthContext";
import { errosPorCampo, mensagemDeErro } from "@/lib/erros";
import { mascararTelefone, somenteDigitos } from "@/lib/telefone";

export default function CadastroPage() {
  const router = useRouter();
  const { usuario, carregando, cadastrar } = useAuth();
  const [nome, setNome] = useState("");
  const [telefone, setTelefone] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmacao, setConfirmacao] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [campos, setCampos] = useState<Record<string, string>>({});
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    if (!carregando && usuario) router.replace("/");
  }, [carregando, usuario, router]);

  async function aoEnviar(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErro(null);
    setCampos({});

    if (senha !== confirmacao) {
      setCampos({ confirmacao: "As senhas não conferem." });
      return;
    }

    setEnviando(true);
    try {
      await cadastrar(nome.trim(), somenteDigitos(telefone), senha);
      router.replace("/");
    } catch (err) {
      const porCampo = errosPorCampo(err);
      setCampos(porCampo);
      if (Object.keys(porCampo).length === 0) setErro(mensagemDeErro(err));
    } finally {
      setEnviando(false);
    }
  }

  return (
    <AuthShell
      titulo="Criar conta"
      subtitulo="Cadastre-se para agendar seus horários em poucos toques."
      rodape={
        <>
          Já tem conta?{" "}
          <Link href="/login" className="font-semibold underline underline-offset-4">
            Entrar
          </Link>
        </>
      }
    >
      <form onSubmit={aoEnviar} className="space-y-5" noValidate>
        <Campo
          id="nome"
          label="Nome completo"
          autoComplete="name"
          placeholder="Como está no seu documento"
          value={nome}
          onChange={(e) => setNome(e.target.value)}
          erro={campos.nome}
          required
        />
        <Campo
          id="telefone"
          label="Telefone"
          type="tel"
          inputMode="tel"
          autoComplete="tel-national"
          placeholder="(82) 99999-9999"
          value={telefone}
          onChange={(e) => setTelefone(mascararTelefone(e.target.value))}
          erro={campos.telefone}
          required
        />
        <Campo
          id="senha"
          label="Senha"
          type="password"
          autoComplete="new-password"
          placeholder="Mínimo de 8 caracteres"
          value={senha}
          onChange={(e) => setSenha(e.target.value)}
          erro={campos.senha}
          required
        />
        <Campo
          id="confirmacao"
          label="Confirmar senha"
          type="password"
          autoComplete="new-password"
          value={confirmacao}
          onChange={(e) => setConfirmacao(e.target.value)}
          erro={campos.confirmacao}
          required
        />
        <p className="text-xs font-light text-marrom-400">
          Seu nome é usado para liberar sua entrada na portaria do condomínio.
        </p>
        <AlertaErro mensagem={erro} />
        <Botao type="submit" carregando={enviando}>
          Criar conta
        </Botao>
      </form>
    </AuthShell>
  );
}
