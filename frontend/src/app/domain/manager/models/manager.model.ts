import { ManagerStatus } from "../../../enumeration/manager-status";
import { UserType } from "../../../enumeration/user-type";

export interface Manager {
    id       : number;
    name     : string;
    cpf      : string;
    email    : string;
    password : string;
    status   : ManagerStatus;
    type     : UserType;
}
