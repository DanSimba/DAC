import { Client } from "./client.model";
import { Manager } from "./manager.model";

export interface Account {
    client   : Client;
    number   : string;
    balance  : number;
    manager  : Manager;
    createdAt : string;
}