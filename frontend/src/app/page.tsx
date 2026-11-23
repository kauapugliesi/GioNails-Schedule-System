import AuthBar from "@/components/AuthBar";
import { api } from "@/lib/api";

export const dynamic = "force-dynamic";

async function apiOnline(): Promise<boolean> {
  try {
    const r = await api<{ status: string }>("/api/health");
    return r.status === "UP";
  } catch {
    return false;
  }
}

export default async function Home() {
  const online = await apiOnline();

  return (
    <main className="mx-auto flex min-h-dvh max-w-md flex-col items-center justify-center px-6 text-center">
      <h1 className="text-3xl leading-tight tracking-tight">
        STUDIO
        <br />
        GIOVANNA PUGLIESI
      </h1>
      <p className="mt-3 text-sm font-light uppercase tracking-[0.35em]">Agendamento</p>

      <p className="mt-10 text-sm font-light leading-relaxed text-marrom-400">
        Unhas em gel, sobrancelhas, lábios e cílios.
      </p>

      <AuthBar />

      <span className="mt-10 inline-flex items-center gap-2 rounded-full bg-bege-100 px-4 py-2 text-xs">
        <span
          className={`h-2 w-2 rounded-full ${online ? "bg-marrom" : "bg-bege-400"}`}
          aria-hidden
        />
        {online ? "Sistema conectado" : "API indisponível (inicie o backend)"}
      </span>

      <p className="mt-16 text-xs font-light uppercase tracking-widest">@studiogiovannapugliesi</p>
    </main>
  );
}
