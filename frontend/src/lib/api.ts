export const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export interface ApiErrorBody {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  violations?: { field: string; message: string }[];
}

export class ApiError extends Error {
  constructor(public readonly status: number, public readonly body?: ApiErrorBody) {
    super(body?.message ?? `Erro ${status}`);
  }
}

/** Cliente HTTP mínimo e tipado. O token JWT será anexado na etapa 2. */
export async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_URL}${path}`, {
    ...init,
    headers: { "Content-Type": "application/json", ...(init?.headers ?? {}) },
    cache: "no-store",
  });

  if (!res.ok) {
    let body: ApiErrorBody | undefined;
    try {
      body = (await res.json()) as ApiErrorBody;
    } catch {
      /* resposta sem corpo JSON */
    }
    throw new ApiError(res.status, body);
  }
  return (await res.json()) as T;
}
