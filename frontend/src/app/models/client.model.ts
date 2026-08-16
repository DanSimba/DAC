import { ClientStatus } from "../enumeration/client-status";
import { UserType } from "../enumeration/user-type";
import { Address } from "./address.model";

export interface Client {
    id       : number;
    cpf      : string;
    name     : string;
    email    : string;
    password : string;
    salary   : number;
    address  : Address;
    status   : ClientStatus;
    type     : UserType;
}