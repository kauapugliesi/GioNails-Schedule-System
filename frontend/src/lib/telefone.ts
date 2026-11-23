export function somenteDigitos(valor: string): string {
  return valor.replace(/\D/g, "");
}

/** Máscara enquanto digita: (82) 99999-9999 (aceita fixo de 10 dígitos). */
export function mascararTelefone(valor: string): string {
  const d = somenteDigitos(valor).slice(0, 11);
  if (d.length === 0) return "";
  if (d.length <= 2) return `(${d}`;
  if (d.length <= 6) return `(${d.slice(0, 2)}) ${d.slice(2)}`;
  if (d.length <= 10) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`;
  return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`;
}

/** Converte o telefone guardado (5582999999999) para exibição. */
export function exibirTelefone(armazenado: string): string {
  const d = somenteDigitos(armazenado);
  return mascararTelefone(d.startsWith("55") && d.length > 11 ? d.slice(2) : d);
}
