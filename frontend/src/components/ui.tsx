import type { ButtonHTMLAttributes, InputHTMLAttributes } from "react";

type Variante = "primario" | "secundario";

export function estiloBotao(variante: Variante = "primario"): string {
  const base =
    "inline-flex h-12 w-full items-center justify-center rounded-xl px-6 text-base font-medium " +
    "transition focus:outline-none focus-visible:ring-2 focus-visible:ring-marrom/40 disabled:opacity-60";
  return variante === "primario"
    ? `${base} bg-marrom text-bege-50 hover:bg-marrom-700 active:bg-marrom-800`
    : `${base} border border-marrom text-marrom hover:bg-bege-100`;
}

export function Botao({
  variante = "primario",
  carregando,
  children,
  className = "",
  ...props
}: ButtonHTMLAttributes<HTMLButtonElement> & { variante?: Variante; carregando?: boolean }) {
  return (
    <button className={`${estiloBotao(variante)} ${className}`} disabled={carregando || props.disabled} {...props}>
      {carregando ? "Aguarde..." : children}
    </button>
  );
}

export function Campo({
  label,
  erro,
  id,
  ...props
}: { label: string; erro?: string; id: string } & InputHTMLAttributes<HTMLInputElement>) {
  return (
    <div>
      <label htmlFor={id} className="mb-1.5 block text-sm font-medium">
        {label}
      </label>
      <input
        id={id}
        aria-invalid={erro ? true : undefined}
        aria-describedby={erro ? `${id}-erro` : undefined}
        className={`h-12 w-full rounded-xl border bg-bege-50 px-4 text-base outline-none transition
          placeholder:text-marrom-300 focus:ring-2 focus:ring-marrom/20
          ${erro ? "border-erro focus:border-erro" : "border-bege-300 focus:border-marrom"}`}
        {...props}
      />
      {erro && (
        <p id={`${id}-erro`} className="mt-1.5 text-sm text-erro">
          {erro}
        </p>
      )}
    </div>
  );
}

export function AlertaErro({ mensagem }: { mensagem: string | null }) {
  if (!mensagem) return null;
  return (
    <p role="alert" className="rounded-xl border border-erro/30 bg-erro/10 px-4 py-3 text-sm text-erro">
      {mensagem}
    </p>
  );
}
