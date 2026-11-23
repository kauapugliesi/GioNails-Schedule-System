import Link from "next/link";

export default function AuthShell({
  titulo,
  subtitulo,
  children,
  rodape,
}: {
  titulo: string;
  subtitulo?: string;
  children: React.ReactNode;
  rodape: React.ReactNode;
}) {
  return (
    <main className="mx-auto flex min-h-dvh w-full max-w-md flex-col px-6 py-10">
      <Link href="/" className="text-center font-serif text-sm font-bold uppercase tracking-wide">
        Studio Giovanna Pugliesi
      </Link>

      <div className="my-auto py-10">
        <h1 className="text-3xl">{titulo}</h1>
        {subtitulo && <p className="mt-2 text-sm font-light text-marrom-400">{subtitulo}</p>}
        <div className="mt-8 rounded-card bg-bege-100 p-6">{children}</div>
        <p className="mt-6 text-center text-sm">{rodape}</p>
      </div>
    </main>
  );
}
