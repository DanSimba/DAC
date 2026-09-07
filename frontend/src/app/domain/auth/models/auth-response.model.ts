// Importa os tipos de usuário do CLIENTE/MANAGER
import { UserType } from '../../../enumeration/user-type';

// Dados do perfil retornados pelo back depois da autenticação
export interface UsuarioAutenticado {
  cpf: string;
  nome: string;
  email: string;
}

// Estrutura da resposta do back depois da autenticação
export interface AuthResponse {
  auth: boolean;
  token: string;
  tipo: UserType | string;
  usuario: UsuarioAutenticado;
}