import { Client } from "../../client/models/client.model";
import { Manager } from "../../manager/models/manager.model";

export interface Account {
    client   : Client;
    number   : string;
    balance  : number;
    manager  : Manager;
    createdAt : string;
}