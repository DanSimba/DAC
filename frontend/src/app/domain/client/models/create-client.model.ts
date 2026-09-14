import Decimal from 'decimal.js'
import { Address } from '../../address/models/address.model'

//Usado internamente no formulário para cálculos com decimal.js
export interface CreateClient {
    nome     : string;
    email    : string;
    cpf      : string; // numeros com formatação
    telefone : string;
    salario  : Decimal;
    endereco : Address;

}

//Usado para enviar pro backend
export interface CreateClientPayload {
    nome     : string;
    email    : string;
    cpf      : string; // só numeros
    telefone : string;
    salario  : string; // fortamado como string com 2 casas decimais, tipo "1000.00"
    endereco : Address;
    
}