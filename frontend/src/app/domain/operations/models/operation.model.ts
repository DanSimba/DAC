import { OperationSide } from "../../../enumeration/operation-side";

export interface OperationModel {
    type: 'operation';
    accNumber: string 
    side: OperationSide;
    value: number;
    datetime: string; //DATA E HORA
}
