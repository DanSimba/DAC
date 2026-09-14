import Decimal from 'decimal.js'
import { Address } from '../../address/models/address.model'

export interface CreateClient {
    nome     : string;
    email    : string;
    cpf      : string;
    telefone : string;
    salario  : Decimal;
    endereco : Address;
}