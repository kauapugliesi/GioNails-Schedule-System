export type Perfil = "CLIENTE" | "MANICURE";

export interface Usuario {
  id: string;
  nome: string;
  /** Somente dígitos com DDI, ex.: 5582999999999 */
  telefone: string;
  perfil: Perfil;
}

export interface AuthResponse {
  token: string;
  tipo: "Bearer";
  expiraEm: string;
  usuario: Usuario;
}
