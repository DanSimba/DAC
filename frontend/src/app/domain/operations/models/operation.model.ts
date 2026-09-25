import { OperationSide } from "../../../enumeration/operation-side";

export interface OperationModel {
    type: 'operation';
    acc_number: string 
    side: OperationSide;
    value: number;
    datetime: string; //DATA E HORA
}
