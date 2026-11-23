"use client";

import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import { estiloBotao } from "@/components/ui";

export default function AuthBar() {
  const { usuario, carregando, sair } = useAuth();

  if (carregando) return <div className="mt-10 h-28" aria-hidden />;

  if (!usuario) {
    return (
      <div className="mt-10 flex w-full flex-col gap-3">
        <Link href="/login" className={estiloBotao("primario")}>
          Entrar
        </Link>
        <Link href="/cadastro" className={estiloBotao("secundario")}>
          Criar conta
        </Link>
      </div>
    );
  }

  const primeiroNome = usuario.nome.split(" ")[0];
  return (
    <div className="mt-10 w-full space-y-4">
      <p className="text-lg">
        Olá, <strong className="font-semibold">{primeiroNome}</strong>
      </p>
      <p className="text-sm font-light text-marrom-400">
        {usuario.perfil === "MANICURE"
          ? "Seu painel da agenda estará disponível em breve."
          : "Em breve você poderá agendar seu horário por aqui."}
      </p>
      <button type="button" onClick={sair} className={estiloBotao("secundario")}>
        Sair
      </button>
    </div>
  );
}
