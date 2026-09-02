import Decimal from 'decimal.js'
import { Address } from '../../address/models/address.model'

export interface CreateClient {
    cpf      : string;
    name     : string;
    phone    : string;
    email    : string;
    salary   : Decimal;
    address  : Address;
}